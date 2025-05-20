package io.github.snaill.bytecode;

import io.github.snaill.ast.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Эмиттер байткода для SnailVM. Генерирует байткод из AST программы Snail.
 */
public class BytecodeEmitter {
    private final Scope program;
    private final List<Constant> constants;
    private final Map<String, Integer> constantIndices;
    private final List<GlobalVariable> globalVariables;
    private final Map<String, Integer> globalVarIndices;
    private final List<Function> functions;
    private final List<Intrinsic> intrinsics; // Список встроенных функций
    private int mainFunctionIndex = -1;

    private static class Constant {
        byte typeId;
        Object value;

        Constant(byte typeId, Object value) {
            this.typeId = typeId;
            this.value = value;
        }
    }

    private static class GlobalVariable {
        String name;
        byte typeId;
        NumberLiteral arraySize;
        Type type;

        GlobalVariable(String name, byte typeId, NumberLiteral arraySize, Type type) {
            this.name = name;
            this.typeId = typeId;
            this.arraySize = arraySize;
            this.type = type;
        }
    }

    private static class Function {
        String name;
        List<Parameter> parameters;
        byte returnTypeId;
        Map<String, Integer> localVarIndices;
        byte[] bytecode;

        Function(String name, List<Parameter> parameters, byte returnTypeId, Map<String, Integer> localVarIndices, byte[] bytecode) {
            this.name = name;
            this.parameters = parameters;
            this.returnTypeId = returnTypeId;
            this.localVarIndices = localVarIndices;
            this.bytecode = bytecode;
        }
    }

    private static class Intrinsic {
        String name;
        int paramCount;
        byte returnTypeId;

        Intrinsic(String name, int paramCount, byte returnTypeId) {
            this.name = name;
            this.paramCount = paramCount;
            this.returnTypeId = returnTypeId;
        }
    }

    private static class LoopContext {
        int endAddress;

        LoopContext(int endAddress) {
            this.endAddress = endAddress;
        }
    }

    /**
     * Создает новый эмиттер байткода для указанной программы.
     *
     * @param program корневой узел AST программы
     */
    public BytecodeEmitter(Scope program) {
        this.program = program;
        this.constants = new ArrayList<>();
        this.constantIndices = new HashMap<>();
        this.globalVariables = new ArrayList<>();
        this.globalVarIndices = new HashMap<>();
        this.functions = new ArrayList<>();
        this.intrinsics = new ArrayList<>();
        // Добавляем встроенную функцию println()
        this.intrinsics.add(new Intrinsic("println", 1, BytecodeConstants.TypeId.VOID.getValue()));
    }

    /**
     * Генерирует байткод для программы.
     *
     * @return массив байтов, представляющий скомпилированный байткод
     * @throws IOException если произошла ошибка ввода-вывода
     */
    public byte[] emit() throws IOException {
        try (ByteArrayOutputStream bytecode = new ByteArrayOutputStream()) {
            collectGlobalsAndFunctions();
            writeHeader(bytecode);
            writeConstantPool(bytecode);
            writeFunctionTable(bytecode);
            writeIntrinsicTable(bytecode); // Добавляем новый раздел
            writeGlobalVariables(bytecode);
            writeGlobalBytecode(bytecode);
            return bytecode.toByteArray();
        }
    }

    /**
     * Генерирует читаемое представление байткода для отладки.
     *
     * @return строка с отформатированным байткодом и комментариями
     * @throws IOException если произошла ошибка ввода-вывода
     */
    public String emitToDebugString() throws IOException {
        StringBuilder debugOutput = new StringBuilder();
        byte[] bytecode = emit();
        int offset = 0;

        // Header
        debugOutput.append("=== Header ===\n");
        debugOutput.append(String.format("  %02X %02X %02X %02X  // Magic: SNA1\n",
                bytecode[offset], bytecode[offset + 1], bytecode[offset + 2], bytecode[offset + 3]));
        offset += 4;
        int version = (bytecode[offset] & 0xFF) << 8 | (bytecode[offset + 1] & 0xFF);
        debugOutput.append(String.format("  %02X %02X        // Version: %d\n", bytecode[offset], bytecode[offset + 1], version));
        offset += 2;
        int mainIdx = (bytecode[offset] & 0xFF) << 8 | (bytecode[offset + 1] & 0xFF);
        debugOutput.append(String.format("  %02X %02X        // Main function index: %d\n", bytecode[offset], bytecode[offset + 1], mainIdx));
        offset += 2;
        debugOutput.append("\n");

        // Constant Pool
        debugOutput.append("=== Constant Pool ===\n");
        int constCount = (bytecode[offset] & 0xFF) << 8 | (bytecode[offset + 1] & 0xFF);
        debugOutput.append(String.format("  %02X %02X        // Number of entries: %d\n", bytecode[offset], bytecode[offset + 1], constCount));
        offset += 2;
        for (int i = 0; i < constCount; i++) {
            byte typeId = bytecode[offset];
            debugOutput.append(String.format("  %02X", typeId));
            offset++;
            if (typeId == BytecodeConstants.TypeId.I32.getValue() || typeId == BytecodeConstants.TypeId.USIZE.getValue()) {
                long value = ((bytecode[offset] & 0xFF) << 24) | ((bytecode[offset + 1] & 0xFF) << 16) |
                        ((bytecode[offset + 2] & 0xFF) << 8) | (bytecode[offset + 3] & 0xFF);
                debugOutput.append(String.format(" %02X %02X %02X %02X  // %s: %d\n",
                        bytecode[offset], bytecode[offset + 1], bytecode[offset + 2], bytecode[offset + 3],
                        typeId == BytecodeConstants.TypeId.I32.getValue() ? "i32" : "usize", value));
                offset += 4;
            } else if (typeId == BytecodeConstants.TypeId.STRING.getValue()) {
                int len = (bytecode[offset] & 0xFF) << 8 | (bytecode[offset + 1] & 0xFF);
                debugOutput.append(String.format(" %02X %02X", bytecode[offset], bytecode[offset + 1]));
                offset += 2;
                StringBuilder str = new StringBuilder();
                for (int j = 0; j < len; j++) {
                    str.append(String.format("%02X", bytecode[offset + j]));
                    if (j > 0 && j % 4 == 0) str.append(" ");
                }
                debugOutput.append(String.format("  // string: \"%s\"\n", new String(bytecode, offset, len, StandardCharsets.UTF_8)));
                offset += len;
            }
        }
        debugOutput.append("\n");

        // Function Table
        debugOutput.append("=== Function Table ===\n");
        int funcCount = (bytecode[offset] & 0xFF) << 8 | (bytecode[offset + 1] & 0xFF);
        debugOutput.append(String.format("  %02X %02X        // Number of functions: %d\n", bytecode[offset], bytecode[offset + 1], funcCount));
        offset += 2;
        for (int i = 0; i < funcCount; i++) {
            int nameLen = bytecode[offset] & 0xFF;
            debugOutput.append(String.format("  %02X", nameLen));
            offset++;
            StringBuilder name = new StringBuilder();
            for (int j = 0; j < nameLen; j++) {
                name.append((char) bytecode[offset + j]);
                debugOutput.append(String.format(" %02X", bytecode[offset + j]));
            }
            debugOutput.append(String.format("  // Name: \"%s\"\n", name.toString()));
            offset += nameLen;
            int paramCount = bytecode[offset] & 0xFF;
            debugOutput.append(String.format("  %02X            // Parameter count: %d\n", paramCount, paramCount));
            offset++;
            byte returnType = bytecode[offset];
            debugOutput.append(String.format("  %02X            // Return type: %s\n", returnType, typeIdToString(returnType)));
            offset++;
            int localVarCount = (bytecode[offset] & 0xFF) << 8 | (bytecode[offset + 1] & 0xFF);
            debugOutput.append(String.format("  %02X %02X        // Local variable count: %d\n", bytecode[offset], bytecode[offset + 1], localVarCount));
            offset += 2;
            int bytecodeLen = (bytecode[offset] & 0xFF) << 24 | (bytecode[offset + 1] & 0xFF) << 16 |
                    (bytecode[offset + 2] & 0xFF) << 8 | (bytecode[offset + 3] & 0xFF);
            debugOutput.append(String.format("  %02X %02X %02X %02X  // Bytecode length: %d bytes\n",
                    bytecode[offset], bytecode[offset + 1], bytecode[offset + 2], bytecode[offset + 3], bytecodeLen));
            offset += 4;
            debugOutput.append(debugBytecode(bytecode, offset, bytecodeLen));
            offset += bytecodeLen;
        }
        debugOutput.append("\n");

        // Intrinsic Table
        debugOutput.append("=== Intrinsic Table ===\n");
        int intrinsicCount = (bytecode[offset] & 0xFF) << 8 | (bytecode[offset + 1] & 0xFF);
        debugOutput.append(String.format("  %02X %02X        // Number of intrinsics: %d\n", bytecode[offset], bytecode[offset + 1], intrinsicCount));
        offset += 2;
        for (int i = 0; i < intrinsicCount; i++) {
            int nameLen = bytecode[offset] & 0xFF;
            debugOutput.append(String.format("  %02X", nameLen));
            offset++;
            StringBuilder name = new StringBuilder();
            for (int j = 0; j < nameLen; j++) {
                name.append((char) bytecode[offset + j]);
                debugOutput.append(String.format(" %02X", bytecode[offset + j]));
            }
            debugOutput.append(String.format("  // Name: \"%s\"\n", name.toString()));
            offset += nameLen;
            int paramCount = bytecode[offset] & 0xFF;
            debugOutput.append(String.format("  %02X            // Parameter count: %d\n", paramCount, paramCount));
            offset++;
            byte returnType = bytecode[offset];
            debugOutput.append(String.format("  %02X            // Return type: %s\n", returnType, typeIdToString(returnType)));
            offset++;
        }
        debugOutput.append("\n");

        // Global Variables
        debugOutput.append("=== Global Variables ===\n");
        int globalVarCount = (bytecode[offset] & 0xFF) << 8 | (bytecode[offset + 1] & 0xFF);
        debugOutput.append(String.format("  %02X %02X        // Number of variables: %d\n", bytecode[offset], bytecode[offset + 1], globalVarCount));
        offset += 2;
        for (int i = 0; i < globalVarCount; i++) {
            int nameLen = bytecode[offset] & 0xFF;
            debugOutput.append(String.format("  %02X", nameLen));
            offset++;
            StringBuilder name = new StringBuilder();
            for (int j = 0; j < nameLen; j++) {
                name.append((char) bytecode[offset + j]);
                debugOutput.append(String.format(" %02X", bytecode[offset + j]));
            }
            debugOutput.append(String.format("  // Name: \"%s\"\n", name.toString()));
            offset += nameLen;
            byte typeId = bytecode[offset];
            debugOutput.append(String.format("  %02X", typeId));
            offset++;
            if (typeId == BytecodeConstants.TypeId.ARRAY.getValue()) {
                byte elemTypeId = bytecode[offset];
                debugOutput.append(String.format(" %02X", elemTypeId));
                offset++;
                long size = ((bytecode[offset] & 0xFF) << 24) | ((bytecode[offset + 1] & 0xFF) << 16) |
                        ((bytecode[offset + 2] & 0xFF) << 8) | (bytecode[offset + 3] & 0xFF);
                debugOutput.append(String.format(" %02X %02X %02X %02X  // Type: array of %s, size %d\n",
                        bytecode[offset], bytecode[offset + 1], bytecode[offset + 2], bytecode[offset + 3],
                        typeIdToString(elemTypeId), size));
                offset += 4;
            } else {
                debugOutput.append(String.format("  // Type: %s\n", typeIdToString(typeId)));
            }
        }
        debugOutput.append("\n");

        // Global Bytecode
        debugOutput.append("=== Global Bytecode ===\n");
        int globalBytecodeLen = (bytecode[offset] & 0xFF) << 24 | (bytecode[offset + 1] & 0xFF) << 16 |
                (bytecode[offset + 2] & 0xFF) << 8 | (bytecode[offset + 3] & 0xFF);
        debugOutput.append(String.format("  %02X %02X %02X %02X  // Bytecode length: %d\n",
                bytecode[offset], bytecode[offset + 1], bytecode[offset + 2], bytecode[offset + 3], globalBytecodeLen));
        offset += 4;
        debugOutput.append(debugBytecode(bytecode, offset, globalBytecodeLen));

        return debugOutput.toString();
    }

    private String debugBytecode(byte[] bytecode, int offset, int length) {
        StringBuilder debug = new StringBuilder();
        int i = 0;
        while (i < length) {
            byte opcode = bytecode[offset + i];
            debug.append(String.format("  %02X", opcode));
            i++;
            if (opcode == BytecodeConstants.Opcode.PUSH_CONST.getValue()) {
                int index = (bytecode[offset + i] & 0xFF) << 8 | (bytecode[offset + i + 1] & 0xFF);
                debug.append(String.format(" %02X %02X  // PUSH_CONST %d", bytecode[offset + i],
                        bytecode[offset + i + 1], index));
                if (index < constants.size()) {
                    Constant c = constants.get(index);
                    debug.append(String.format(" (%s)", c.value));
                }
                debug.append("\n");
                i += 2;
            } else if (opcode == BytecodeConstants.Opcode.PUSH_LOCAL.getValue()) {
                int index = (bytecode[offset + i] & 0xFF) << 8 | (bytecode[offset + i + 1] & 0xFF);
                debug.append(String.format(" %02X %02X  // PUSH_LOCAL %d\n", bytecode[offset + i], bytecode[offset + i + 1], index));
                i += 2;
            } else if (opcode == BytecodeConstants.Opcode.STORE_LOCAL.getValue()) {
                int index = (bytecode[offset + i] & 0xFF) << 8 | (bytecode[offset + i + 1] & 0xFF);
                debug.append(String.format(" %02X %02X  // STORE_LOCAL %d\n", bytecode[offset + i], bytecode[offset + i + 1], index));
                i += 2;
            } else if (opcode == BytecodeConstants.Opcode.POP.getValue()) {
                debug.append("  // POP\n");
            } else if (opcode == BytecodeConstants.Opcode.ADD.getValue()) {
                debug.append("  // ADD\n");
            } else if (opcode == BytecodeConstants.Opcode.SUB.getValue()) {
                debug.append("  // SUB\n");
            } else if (opcode == BytecodeConstants.Opcode.MUL.getValue()) {
                debug.append("  // MUL\n");
            } else if (opcode == BytecodeConstants.Opcode.DIV.getValue()) {
                debug.append("  // DIV\n");
            } else if (opcode == BytecodeConstants.Opcode.MOD.getValue()) {
                debug.append("  // MOD\n");
            } else if (opcode == BytecodeConstants.Opcode.EQ.getValue()) {
                debug.append("  // EQ\n");
            } else if (opcode == BytecodeConstants.Opcode.NEQ.getValue()) {
                debug.append("  // NEQ\n");
            } else if (opcode == BytecodeConstants.Opcode.LT.getValue()) {
                debug.append("  // LT\n");
            } else if (opcode == BytecodeConstants.Opcode.LTE.getValue()) {
                debug.append("  // LTE\n");
            } else if (opcode == BytecodeConstants.Opcode.GT.getValue()) {
                debug.append("  // GT\n");
            } else if (opcode == BytecodeConstants.Opcode.GTE.getValue()) {
                debug.append("  // GTE\n");
            } else if (opcode == BytecodeConstants.Opcode.AND.getValue()) {
                debug.append("  // AND\n");
            } else if (opcode == BytecodeConstants.Opcode.OR.getValue()) {
                debug.append("  // OR\n");
            } else if (opcode == BytecodeConstants.Opcode.NOT.getValue()) {
                debug.append("  // NOT\n");
            } else if (opcode == BytecodeConstants.Opcode.JMP.getValue()) {
                int addr = (bytecode[offset + i] & 0xFF) << 8 | (bytecode[offset + i + 1] & 0xFF);
                debug.append(String.format(" %02X %02X  // JMP %d\n", bytecode[offset + i], bytecode[offset + i + 1], addr));
                i += 2;
            } else if (opcode == BytecodeConstants.Opcode.JMP_IF_FALSE.getValue()) {
                int addr = (bytecode[offset + i] & 0xFF) << 8 | (bytecode[offset + i + 1] & 0xFF);
                debug.append(String.format(" %02X %02X  // JMP_IF_FALSE %d\n", bytecode[offset + i], bytecode[offset + i + 1], addr));
                i += 2;
            } else if (opcode == BytecodeConstants.Opcode.CALL.getValue()) {
                int index = (bytecode[offset + i] & 0xFF) << 8 | (bytecode[offset + i + 1] & 0xFF);
                debug.append(String.format(" %02X %02X  // CALL %d\n", bytecode[offset + i], bytecode[offset + i + 1], index));
                i += 2;
            } else if (opcode == BytecodeConstants.Opcode.INTRINSIC_CALL.getValue()) {
                int index = (bytecode[offset + i] & 0xFF) << 8 | (bytecode[offset + i + 1] & 0xFF);
                debug.append(String.format(" %02X %02X  // INTRINSIC_CALL %d", bytecode[offset + i], bytecode[offset + i + 1], index));
                if (index < intrinsics.size()) {
                    debug.append(String.format(" (%s)", intrinsics.get(index).name));
                }
                debug.append("\n");
                i += 2;
            } else if (opcode == BytecodeConstants.Opcode.RET.getValue()) {
                debug.append("  // RET\n");
            } else if (opcode == BytecodeConstants.Opcode.HALT.getValue()) {
                debug.append("  // HALT\n");
            } else if (opcode == BytecodeConstants.Opcode.NEW_ARRAY.getValue()) {
                int size = (bytecode[offset + i] & 0xFF) << 8 | (bytecode[offset + i + 1] & 0xFF);
                byte typeId = bytecode[offset + i + 2];
                debug.append(String.format(" %02X %02X %02X  // NEW_ARRAY size %d, type %s\n",
                        bytecode[offset + i], bytecode[offset + i + 1], typeId, size, typeIdToString(typeId)));
                i += 3;
            } else if (opcode == BytecodeConstants.Opcode.GET_ARRAY.getValue()) {
                debug.append("  // GET_ARRAY\n");
            } else if (opcode == BytecodeConstants.Opcode.SET_ARRAY.getValue()) {
                debug.append("  // SET_ARRAY\n");
            } else {
                debug.append("  // Unknown opcode\n");
            }
        }
        return debug.toString();
    }

    private String typeIdToString(byte typeId) {
        if (typeId == BytecodeConstants.TypeId.VOID.getValue()) return "void";
        if (typeId == BytecodeConstants.TypeId.I32.getValue()) return "i32";
        if (typeId == BytecodeConstants.TypeId.USIZE.getValue()) return "usize";
        if (typeId == BytecodeConstants.TypeId.STRING.getValue()) return "string";
        if (typeId == BytecodeConstants.TypeId.ARRAY.getValue()) return "array";
        return "unknown";
    }

    private void collectGlobalsAndFunctions() throws IOException {
        for (Statement stmt : program.getStatements()) {
            if (stmt instanceof VariableDeclaration varDecl) {
                byte typeId = getTypeId(varDecl.getType());
                NumberLiteral arraySize = varDecl.getType() instanceof ArrayType ? ((ArrayType) varDecl.getType()).getSize() : null;
                globalVariables.add(new GlobalVariable(varDecl.getName(), typeId, arraySize, varDecl.getType()));
                globalVarIndices.put(varDecl.getName(), globalVariables.size() - 1);
                if (varDecl.getValue() instanceof ArrayLiteral arrayLit) {
                    for (Expression elem : arrayLit.getElements()) {
                        if (elem instanceof NumberLiteral num) {
                            getConstantIndex(BytecodeConstants.TypeId.I32.getValue(), num.getValue());
                        } else if (elem instanceof StringLiteral str) {
                            getConstantIndex(BytecodeConstants.TypeId.STRING.getValue(), str.getValue());
                        } else if (elem instanceof BooleanLiteral bool) {
                            getConstantIndex(BytecodeConstants.TypeId.I32.getValue(), bool.getValue() ? 1L : 0L);
                        }
                    }
                }
            } else if (stmt instanceof FunctionDeclaration funcDecl) {
                if (funcDecl.getName().equals("main")) {
                    mainFunctionIndex = functions.size();
                }
                Map<String, Integer> localVarIndices = new HashMap<>();
                int index = 0;
                // Добавляем глобальные переменные как первые локальные для main
                if (funcDecl.getName().equals("main")) {
                    for (GlobalVariable globalVar : globalVariables) {
                        localVarIndices.put(globalVar.name, index++);
                    }
                }
                // Добавляем параметры
                for (Parameter param : funcDecl.getParameterList()) {
                    localVarIndices.put(param.getName(), index++);
                }
                // Собираем остальные локальные переменные
                index = collectLocalVarIndicesRecursive(funcDecl.getScope(), localVarIndices, index);
                byte[] funcBytecode = emitFunctionBytecode(funcDecl, localVarIndices);
                functions.add(new Function(
                        funcDecl.getName(),
                        funcDecl.getParameterList(),
                        getTypeId(funcDecl.getReturnType()),
                        localVarIndices,
                        funcBytecode
                ));
            }
        }
    }

    private int collectLocalVarIndicesRecursive(Node node, Map<String, Integer> localVarIndices, int startIndex) {
        int currentIndex = startIndex;
        if (node instanceof VariableDeclaration varDecl) {
            if (!localVarIndices.containsKey(varDecl.getName())) {
                localVarIndices.put(varDecl.getName(), currentIndex);
                currentIndex++;
            }
        } else if (node instanceof Scope scope) {
            for (Statement stmt : scope.getStatements()) {
                if (stmt instanceof VariableDeclaration varDecl) {
                    if (!localVarIndices.containsKey(varDecl.getName())) {
                        localVarIndices.put(varDecl.getName(), currentIndex);
                        currentIndex++;
                    }
                }
                for (Node child : stmt.getChildren()) {
                    currentIndex = collectLocalVarIndicesRecursive(child, localVarIndices, currentIndex);
                }
            }
        }
        return currentIndex;
    }

    private void writeHeader(ByteArrayOutputStream bytecode) throws IOException {
        bytecode.write(BytecodeConstants.MAGIC_NUMBER);
        BytecodeUtils.writeU16(bytecode, BytecodeConstants.CURRENT_VERSION);
        BytecodeUtils.writeU16(bytecode, mainFunctionIndex);
    }

    private void writeConstantPool(ByteArrayOutputStream bytecode) throws IOException {
        BytecodeUtils.writeU16(bytecode, constants.size());
        for (Constant c : constants) {
            bytecode.write(c.typeId);
            if (c.typeId == BytecodeConstants.TypeId.I32.getValue() || c.typeId == BytecodeConstants.TypeId.USIZE.getValue()) {
                BytecodeUtils.writeI32(bytecode, (Long) c.value);
            } else if (c.typeId == BytecodeConstants.TypeId.STRING.getValue()) {
                byte[] bytes = ((String) c.value).getBytes(StandardCharsets.UTF_8);
                BytecodeUtils.writeU16(bytecode, bytes.length);
                bytecode.write(bytes);
            }
        }
    }

    private void writeFunctionTable(ByteArrayOutputStream bytecode) throws IOException {
        BytecodeUtils.writeU16(bytecode, functions.size());
        for (Function func : functions) {
            byte[] nameBytes = func.name.getBytes(StandardCharsets.UTF_8);
            bytecode.write((byte) nameBytes.length);
            bytecode.write(nameBytes);
            bytecode.write((byte) func.parameters.size());
            bytecode.write(func.returnTypeId);
            BytecodeUtils.writeU16(bytecode, func.localVarIndices.size());
            BytecodeUtils.writeU32(bytecode, func.bytecode.length);
            bytecode.write(func.bytecode);
        }
    }

    private void writeIntrinsicTable(ByteArrayOutputStream bytecode) throws IOException {
        BytecodeUtils.writeU16(bytecode, intrinsics.size());
        for (Intrinsic intrinsic : intrinsics) {
            byte[] nameBytes = intrinsic.name.getBytes(StandardCharsets.UTF_8);
            bytecode.write((byte) nameBytes.length);
            bytecode.write(nameBytes);
            bytecode.write((byte) intrinsic.paramCount);
            bytecode.write(intrinsic.returnTypeId);
        }
    }

    private void writeGlobalVariables(ByteArrayOutputStream bytecode) throws IOException {
        BytecodeUtils.writeU16(bytecode, globalVariables.size());
        for (GlobalVariable var : globalVariables) {
            byte[] nameBytes = var.name.getBytes(StandardCharsets.UTF_8);
            bytecode.write((byte) nameBytes.length);
            bytecode.write(nameBytes);
            bytecode.write(var.typeId);
            if (var.typeId == BytecodeConstants.TypeId.ARRAY.getValue() && var.arraySize != null) {
                Type elementType = ((ArrayType) var.type).getElementType();
                BytecodeUtils.writeU8(bytecode, getTypeId(elementType));
                BytecodeUtils.writeI32(bytecode, var.arraySize.getValue());
            }
        }
    }

    private void writeGlobalBytecode(ByteArrayOutputStream bytecode) throws IOException {
        try (ByteArrayOutputStream globalBytecode = new ByteArrayOutputStream()) {
            for (Statement stmt : program.getStatements()) {
                if (stmt instanceof VariableDeclaration varDecl) {
                    emitVariableDeclarationBytecode(varDecl, globalBytecode, true, new HashMap<>());
                }
            }
            if (mainFunctionIndex != -1) {
                globalBytecode.write(new byte[]{BytecodeConstants.Opcode.CALL.getValue()});
                BytecodeUtils.writeU16(globalBytecode, mainFunctionIndex);
            }
            globalBytecode.write(new byte[]{BytecodeConstants.Opcode.HALT.getValue()});
            byte[] bytes = globalBytecode.toByteArray();
            BytecodeUtils.writeU32(bytecode, bytes.length);
            bytecode.write(bytes);
        }
    }

    private byte[] emitFunctionBytecode(FunctionDeclaration func, Map<String, Integer> localVarIndices) throws IOException {
        try (ByteArrayOutputStream funcBytecode = new ByteArrayOutputStream()) {
            emitScopeBytecode(func.getScope(), funcBytecode, localVarIndices, new Stack<>());
            if (func.getScope().getStatements().stream().noneMatch(s -> s instanceof ReturnStatement)) {
                funcBytecode.write(new byte[]{BytecodeConstants.Opcode.RET.getValue()});
            }
            return funcBytecode.toByteArray();
        }
    }

    private void emitScopeBytecode(Scope scope, ByteArrayOutputStream out, Map<String, Integer> localVarIndices, Stack<LoopContext> loopStack) throws IOException {
        for (Statement stmt : scope.getStatements()) {
            emitStatementBytecode(stmt, out, localVarIndices, loopStack);
        }
    }

    private void emitStatementBytecode(Statement stmt, ByteArrayOutputStream out, Map<String, Integer> localVarIndices, Stack<LoopContext> loopStack) throws IOException {
        if (stmt instanceof VariableDeclaration varDecl) {
            emitVariableDeclarationBytecode(varDecl, out, false, localVarIndices);
        } else if (stmt instanceof IfStatement ifStmt) {
            emitIfStatementBytecode(ifStmt, out, localVarIndices, loopStack);
        } else if (stmt instanceof WhileLoop whileLoop) {
            emitWhileLoopBytecode(whileLoop, out, localVarIndices, loopStack);
        } else if (stmt instanceof ForLoop forLoop) {
            emitForLoopBytecode(forLoop, out, localVarIndices, loopStack);
        } else if (stmt instanceof ReturnStatement retStmt) {
            emitReturnStatementBytecode(retStmt, out, localVarIndices);
        } else if (stmt instanceof BreakStatement) {
            out.write(new byte[]{BytecodeConstants.Opcode.JMP.getValue()});
            BytecodeUtils.writeU16(out, loopStack.peek().endAddress);
        } else if (stmt instanceof Expression expr) {
            emitExpressionBytecode(expr, out, localVarIndices);
            out.write(new byte[]{BytecodeConstants.Opcode.POP.getValue()});
        }
    }

    private void emitVariableDeclarationBytecode(VariableDeclaration varDecl, ByteArrayOutputStream out, boolean isGlobal, Map<String, Integer> localVarIndices) throws IOException {
        int index = isGlobal ? globalVarIndices.get(varDecl.getName()) : localVarIndices.get(varDecl.getName());
        if (varDecl.getType() instanceof ArrayType arrayType) {
            out.write(new byte[]{BytecodeConstants.Opcode.NEW_ARRAY.getValue()});
            BytecodeUtils.writeU16(out, (int) arrayType.getSize().getValue());
            BytecodeUtils.writeU8(out, getTypeId(arrayType.getElementType()));
            out.write(new byte[]{BytecodeConstants.Opcode.STORE_LOCAL.getValue()});
            BytecodeUtils.writeU16(out, index);
            if (varDecl.getValue() instanceof ArrayLiteral arrayLit) {
                for (int i = 0; i < arrayLit.getElements().size(); i++) {
                    Expression elem = arrayLit.getElements().get(i);
                    out.write(new byte[]{BytecodeConstants.Opcode.PUSH_LOCAL.getValue()});
                    BytecodeUtils.writeU16(out, index);
                    emitExpressionBytecode(new NumberLiteral(i), out, localVarIndices);
                    emitExpressionBytecode(elem, out, localVarIndices);
                    out.write(new byte[]{BytecodeConstants.Opcode.SET_ARRAY.getValue()});
                }
            }
        } else {
            emitExpressionBytecode(varDecl.getValue(), out, localVarIndices);
            out.write(new byte[]{BytecodeConstants.Opcode.STORE_LOCAL.getValue()});
            BytecodeUtils.writeU16(out, index);
        }
    }

    private void emitIfStatementBytecode(IfStatement ifStmt, ByteArrayOutputStream out, Map<String, Integer> localVarIndices, Stack<LoopContext> loopStack) throws IOException {
        emitExpressionBytecode(ifStmt.getCondition(), out, localVarIndices);
        try (ByteArrayOutputStream thenBytecode = new ByteArrayOutputStream()) {
            emitScopeBytecode(ifStmt.getBody(), thenBytecode, localVarIndices, loopStack);
            byte[] thenBytes = thenBytecode.toByteArray();
            if (ifStmt.getElseBody() != null) {
                try (ByteArrayOutputStream elseBytecode = new ByteArrayOutputStream()) {
                    emitScopeBytecode(ifStmt.getElseBody(), elseBytecode, localVarIndices, loopStack);
                    byte[] elseBytes = elseBytecode.toByteArray();
                    out.write(new byte[]{BytecodeConstants.Opcode.JMP_IF_FALSE.getValue()});
                    BytecodeUtils.writeU16(out, thenBytes.length + 3);
                    out.write(thenBytes);
                    out.write(new byte[]{BytecodeConstants.Opcode.JMP.getValue()});
                    BytecodeUtils.writeU16(out, elseBytes.length);
                    out.write(elseBytes);
                }
            } else {
                out.write(new byte[]{BytecodeConstants.Opcode.JMP_IF_FALSE.getValue()});
                BytecodeUtils.writeU16(out, thenBytes.length);
                out.write(thenBytes);
            }
        }
    }

    private void emitWhileLoopBytecode(WhileLoop whileLoop, ByteArrayOutputStream out, Map<String, Integer> localVarIndices, Stack<LoopContext> loopStack) throws IOException {
        int startPos = out.size();
        emitExpressionBytecode(whileLoop.getCondition(), out, localVarIndices);
        try (ByteArrayOutputStream bodyBytecode = new ByteArrayOutputStream()) {
            loopStack.push(new LoopContext(startPos + bodyBytecode.size() + 3));
            emitScopeBytecode(whileLoop.getBody(), bodyBytecode, localVarIndices, loopStack);
            byte[] bodyBytes = bodyBytecode.toByteArray();
            out.write(new byte[]{BytecodeConstants.Opcode.JMP_IF_FALSE.getValue()});
            BytecodeUtils.writeU16(out, bodyBytes.length + 3);
            out.write(bodyBytes);
            out.write(new byte[]{BytecodeConstants.Opcode.JMP.getValue()});
            BytecodeUtils.writeU16(out, startPos);
            loopStack.pop();
        }
    }

    private void emitForLoopBytecode(ForLoop forLoop, ByteArrayOutputStream out, Map<String, Integer> localVarIndices, Stack<LoopContext> loopStack) throws IOException {
        emitVariableDeclarationBytecode(forLoop.getDeclaration(), out, false, localVarIndices);
        int startPos = out.size();
        emitExpressionBytecode(forLoop.getCondition(), out, localVarIndices);
        try (ByteArrayOutputStream bodyBytecode = new ByteArrayOutputStream()) {
            loopStack.push(new LoopContext(startPos + bodyBytecode.size() + 3));
            emitScopeBytecode(forLoop.getBody(), bodyBytecode, localVarIndices, loopStack);
            emitExpressionBytecode(forLoop.getStep(), bodyBytecode, localVarIndices);
            out.write(new byte[]{BytecodeConstants.Opcode.POP.getValue()});
            byte[] bodyBytes = bodyBytecode.toByteArray();
            out.write(new byte[]{BytecodeConstants.Opcode.JMP_IF_FALSE.getValue()});
            BytecodeUtils.writeU16(out, bodyBytes.length + 3);
            out.write(bodyBytes);
            out.write(new byte[]{BytecodeConstants.Opcode.JMP.getValue()});
            BytecodeUtils.writeU16(out, startPos);
            loopStack.pop();
        }
    }

    private void emitReturnStatementBytecode(ReturnStatement retStmt, ByteArrayOutputStream out, Map<String, Integer> localVarIndices) throws IOException {
        if (retStmt.getReturnable() != null) {
            emitExpressionBytecode(retStmt.getReturnable(), out, localVarIndices);
        }
        out.write(new byte[]{BytecodeConstants.Opcode.RET.getValue()});
    }

    private void emitExpressionBytecode(Expression expr, ByteArrayOutputStream out, Map<String, Integer> localVarIndices) throws IOException {
        if (expr instanceof NumberLiteral num) {
            int index = getConstantIndex(BytecodeConstants.TypeId.I32.getValue(), num.getValue());
            out.write(new byte[]{BytecodeConstants.Opcode.PUSH_CONST.getValue()});
            BytecodeUtils.writeU16(out, index);
        } else if (expr instanceof StringLiteral str) {
            int index = getConstantIndex(BytecodeConstants.TypeId.STRING.getValue(), str.getValue());
            out.write(new byte[]{BytecodeConstants.Opcode.PUSH_CONST.getValue()});
            BytecodeUtils.writeU16(out, index);
        } else if (expr instanceof BooleanLiteral bool) {
            int index = getConstantIndex(BytecodeConstants.TypeId.I32.getValue(), bool.getValue() ? 1L : 0L);
            out.write(new byte[]{BytecodeConstants.Opcode.PUSH_CONST.getValue()});
            BytecodeUtils.writeU16(out, index);
        } else if (expr instanceof Identifier id) {
            if (localVarIndices.containsKey(id.getName())) {
                out.write(new byte[]{BytecodeConstants.Opcode.PUSH_LOCAL.getValue()});
                BytecodeUtils.writeU16(out, localVarIndices.get(id.getName()));
            } else if (globalVarIndices.containsKey(id.getName())) {
                out.write(new byte[]{BytecodeConstants.Opcode.PUSH_LOCAL.getValue()});
                BytecodeUtils.writeU16(out, globalVarIndices.get(id.getName()));
            } else {
                throw new RuntimeException("Variable not found: " + id.getName());
            }
        } else if (expr instanceof ArrayElement arrayEl) {
            emitExpressionBytecode(arrayEl.getIdentifier(), out, localVarIndices);
            for (Expression dim : arrayEl.getDims()) {
                emitExpressionBytecode(dim, out, localVarIndices);
            }
            out.write(new byte[]{BytecodeConstants.Opcode.GET_ARRAY.getValue()});
        } else if (expr instanceof ArrayLiteral arrayLit) {
            Type elementType = inferElementType(arrayLit.getElements().isEmpty() ? new NumberLiteral(0) : arrayLit.getElements().getFirst());
            out.write(new byte[]{BytecodeConstants.Opcode.NEW_ARRAY.getValue()});
            BytecodeUtils.writeU16(out, arrayLit.getElements().size());
            BytecodeUtils.writeU8(out, getTypeId(elementType));
            for (int i = 0; i < arrayLit.getElements().size(); i++) {
                out.write(new byte[]{BytecodeConstants.Opcode.PUSH_LOCAL.getValue()});
                BytecodeUtils.writeU16(out, localVarIndices.size() - 1);
                emitExpressionBytecode(new NumberLiteral(i), out, localVarIndices);
                emitExpressionBytecode(arrayLit.getElements().get(i), out, localVarIndices);
                out.write(new byte[]{BytecodeConstants.Opcode.SET_ARRAY.getValue()});
            }
        } else if (expr instanceof BinaryExpression binExpr) {
            emitExpressionBytecode(binExpr.getLeft(), out, localVarIndices);
            emitExpressionBytecode(binExpr.getRight(), out, localVarIndices);
            out.write(getBinaryOpCode(binExpr.getOperator()));
        } else if (expr instanceof UnaryExpression unExpr) {
            emitExpressionBytecode(unExpr.getArgument(), out, localVarIndices);
            if (unExpr.getOperator().equals("!")) {
                out.write(new byte[]{BytecodeConstants.Opcode.NOT.getValue()});
            } else if (unExpr.getOperator().equals("-")) {
                out.write(new byte[]{BytecodeConstants.Opcode.PUSH_CONST.getValue()});
                BytecodeUtils.writeU16(out, getConstantIndex(BytecodeConstants.TypeId.I32.getValue(), 0L));
                out.write(new byte[]{BytecodeConstants.Opcode.SUB.getValue()});
            }
        } else if (expr instanceof FunctionCall funcCall) {
            for (Expression arg : funcCall.getArguments()) {
                emitExpressionBytecode(arg, out, localVarIndices);
            }
            // Проверяем, является ли функция встроенной
            Optional<Intrinsic> intrinsic = intrinsics.stream()
                    .filter(intr -> intr.name.equals(funcCall.getName()))
                    .findFirst();
            if (intrinsic.isPresent()) {
                int intrinsicIndex = intrinsics.indexOf(intrinsic.get());
                out.write(new byte[]{BytecodeConstants.Opcode.INTRINSIC_CALL.getValue()});
                BytecodeUtils.writeU16(out, intrinsicIndex);
            } else {
                int funcIndex = functions.stream()
                        .filter(f -> f.name.equals(funcCall.getName()))
                        .findFirst()
                        .map(functions::indexOf)
                        .orElseThrow(() -> new RuntimeException("Function not found: " + funcCall.getName()));
                out.write(new byte[]{BytecodeConstants.Opcode.CALL.getValue()});
                BytecodeUtils.writeU16(out, funcIndex);
            }
        } else if (expr instanceof AssigmentExpression assignExpr) {
            Expression left = assignExpr.getLeft();
            if (left instanceof Identifier id) {
                emitExpressionBytecode(assignExpr.getExpression(), out, localVarIndices);
                if (localVarIndices.containsKey(id.getName())) {
                    out.write(new byte[]{BytecodeConstants.Opcode.STORE_LOCAL.getValue()});
                    BytecodeUtils.writeU16(out, localVarIndices.get(id.getName()));
                } else if (globalVarIndices.containsKey(id.getName())) {
                    out.write(new byte[]{BytecodeConstants.Opcode.STORE_LOCAL.getValue()});
                    BytecodeUtils.writeU16(out, globalVarIndices.get(id.getName()));
                } else {
                    throw new RuntimeException("Variable not found: " + id.getName());
                }
            } else if (left instanceof ArrayElement arrayEl) {
                emitExpressionBytecode(arrayEl.getIdentifier(), out, localVarIndices);
                for (Expression dim : arrayEl.getDims()) {
                    emitExpressionBytecode(dim, out, localVarIndices);
                }
                emitExpressionBytecode(assignExpr.getExpression(), out, localVarIndices);
                out.write(new byte[]{BytecodeConstants.Opcode.SET_ARRAY.getValue()});
            } else {
                throw new RuntimeException("Invalid left-hand side of assignment: " + left.getClass().getSimpleName());
            }
        }
    }

    private Type inferElementType(Expression expr) {
        if (expr instanceof NumberLiteral || expr instanceof BooleanLiteral) {
            return new PrimitiveType("i32");
        } else if (expr instanceof StringLiteral) {
            return new PrimitiveType("string");
        } else if (expr instanceof ArrayLiteral arrayLit) {
            if (arrayLit.getElements().isEmpty()) {
                return new ArrayType(new PrimitiveType("i32"), new NumberLiteral(0));
            }
            Type elementType = inferElementType(arrayLit.getElements().getFirst());
            NumberLiteral size = new NumberLiteral(arrayLit.getElements().size());
            return new ArrayType(elementType, size);
        } else {
            return new PrimitiveType("i32");
        }
    }

    private int getConstantIndex(byte typeId, Object value) {
        String key = typeId + ":" + value.toString();
        if (!constantIndices.containsKey(key)) {
            constants.add(new Constant(typeId, value));
            constantIndices.put(key, constants.size() - 1);
        }
        return constantIndices.get(key);
    }

    private byte getTypeId(Type type) {
        if (type instanceof PrimitiveType prim) {
            return switch (prim.getName()) {
                case "void" -> BytecodeConstants.TypeId.VOID.getValue();
                case "i32" -> BytecodeConstants.TypeId.I32.getValue();
                case "usize" -> BytecodeConstants.TypeId.USIZE.getValue();
                case "string" -> BytecodeConstants.TypeId.STRING.getValue();
                case "bool" -> BytecodeConstants.TypeId.I32.getValue();
                default -> throw new RuntimeException("Unknown type: " + prim.getName());
            };
        } else if (type instanceof ArrayType) {
            return BytecodeConstants.TypeId.ARRAY.getValue();
        }
        throw new RuntimeException("Unsupported type: " + type.getClass().getSimpleName());
    }

    private byte getBinaryOpCode(String operator) {
        return switch (operator) {
            case "+" -> BytecodeConstants.Opcode.ADD.getValue();
            case "-" -> BytecodeConstants.Opcode.SUB.getValue();
            case "*" -> BytecodeConstants.Opcode.MUL.getValue();
            case "/" -> BytecodeConstants.Opcode.DIV.getValue();
            case "%" -> BytecodeConstants.Opcode.MOD.getValue();
            case "==" -> BytecodeConstants.Opcode.EQ.getValue();
            case "!=" -> BytecodeConstants.Opcode.NEQ.getValue();
            case "<" -> BytecodeConstants.Opcode.LT.getValue();
            case "<=" -> BytecodeConstants.Opcode.LTE.getValue();
            case ">" -> BytecodeConstants.Opcode.GT.getValue();
            case ">=" -> BytecodeConstants.Opcode.GTE.getValue();
            case "&&" -> BytecodeConstants.Opcode.AND.getValue();
            case "||" -> BytecodeConstants.Opcode.OR.getValue();
            default -> throw new RuntimeException("Unknown operator: " + operator);
        };
    }
}