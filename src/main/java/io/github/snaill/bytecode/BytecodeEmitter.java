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

    public BytecodeEmitter(Scope program) {
        this.program = program;
        this.context = new BytecodeContext();
        this.localVariables = new HashMap<>();
        this.functionSignatures = new HashMap<>();
        this.localVariableCounts = new HashMap<>();
        registerBuiltInFunctions();
    }

    /**
     * Генерирует байткод для всей программы.
     */
    public byte[] emit() throws IOException, FailedCheckException {
        initializeContext();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            writeHeader(out);
            writeConstantPool(out);
            writeGlobalVariables(out);
            writeFunctions(out);
            writeGlobalBytecode(out);
            return out.toByteArray();
        }
    }

    private void initializeContext() {
        for (Statement stmt : program.getStatements()) {
            if (stmt instanceof VariableDeclaration var) {
                context.addGlobalVariable(var.getName());
            } else if (stmt instanceof FunctionDeclaration func) {
                context.addFunction(func);
            }
        }
    }

    private void writeHeader(ByteArrayOutputStream out) throws IOException {
        out.write(BytecodeConstants.MAGIC_NUMBER);
        BytecodeUtils.writeU16(out, BytecodeConstants.CURRENT_VERSION);
        // Индекс main (если есть)
        int mainIdx = context.getFunctionIndex("main");
        BytecodeUtils.writeU16(out, mainIdx >= 0 ? mainIdx : 0xFFFF);
    }

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
            }
        }
    }

    private void writeGlobalVariables(ByteArrayOutputStream out) throws IOException {
        List<String> globals = context.getGlobalVariables();
        BytecodeUtils.writeU16(out, globals.size());
        for (String name : globals) {
            byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
            out.write((byte) nameBytes.length);
            out.write(nameBytes);
            // Determine the type of the variable from its declaration
            byte typeId = BytecodeConstants.TypeId.I32; // Default
            VariableDeclaration varDecl = findGlobalVariableDeclaration(name);
            if (varDecl != null) {
                Type type = varDecl.getType();
                typeId = getTypeId(type);
                out.write(typeId);
                if (typeId == BytecodeConstants.TypeId.ARRAY && type instanceof ArrayType arrayType) {
                    out.write(getTypeId(arrayType.getElementType()));
                    long size = arrayType.getSize().getValue();
                    BytecodeUtils.writeI32(out, (int) size);
                }
            } else {
                out.write(typeId); // Fallback to default
            }
        }
    }

    // Helper method to find global variable declaration by name
    private VariableDeclaration findGlobalVariableDeclaration(String name) {
        for (Statement stmt : program.getStatements()) {
            if (stmt instanceof VariableDeclaration varDecl && varDecl.getName().equals(name)) {
                return varDecl;
            }
        }
        return null;
    }

    private void writeFunctions(ByteArrayOutputStream out) throws IOException, FailedCheckException {
        List<FunctionDeclaration> functions = context.getFunctions();
        BytecodeUtils.writeU16(out, functions.size());
        for (FunctionDeclaration func : functions) {
            byte[] nameBytes = func.getName().getBytes(StandardCharsets.UTF_8);
            out.write((byte) nameBytes.length);
            out.write(nameBytes);
            out.write((byte) func.getParameters().size());
            out.write(getTypeId(func.getReturnType()));
            // Индексируем параметры как локальные переменные
            Map<String, Integer> localVarIndices = new HashMap<>();
            int idx = 0;
            for (Parameter p : func.getParameters()) {
                localVarIndices.put(p.getName(), idx++);
            }
            // Собираем все локальные переменные из тела функции
            Set<String> localVars = new LinkedHashSet<>();
            collectLocalVariables(func.getBody(), localVars);
            for (String var : localVars) {
                if (!localVarIndices.containsKey(var)) {
                    localVarIndices.put(var, idx++);
                }
            }
            // Устанавливаем индексы в контексте
            context.setLocalVarIndices(func, localVarIndices);
            BytecodeUtils.writeU16(out, localVarIndices.size());
            // Теперь, когда индексы переменных зарегистрированы, генерируем байткод функции
            ByteArrayOutputStream funcOut = new ByteArrayOutputStream();
            func.getBody().emitBytecode(funcOut, context, func);
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

    private void writeGlobalBytecode(ByteArrayOutputStream out) throws IOException, FailedCheckException {
        ByteArrayOutputStream globalOut = new ByteArrayOutputStream();
        program.emitBytecode(globalOut, context);
        byte[] code = globalOut.toByteArray();
        BytecodeUtils.writeI32(out, code.length);
        out.write(code);
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