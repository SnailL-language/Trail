package io.github.snaill.bytecode;

import io.github.snaill.ast.*;
import io.github.snaill.exception.FailedCheckException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс для записи байткода в поток.
 * Обеспечивает корректную запись всех компонентов байткода.
 */
public class BytecodeWriter {
    private final BytecodeContext context;
    private final OutputStream out;

    public BytecodeWriter(BytecodeContext context, OutputStream out) {
        this.context = context;
        this.out = out;
    }

    /**
     * Записывает байткод в поток
     */
    public void write(Scope program) throws IOException, FailedCheckException {
        // Записываем магическое число
        out.write(BytecodeConstants.MAGIC_NUMBER);
        
        // Записываем версию
        BytecodeUtils.writeU16(out, BytecodeConstants.CURRENT_VERSION);
        
        // Индекс main (если есть)
        int mainIdx = context.getFunctionIndex("main");
        BytecodeUtils.writeU16(out, mainIdx >= 0 ? mainIdx : 0xFFFF);
        
        // Записываем константы
        writeConstants();
        
        // Записываем глобальные переменные
        writeGlobalVariables();
        
        // Записываем функции
        writeFunctions();
        
        // Записываем глобальный байткод
        writeGlobalBytecode(program);
    }

    /**
     * Записывает пул констант
     */
    private void writeConstants() throws IOException {
        var constants = context.getConstants();
        BytecodeUtils.writeU16(out, constants.size());
        for (Object c : constants) {
            if (c instanceof Long l) {
                out.write(BytecodeConstants.TypeId.I32);
                BytecodeUtils.writeI32(out, l.intValue());
            } else if (c instanceof String s) {
                out.write(BytecodeConstants.TypeId.STRING);
                BytecodeUtils.writeU16(out, s.getBytes(java.nio.charset.StandardCharsets.UTF_8).length);
                out.write(s.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            }
        }
    }

    /**
     * Записывает секцию глобальных переменных в байткод.
     * Для каждой переменной записывается:
     * - Длина имени (1 байт)
     * - Имя (UTF-8 байты)
     * - Тип (1 байт)
     * - Для массивов: тип элементов (1 байт) и размер (4 байта)
     */
    private void writeGlobalVariables() throws IOException {
        var globals = context.getGlobalVariables();
        BytecodeUtils.writeU16(out, globals.size());
        
        for (String name : globals) {
            byte[] nameBytes = name.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            out.write((byte) nameBytes.length);
            out.write(nameBytes);
            
            // Поиск объявления глобальной переменной
            VariableDeclaration varDecl = findGlobalVariableDeclaration(name);
            
            if (varDecl != null) {
                Type type = varDecl.getType();
                byte typeId = getTypeId(type);
                out.write(typeId);
                
                // Для массивов дополнительно записываем тип элементов и размер
                if (typeId == BytecodeConstants.TypeId.ARRAY && type instanceof ArrayType arrayType) {
                    out.write(getTypeId(arrayType.getElementType()));
                    long size = arrayType.getSize().getValue();
                    BytecodeUtils.writeI32(out, (int) size);
                }
            } else {
                // Если по каким-то причинам переменная не найдена, используем тип по умолчанию
                byte typeId = BytecodeConstants.TypeId.I32;
                out.write(typeId);
                System.err.println("Warning: Тип глобальной переменной '" + name + "' неизвестен, используется i32");
            }
        }
    }
    
    /**
     * Находит объявление глобальной переменной по имени
     * @param name Имя переменной
     * @return Объявление переменной или null, если не найдено
     */
    private VariableDeclaration findGlobalVariableDeclaration(String name) {
        List<Statement> globalStmts = context.getGlobalStatements();
        if (globalStmts == null) {
            return null;
        }
        
        for (Statement stmt : globalStmts) {
            if (stmt instanceof VariableDeclaration varDecl && varDecl.getName().equals(name)) {
                return varDecl;
            }
        }
        
        return null;
    }

    /**
     * Записывает функции
     */
    private void writeFunctions() throws IOException, FailedCheckException {
        var functions = context.getFunctions();
        // Проверка на пустой список функций
        if (functions == null || functions.isEmpty()) {
            BytecodeUtils.writeU16(out, 0); // Если функций нет, записываем 0
            return;
        }
        
        BytecodeUtils.writeU16(out, functions.size());
        
        for (FunctionDeclaration func : functions) {
            // Проверка на null имя функции
            String funcName = func.getName() != null ? func.getName() : "";
            if (funcName.isEmpty()) {
                System.err.println("Предупреждение: обнаружена функция с пустым именем");
                funcName = "unnamed_func";
            }
            
            byte[] nameBytes = funcName.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            
            // Проверка на слишком длинное имя функции (макс. 255 байт)
            if (nameBytes.length > 255) {
                System.err.println("Предупреждение: имя функции " + funcName + " слишком длинное, будет усечено");
                byte[] truncated = new byte[255];
                System.arraycopy(nameBytes, 0, truncated, 0, 255);
                nameBytes = truncated;
            }
            
            out.write((byte) nameBytes.length);
            out.write(nameBytes);
            out.write((byte) func.getParameters().size());
            out.write(getTypeId(func.getReturnType()));
            
            // Записываем количество локальных переменных (включая параметры)
            // Согласно спецификации, должны учитываться все локальные переменные, а не только параметры
            int localVarCount = countLocalVariables(func);
            BytecodeUtils.writeU16(out, localVarCount);
            
            // Генерируем байткод функции
            ByteArrayOutputStream funcOut = new ByteArrayOutputStream();
            func.getBody().emitBytecode(funcOut, context);
            byte[] code = funcOut.toByteArray();
            
            // Проверка размера байткода
            if (code.length > Integer.MAX_VALUE - 10) { // Защита от переполнения
                throw new IOException("Слишком большой размер байткода функции: " + funcName);
            }
            
            BytecodeUtils.writeI32(out, code.length);
            out.write(code);
        }
    }

    /**
     * Подсчитывает общее количество локальных переменных в функции, включая параметры.
     * Анализирует блок функции, чтобы найти все объявления переменных.
     * @param func Функция для анализа
     * @return Общее количество локальных переменных
     */
    private int countLocalVariables(FunctionDeclaration func) {
        // Начинаем с параметров функции
        int count = func.getParameters().size();
        
        // Анализируем тело функции для поиска всех объявлений переменных
        if (func.getBody() != null && func.getBody() instanceof Scope bodyScope) {
            count += countVariableDeclarationsInScope(bodyScope);
        }
        
        return count;
    }
    
    /**
     * Рекурсивно подсчитывает количество объявлений переменных в области видимости
     * @param scope Область видимости для анализа
     * @return Количество найденных объявлений переменных
     */
    private int countVariableDeclarationsInScope(Scope scope) {
        int count = 0;
        
        // Получаем список операторов
        List<Statement> statements = scope.getChildren().stream()
            .filter(node -> node instanceof Statement)
            .map(node -> (Statement) node)
            .collect(Collectors.toList());
        
        if (statements.isEmpty()) {
            return 0;
        }
        
        for (Statement stmt : statements) {
            if (stmt instanceof VariableDeclaration) {
                count++;
            } else if (stmt instanceof IfStatement ifStmt) {
                // Проверяем блоки if и else
                List<Node> children = ifStmt.getChildren();
                if (children.size() >= 2 && children.get(1) instanceof Scope thenScope) {
                    count += countVariableDeclarationsInScope(thenScope);
                }
                if (children.size() >= 3 && children.get(2) instanceof Scope elseScope) {
                    count += countVariableDeclarationsInScope(elseScope);
                }
            } else if (stmt instanceof WhileLoop whileLoop) {
                // Проверяем тело цикла while
                List<Node> children = whileLoop.getChildren();
                if (children.size() >= 2 && children.get(1) instanceof Scope bodyScope) {
                    count += countVariableDeclarationsInScope(bodyScope);
                }
            } else if (stmt instanceof ForLoop forLoop) {
                // Проверяем тело цикла for и инициализацию
                List<Node> children = forLoop.getChildren();
                // Проверяем инициализатор цикла
                if (!children.isEmpty() && children.get(0) instanceof VariableDeclaration) {
                    count++;
                }
                // Проверяем тело цикла
                if (children.size() >= 4 && children.get(3) instanceof Scope bodyScope) {
                    count += countVariableDeclarationsInScope(bodyScope);
                }
            } else if (stmt instanceof Scope nestedScope) {
                // Рекурсивно обрабатываем вложенные области видимости
                count += countVariableDeclarationsInScope(nestedScope);
            }
        }
        
        return count;
    }

    private byte getTypeId(Type type) {
        if (type instanceof PrimitiveType prim) {
            return switch (prim.getName()) {
                case "void" -> BytecodeConstants.TypeId.VOID;
                case "i32" -> BytecodeConstants.TypeId.I32;
                case "usize" -> BytecodeConstants.TypeId.USIZE;
                case "string" -> BytecodeConstants.TypeId.STRING;
                case "bool" -> BytecodeConstants.TypeId.I32;
                default -> throw new RuntimeException("Unknown type: " + prim.getName());
            };
        } else if (type instanceof ArrayType) {
            return BytecodeConstants.TypeId.ARRAY;
        }
        throw new RuntimeException("Unsupported type: " + type.getClass().getSimpleName());
    }

    /**
     * Записывает секцию глобального байткода для инициализации переменных
     * @param program корневой узел AST
     */
    private void writeGlobalBytecode(Scope program) throws IOException {
        ByteArrayOutputStream globalOut = new ByteArrayOutputStream();
        
        // Проверяем, что программа не null
        if (program == null || program.getStatements() == null) {
            // Если программа пуста, записываем 0 как длину байткода
            BytecodeUtils.writeI32(out, 0);
            return;
        }
        
        // Генерируем байткод для глобального скоупа (инициализация глобальных переменных)
        for (Statement stmt : program.getStatements()) {
            if (stmt instanceof VariableDeclaration varDecl) {
                try {
                    varDecl.emitBytecode(globalOut, context);
                } catch (Exception e) {
                    System.err.println("Ошибка при генерации байткода для глобальной переменной: " + varDecl.getName());
                    e.printStackTrace();
                }
            }
        }
        
        byte[] code = globalOut.toByteArray();
        
        // Проверка размера глобального байткода
        if (code.length > 1000000) { // Ограничиваем размер глобального байткода до 1MB
            System.err.println("Предупреждение: очень большой размер глобального байткода: " + code.length + " байт");
        }
        
        // Дополнительная защита от переполнения
        if (code.length > Integer.MAX_VALUE - 10) {
            throw new IOException("Слишком большой размер глобального байткода");
        }
        
        BytecodeUtils.writeI32(out, code.length);
        out.write(code);
    }
} 