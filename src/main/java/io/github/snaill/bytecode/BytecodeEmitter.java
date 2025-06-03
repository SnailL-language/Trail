package io.github.snaill.bytecode;

import io.github.snaill.ast.*;
import io.github.snaill.exception.FailedCheckException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Фасад для генерации байткода из AST.
 * Делегирует генерацию байткода самим узлам AST через emitBytecode.
 */
public class BytecodeEmitter {
    private final Scope program;
    private final BytecodeContext context;
    private final Map<String, Integer> localVariables;
    private final Map<String, FunctionSignature> functionSignatures;
    private final Map<String, Integer> localVariableCounts;

    /**
     * Исключение, выбрасываемое при ошибках генерации байткода
     */
    public static class BytecodeEmitterException extends RuntimeException {
        public BytecodeEmitterException(String message) {
            super(message);
        }

        public BytecodeEmitterException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Конструктор без параметров для совместимости с новым интерфейсом Trail
     */
    public BytecodeEmitter() {
        this.program = null;
        this.context = new BytecodeContext();
        this.localVariables = new HashMap<>();
        this.functionSignatures = new HashMap<>();
        this.localVariableCounts = new HashMap<>();
        registerBuiltInFunctions();
    }
    
    /**
     * Конструктор с параметром Scope
     * 
     * @param program Глобальная область видимости программы
     */
    public BytecodeEmitter(Scope program) {
        this.program = program;
        this.context = new BytecodeContext();
        // Сохраняем глобальные выражения в контексте
        context.setGlobalStatements(program.getStatements());
        this.localVariables = new HashMap<>();
        this.functionSignatures = new HashMap<>();
        this.localVariableCounts = new HashMap<>();
        registerBuiltInFunctions();
    }

    /**
     * Генерирует байткод для всей программы в соответствии со спецификацией.
     * Порядок секций байткода:
     * 1. Заголовок (magic number, версия, индекс функции main)
     * 2. Пул констант
     * 3. Глобальные переменные
     * 4. Таблица функций (включая байткод функций)
     * 5. Глобальный байткод (инициализация глобальных переменных)
     */
    public byte[] emit() throws IOException, FailedCheckException {
        // Инициализируем контекст, регистрируем все глобальные переменные и функции
        initializeContext();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // 1. Записываем заголовок
            writeHeader(out);

            // 2. Записываем пул констант
            writeConstantPool(out);

            // 3. Записываем глобальные переменные
            writeGlobalVariables(out);

            // 4. Записываем таблицу функций и их байткод
            writeFunctions(out);
            
            // 5. Записываем таблицу встроенных функций (intrinsics)
            writeIntrinsics(out);

            // 6. Записываем глобальный байткод (инициализация глобальных переменных)
            writeGlobalBytecode(out);

            return out.toByteArray();
        } catch (IOException e) {
            throw new BytecodeEmitterException("IO error during bytecode generation: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new BytecodeEmitterException("Unexpected error during bytecode generation: " + e.getMessage(), e);
        }
    }
    
    /**
     * Генерирует байткод из AST и записывает его в файл.
     * Этот метод используется в Trail.java для генерации байткода.
     * 
     * @param ast AST программы
     * @param outputPath путь к выходному файлу
     * @throws BytecodeEmitterException если произошла ошибка при генерации байткода
     */
    public void emitBytecode(io.github.snaill.ast.AST ast, String outputPath) throws BytecodeEmitterException {
        try {
            // Создаем новый экземпляр BytecodeEmitter с корневым скоупом из AST
            BytecodeEmitter emitter = new BytecodeEmitter(ast.getRoot());
            
            // Генерируем байткод
            byte[] bytecode = emitter.emit();
            
            // Записываем байткод в файл
            java.nio.file.Files.write(java.nio.file.Paths.get(outputPath), bytecode);
            
            System.out.println("Байткод успешно сохранен в: " + outputPath + " (" + bytecode.length + " байт)");
        } catch (IOException e) {
            throw new BytecodeEmitterException("Ошибка записи байткода в файл: " + e.getMessage(), e);
        } catch (FailedCheckException e) {
            throw new BytecodeEmitterException("Ошибка проверки при генерации байткода: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new BytecodeEmitterException("Неожиданная ошибка при генерации байткода: " + e.getMessage(), e);
        }
    }

    /**
     * Инициализирует контекст генерации байткода.
     * Регистрирует все глобальные переменные, функции и константы в контексте.
     */
    private void initializeContext() {
        System.err.println("Initializing bytecode context...");

        if (program == null) {
            System.err.println("Warning: program scope is null, context initialization might be incomplete");
            // Добавляем стандартные константы
            context.addConstant(0L);  // Для инициализации по умолчанию
            context.addConstant(1L);  // Для инкремента/декремента
            return;
        }
        
        // Сначала собираем все глобальные переменные
        for (Statement stmt : program.getStatements()) {
            if (stmt instanceof VariableDeclaration var) {
                System.err.println("Registering global variable: " + var.getName());
                context.addGlobalVariable(var.getName());

                // Добавляем константы из инициализаторов глобальных переменных
                addConstants(var.getValue());
            }
        }

        // Затем регистрируем все функции и константы в них
        for (Statement stmt : program.getStatements()) {
            if (stmt instanceof FunctionDeclaration func) {
                System.err.println("Registering function: " + func.getName() + " with " + func.getParameters().size() + " parameters");
                context.addFunction(func);

                // Добавляем константы из тела функции
                addConstantsFromScope(func.getBody());
            }
        }

        // Добавляем стандартные константы
        context.addConstant(0L);  // Для инициализации по умолчанию
        context.addConstant(1L);  // Для инкремента/декремента
    }

    /**
     * Рекурсивно добавляет все константы из выражения в контекст
     */
    private void addConstants(Expression expr) {
        if (expr == null) return;

        if (expr instanceof NumberLiteral number) {
            context.addConstant(number.getValue());
        } else if (expr instanceof StringLiteral string) {
            context.addConstant(string.getValue());
        } else if (expr instanceof BooleanLiteral bool) {
            // В Snail булевы значения представлены как i32
            context.addConstant(bool.getValue() ? 1L : 0L);
        } else if (expr instanceof BinaryExpression binary) {
            addConstants(binary.getLeft());
            addConstants(binary.getRight());
        } else if (expr instanceof UnaryExpression unary) {
            addConstants(unary.getArgument());
        } else if (expr instanceof ArrayLiteral array) {
            for (Expression element : array.getElements()) {
                addConstants(element);
            }
        } else if (expr instanceof FunctionCall call) {
            for (Expression arg : call.getArguments()) {
                addConstants(arg);
            }
        } else if (expr instanceof AssignmentExpression assignExpr) { // Новый блок
            // Обрабатываем правую часть присваивания
            addConstants(assignExpr.getRight());
            // Обрабатываем левую часть, если это, например, ArrayAccess с литералом в индексе
            addConstants(assignExpr.getLeft());
        } else if (expr instanceof ArrayAccess arrayAccess) { // Новый блок
            // Обрабатываем выражение индекса
            addConstants(arrayAccess.getIndex());
            // Также можно обработать выражение самого массива, если оно может быть литералом,
            // но обычно это идентификатор.
            // addConstants(arrayAccess.getArrayExpression());
        }
    }

    /**
     * Рекурсивно добавляет все константы из области видимости (Scope) в контекст
     */
    private void addConstantsFromScope(Scope scope) {
        if (scope == null) return;

        for (Node node : scope.getChildren()) {
            if (node instanceof VariableDeclaration varDecl) {
                addConstants(varDecl.getValue());
            } else if (node instanceof ExpressionStatement exprStmt) {
                addConstants(exprStmt.getExpression());
            } else if (node instanceof IfStatement ifStmt) {
                addConstants(ifStmt.getCondition());
                addConstantsFromScope(ifStmt.getBody());
                if (ifStmt.getElseBody() != null) {
                    addConstantsFromScope(ifStmt.getElseBody());
                }
            } else if (node instanceof WhileLoop whileLoop) {
                addConstants(whileLoop.getCondition());
                addConstantsFromScope(whileLoop.getBody());
            } else if (node instanceof ForLoop forLoop) {
                addConstants(forLoop.getCondition());
                addConstants(forLoop.getStep());
                addConstantsFromScope(forLoop.getBody());
            } else if (node instanceof ReturnStatement returnStmt) {
                addConstants(returnStmt.getReturnable());
            } else if (node instanceof Scope nestedScope) {
                addConstantsFromScope(nestedScope);
            }
        }
    }

    /**
     * Записывает заголовок байткода
     */
    private void writeHeader(ByteArrayOutputStream out) throws IOException {
        out.write(BytecodeConstants.MAGIC_NUMBER);
        BytecodeUtils.writeU16(out, BytecodeConstants.CURRENT_VERSION);
        // Индекс main (если есть)
        int mainIdx = context.getFunctionIndex("main");
        BytecodeUtils.writeU16(out, mainIdx >= 0 ? mainIdx : 0xFFFF);
    }

    /**
     * Записывает таблицу встроенных функций (intrinsics)
     * @param out поток для записи байткода
     */
    private void writeIntrinsics(ByteArrayOutputStream out) throws IOException {
        // Записываем количество встроенных функций (всего 1 - println)
        BytecodeUtils.writeU16(out, 1);
        
        // Записываем встроенную функцию println
        // Имя функции
        String name = "println";
        byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
        out.write(nameBytes.length); // Длина имени
        out.write(nameBytes);  // Имя
        
        // Количество параметров (1)
        out.write(1);
        
        // Тип возвращаемого значения (void - 0x03)
        out.write(0x03);
    }
    
    /**
     * Записывает секцию глобального байткода для инициализации переменных
     * @param out поток для записи байткода
     */
    private void writeGlobalBytecode(ByteArrayOutputStream out) throws IOException, FailedCheckException {
        ByteArrayOutputStream globalOut = new ByteArrayOutputStream();
        
        // Проверяем, что программа не null
        if (program == null || program.getStatements() == null) {
            // Если программа пуста, записываем 0 как длину байткода
            BytecodeUtils.writeI32(out, 0);
            return;
        }
        
        // Генерируем байткод для глобального скоупа (инициализация глобальных переменных)
        for (Statement stmt : program.getStatements()) {
            if (stmt instanceof VariableDeclaration varDecl && varDecl.getValue() != null) {
                try {
                    varDecl.emitBytecode(globalOut, context);
                } catch (Exception e) {
                    System.err.println("Ошибка при генерации байткода для глобальной переменной: " + varDecl.getName());
                    e.printStackTrace();
                }
            } else if (stmt instanceof ExpressionStatement exprStmt) {
                try {
                    exprStmt.emitBytecode(globalOut, context);
                } catch (Exception e) {
                    System.err.println("Ошибка при генерации байткода для глобального выражения");
                    e.printStackTrace();
                }
            }
        }
        
        // Добавляем вызов функции main в конец глобального кода
        int mainFuncIdx = context.getFunctionIndex("main");
        if (mainFuncIdx >= 0) {
            globalOut.write(BytecodeConstants.Opcode.CALL);
            BytecodeUtils.writeU16(globalOut, mainFuncIdx);
        } else {
            System.err.println("Предупреждение: функция main не найдена, вызов не будет добавлен в глобальный код");
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
        
        // Записываем длину глобального кода как целое число (4 байта)
        int actualLength = code.length;
        
        // Отладочная информация о длине глобального кода
        System.out.println("Действительная длина глобального кода: " + actualLength + " байт");
        
        // Корректно записываем длину как 4 байта (big-endian) для соответствия с форматом SnailVM
        out.write((actualLength >> 24) & 0xFF);
        out.write((actualLength >> 16) & 0xFF);
        out.write((actualLength >> 8) & 0xFF);
        out.write(actualLength & 0xFF);
        
        // Записываем сам глобальный код
        out.write(code);
    }

    /**
     * Записывает секцию констант в байткод
     */
    private void writeConstantPool(ByteArrayOutputStream out) throws IOException {
        List<Object> constants = context.getConstants();
        BytecodeUtils.writeU16(out, constants.size());
        for (Object c : constants) {
            if (c instanceof Long l) {
                out.write(BytecodeConstants.TypeId.I32);
                BytecodeUtils.writeI32(out, l.intValue());
            } else if (c instanceof String s) {
                out.write(BytecodeConstants.TypeId.STRING);
                BytecodeUtils.writeU16(out, s.getBytes(StandardCharsets.UTF_8).length);
                out.write(s.getBytes(StandardCharsets.UTF_8));
            } else {
                // Неподдерживаемый тип константы
                throw new BytecodeEmitterException("Unsupported constant type: " + (c != null ? c.getClass().getName() : "null"));
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
    private void writeGlobalVariables(ByteArrayOutputStream out) throws IOException {
        List<String> globals = context.getGlobalVariables();
        BytecodeUtils.writeU16(out, globals.size());

        for (String name : globals) {
            byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
            out.write((byte) nameBytes.length);
            out.write(nameBytes);

            // Определяем тип переменной из ее объявления
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

    // Helper method to find global variable declaration by name
    private VariableDeclaration findGlobalVariableDeclaration(String name) {
        if (program == null || program.getStatements() == null) {
            return null;
        }
        for (Statement stmt : program.getStatements()) {
            if (stmt instanceof VariableDeclaration varDecl && varDecl.getName().equals(name)) {
                return varDecl;
            }
        }
        return null;
    }

    /**
     * Записывает таблицу функций в байткод.
     * Для каждой функции записывается:
     * - Имя функции
     * - Количество параметров
     * - Тип возвращаемого значения
     * - Количество локальных переменных (включая параметры)
     * - Длина байткода функции
     * - Байткод функции
     */
    private void writeFunctions(ByteArrayOutputStream out) throws IOException, FailedCheckException {
        List<FunctionDeclaration> functions = context.getFunctions();
        BytecodeUtils.writeU16(out, functions.size());

        for (FunctionDeclaration func : functions) {
            // Записываем имя функции
            byte[] nameBytes = func.getName().getBytes(StandardCharsets.UTF_8);
            out.write((byte) nameBytes.length);
            out.write(nameBytes);

            // Записываем количество параметров
            out.write((byte) func.getParameters().size());

            // Записываем тип возвращаемого значения
            out.write(getTypeId(func.getReturnType()));
            
            // Индексируем параметры как локальные переменные
            // Параметры занимают индексы 0..N-1, где N - количество параметров
            Map<String, Integer> localVarIndices = new HashMap<>();
            int idx = 0;
            for (Parameter p : func.getParameters()) {
                localVarIndices.put(p.getName(), idx++);
            }
            
            // Собираем все локальные переменные из тела функции
            // Они получают индексы после параметров
            Set<String> localVars = new LinkedHashSet<>();
            collectLocalVariables(func.getBody(), localVars);
            for (String var : localVars) {
                if (!localVarIndices.containsKey(var)) {
                    localVarIndices.put(var, idx++);
                }
            }
            
            // Устанавливаем индексы в контексте для дальнейшего использования в PUSH_LOCAL/STORE_LOCAL
            context.setLocalVarIndices(func, localVarIndices);
            BytecodeUtils.writeU16(out, localVarIndices.size());
            
            // Теперь, когда индексы переменных зарегистрированы, генерируем байткод функции
            ByteArrayOutputStream funcOut = new ByteArrayOutputStream();
            
            // Используем метод emitBytecode функции для генерации ее байткода
            // Это обеспечит правильную генерацию кода в соответствии с логикой самой функции
            func.emitBytecode(funcOut, context, func);
            
            byte[] code = funcOut.toByteArray();
            BytecodeUtils.writeI32(out, code.length);
            out.write(code);
        }
    }

    // Рекурсивно собирает имена всех локальных переменных (VariableDeclaration) в scope
    private void collectLocalVariables(Node node, Set<String> vars) {
        if (node instanceof Scope scope) {
            for (Node child : scope.getChildren()) {
                collectLocalVariables(child, vars);
            }
        } else if (node instanceof VariableDeclaration varDecl) {
            vars.add(varDecl.getName());
            // Если инициализатор — ArrayLiteral, резервируем временную переменную
            if (varDecl.getValue() instanceof io.github.snaill.ast.ArrayLiteral) {
                vars.add("__tmp_array_" + System.identityHashCode(varDecl.getValue()));
            }
        } else if (node instanceof io.github.snaill.ast.ArrayLiteral arrLit) {
            vars.add("__tmp_array_" + System.identityHashCode(arrLit));
        } else if (node instanceof ForLoop forLoop) {
            collectLocalVariables((VariableDeclaration) forLoop.getBody().getChildren().get(0), vars);
            collectLocalVariables(forLoop.getBody(), vars);
        } else if (node instanceof IfStatement ifStmt) {
            collectLocalVariables(ifStmt.getBody(), vars);
            if (ifStmt.getElseBody() != null) collectLocalVariables(ifStmt.getElseBody(), vars);
        } else if (node instanceof WhileLoop whileLoop) {
            collectLocalVariables(whileLoop.getBody(), vars);
        }
        // Можно добавить другие конструкции, если появятся
    }

    /**
     * Определяет идентификатор типа для байткода на основе типа из AST.
     * 
     * @param type тип из AST
     * @return идентификатор типа для байткода
     */
    private byte getTypeId(Type type) {
        if (type == null) {
            return BytecodeConstants.TypeId.VOID;
        }
        
        // Используем toString() вместо getTypeName()
        String typeName = type.toString();
        if ("i32".equals(typeName)) {
            return BytecodeConstants.TypeId.I32;
        } else if ("usize".equals(typeName)) {
            return BytecodeConstants.TypeId.USIZE;
        } else if ("string".equals(typeName)) {
            return BytecodeConstants.TypeId.STRING;
        } else if ("bool".equals(typeName)) {
            // Для булевого типа используем I32, так как BOOL не определен
            return BytecodeConstants.TypeId.I32;
        } else if ("void".equals(typeName)) {
            return BytecodeConstants.TypeId.VOID;
        } else if (type instanceof ArrayType) {
            return BytecodeConstants.TypeId.ARRAY;
        }
        
        throw new IllegalArgumentException("Unknown type: " + typeName);
    }

    private void registerBuiltInFunctions() {
        List<Parameter> params = new ArrayList<>();
        params.add(new Parameter("arg", new PrimitiveType("any")));
        registerBuiltInFunction("println", new FunctionSignature("println", params, new PrimitiveType("void")));
    }

    private void registerBuiltInFunction(String name, FunctionSignature signature) {
        functionSignatures.put(name, signature);
    }

    private int registerLocalVariable(String name, FunctionDeclaration currentFunction) {
        if (currentFunction == null) {
            throw new IllegalStateException("Cannot register local variable without a current function");
        }
        String key = currentFunction.getName() + ":" + name;
        if (localVariables.containsKey(key)) {
            throw new IllegalStateException("Local variable index already set for " + name + " in function " + currentFunction.getName());
        }
        int index = localVariableCounts.getOrDefault(currentFunction.getName(), 0);
        localVariables.put(key, index);
        localVariableCounts.put(currentFunction.getName(), index + 1);
        return index;
    }

    // Временное определение класса FunctionSignature, если его нет в проекте
    public static class FunctionSignature {
        private final String name;
        private final List<Parameter> parameters;
        private final Type returnType;

        public FunctionSignature(String name, List<Parameter> parameters, Type returnType) {
            this.name = name;
            this.parameters = parameters;
            this.returnType = returnType;
        }

        public String getName() {
            return name;
        }

        public List<Parameter> getParameters() {
            return parameters;
        }

        public Type getReturnType() {
            return returnType;
        }
    }
}
