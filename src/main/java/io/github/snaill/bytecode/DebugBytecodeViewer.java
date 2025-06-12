package io.github.snaill.bytecode;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Дебаггер/дизассемблер для байткода SnailVM.
 * Выводит подробную расшифровку секций, опкодов и констант.
 */
public class DebugBytecodeViewer {
    
    /**
     * Метод main для прямого запуска отладчика байткода
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            // System.out.println("Использование: java -cp target/classes io.github.snaill.bytecode.DebugBytecodeViewer <файл_байткода>");
            return;
        }
        
        try {
            java.nio.file.Path path = java.nio.file.Path.of(args[0]);
            byte[] bytecode = java.nio.file.Files.readAllBytes(path);
            String disassembly = disassemble(bytecode);
            System.out.println(disassembly);
        } catch (Exception e) {
            System.err.println("Error reading or disassembling bytecode: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static final Map<Integer, String> OPCODE_NAMES = new HashMap<>();
    static {
        OPCODE_NAMES.put((int)BytecodeConstants.Opcode.NOP & 0xFF, "NOP");
        OPCODE_NAMES.put((int)BytecodeConstants.Opcode.PUSH_CONST & 0xFF, "PUSH_CONST");
        OPCODE_NAMES.put((int)BytecodeConstants.Opcode.PUSH_LOCAL & 0xFF, "PUSH_LOCAL");
        OPCODE_NAMES.put((int)BytecodeConstants.Opcode.PUSH_GLOBAL & 0xFF, "PUSH_GLOBAL");
        OPCODE_NAMES.put((int)BytecodeConstants.Opcode.STORE_LOCAL & 0xFF, "STORE_LOCAL");
        OPCODE_NAMES.put((int)BytecodeConstants.Opcode.STORE_GLOBAL & 0xFF, "STORE_GLOBAL");
        OPCODE_NAMES.put((int)BytecodeConstants.Opcode.POP & 0xFF, "POP");
        OPCODE_NAMES.put((int)BytecodeConstants.Opcode.DUP & 0xFF, "DUP");
        OPCODE_NAMES.put((int)BytecodeConstants.Opcode.ADD & 0xFF, "ADD");
        OPCODE_NAMES.put((int)BytecodeConstants.Opcode.SUB & 0xFF, "SUB");
        OPCODE_NAMES.put((int)BytecodeConstants.Opcode.MUL & 0xFF, "MUL");
        OPCODE_NAMES.put((int)BytecodeConstants.Opcode.DIV & 0xFF, "DIV");
        OPCODE_NAMES.put((int)BytecodeConstants.Opcode.MOD & 0xFF, "MOD");
        OPCODE_NAMES.put((int)BytecodeConstants.Opcode.EQ & 0xFF, "EQ");
        OPCODE_NAMES.put((int)BytecodeConstants.Opcode.NEQ & 0xFF, "NEQ");
        OPCODE_NAMES.put((int)BytecodeConstants.Opcode.LT & 0xFF, "LT");
        OPCODE_NAMES.put((int)BytecodeConstants.Opcode.LTE & 0xFF, "LTE");
        OPCODE_NAMES.put((int)BytecodeConstants.Opcode.GT & 0xFF, "GT");
        OPCODE_NAMES.put((int)BytecodeConstants.Opcode.GTE & 0xFF, "GTE");
        OPCODE_NAMES.put((int)BytecodeConstants.Opcode.AND & 0xFF, "AND");
        OPCODE_NAMES.put((int)BytecodeConstants.Opcode.OR & 0xFF, "OR");
        OPCODE_NAMES.put((int)BytecodeConstants.Opcode.NOT & 0xFF, "NOT");
        OPCODE_NAMES.put((int)BytecodeConstants.Opcode.JMP & 0xFF, "JMP");
        OPCODE_NAMES.put((int)BytecodeConstants.Opcode.JMP_IF_FALSE & 0xFF, "JMP_IF_FALSE");
        OPCODE_NAMES.put((int)BytecodeConstants.Opcode.JMP_IF_TRUE & 0xFF, "JMP_IF_TRUE");
        OPCODE_NAMES.put((int)BytecodeConstants.Opcode.CALL & 0xFF, "CALL");
        OPCODE_NAMES.put((int)BytecodeConstants.Opcode.INTRINSIC_CALL & 0xFF, "INTRINSIC_CALL");
        OPCODE_NAMES.put((int)BytecodeConstants.Opcode.RET & 0xFF, "RET");
        OPCODE_NAMES.put((int)BytecodeConstants.Opcode.HALT & 0xFF, "HALT");
        OPCODE_NAMES.put((int)BytecodeConstants.Opcode.NEW_ARRAY & 0xFF, "NEW_ARRAY");
        OPCODE_NAMES.put((int)BytecodeConstants.Opcode.GET_ARRAY & 0xFF, "GET_ARRAY");
        OPCODE_NAMES.put((int)BytecodeConstants.Opcode.SET_ARRAY & 0xFF, "SET_ARRAY");
    }

    private static class ConstantInfo {
        public final int typeId;
        public Object value;

        public ConstantInfo(int typeId, Object value) {
            this.typeId = typeId;
            this.value = value;
        }

        @Override
        public String toString() {
            return switch (typeId) {
                case BytecodeConstants.TypeId.I32 -> "i32: " + value;
                case BytecodeConstants.TypeId.USIZE -> "usize: " + value;
                case BytecodeConstants.TypeId.STRING -> "string(" + ((String) value).length() + "): \"" + value + "\"";
                default -> "unknown(" + typeId + ")";
            };
        }
    }

    private record FunctionInfo(String name, int paramCount, int returnType, int localVarCount, int codeLength,
                                int codeOffset, boolean isMain) {

        @Override
        public String toString() {
            String mainMarker = isMain ? " [main]" : "";
            return String.format("%s%s (params: %d, return: %s, locals: %d, code length: %d bytes, offset: 0x%08X)",
                    name, mainMarker, paramCount, getTypeDescription(returnType), localVarCount, codeLength, codeOffset);
        }
    }

    private static class GlobalVarInfo {
        final String name;
        final byte typeId;
        byte elemTypeId;
        int size;

        public GlobalVarInfo(String name, byte typeId) {
            this.name = name;
            this.typeId = typeId;
            this.elemTypeId = 0;
            this.size = 0;
        }
    }

    private record IntrinsicInfo(String name, int paramCount, int returnType) {

        @Override
            public String toString() {
                return String.format("%s (params: %d, return: %s)", name, paramCount, getTypeDescription(returnType));
            }
        }

    public static String disassemble(byte[] code) {
        if (code == null || code.length < 8) {
            return "Invalid bytecode: too short";
        }

        StringBuilder sb = new StringBuilder();
        List<ConstantInfo> constants = new ArrayList<>();
        List<GlobalVarInfo> globals = new ArrayList<>();
        List<FunctionInfo> functions = new ArrayList<>();
        List<IntrinsicInfo> intrinsics = new ArrayList<>();
        int pos = 0;

        // Read header
        int[] mainFunctionIndex = new int[]{-1};
        pos = readHeader(code, sb, pos, mainFunctionIndex);

        // Read constants
        pos = readConstants(code, sb, pos, constants, new int[]{2});

        // Read global variables
        pos = readGlobals(code, sb, pos, globals);

        // Read functions
        pos = readFunctions(code, sb, pos, functions, mainFunctionIndex[0], constants, globals);

        // Read intrinsic functions table
        pos = readIntrinsics(code, sb, pos, intrinsics);

        // Read and analyze global bytecode
        pos = readGlobalCode(code, sb, pos, constants, globals, functions, intrinsics, mainFunctionIndex[0]);

        // Output statistics
        appendBytecodeStatistics(sb, constants, functions, globals, pos);

        return sb.toString();
    }

    private static int readHeader(byte[] code, StringBuilder sb, int pos, int[] mainFunctionIndex) {
        sb.append("=== HEADER SECTION ===\n");
        sb.append("[HEADER] Magic: ");
        String magic = new String(Arrays.copyOfRange(code, pos, pos + 4), StandardCharsets.US_ASCII);
        sb.append(magic).append("\n");
        pos += 4;

        sb.append("[HEADER] Version: ");
        int version = getUnsignedShort(code, pos);
        sb.append(version).append("\n");
        pos += 2;

        sb.append("[HEADER] Main function index: ");
        int mainFuncIdx = getUnsignedShort(code, pos);
        if (mainFuncIdx == 0xFFFF) {
            sb.append("-1 (main is missing)").append("\n");
            mainFunctionIndex[0] = -1;
        } else {
            sb.append(mainFuncIdx).append("\n");
            mainFunctionIndex[0] = mainFuncIdx;
        }
        pos += 2;

        return pos;
    }

    private static int readConstants(byte[] code, StringBuilder sb, int pos, List<ConstantInfo> constants, int[] sectionSize) {
        sb.append("\n=== CONSTANTS SECTION ===\n");
        sb.append("[CONSTANTS] Count: ");
        int constCount = getUnsignedShort(code, pos);
        if (constCount < 0 || constCount > 10000) {
            sb.append(constCount).append(" (possibly incorrect count)\n");
        } else {
            sb.append(constCount).append("\n");
        }
        pos += 2;

        for (int i = 0; i < constCount && pos < code.length; i++) {
            if (pos >= code.length) {
                sb.append("  [CONST] <incomplete data>\n");
                break;
            }
            int typeId = getUnsignedByte(code[pos++]);
            sectionSize[0] += 1;

            ConstantInfo info = new ConstantInfo(typeId, null);
            if (typeId == BytecodeConstants.TypeId.I32 || typeId == BytecodeConstants.TypeId.USIZE) {
                if (pos + 4 <= code.length) {
                    int value = ((code[pos] & 0xFF) << 24) | ((code[pos + 1] & 0xFF) << 16) |
                            ((code[pos + 2] & 0xFF) << 8) | (code[pos + 3] & 0xFF);
                    info.value = value;
                    sb.append("  [CONST] ").append(typeId == BytecodeConstants.TypeId.I32 ? "i32" : "usize")
                            .append(": ").append(value).append("\n");
                    pos += 4;
                    sectionSize[0] += 4;
                } else {
                    sb.append("  [CONST] ").append(typeId == BytecodeConstants.TypeId.I32 ? "i32" : "usize")
                            .append(": <incomplete data>\n");
                    pos = code.length;
                }
            } else if (typeId == BytecodeConstants.TypeId.STRING) {
                if (pos + 2 <= code.length) {
                    int strLen = getUnsignedShort(code, pos);
                    pos += 2;
                    sectionSize[0] += 2;
                    if (strLen >= 0 && pos + strLen <= code.length) {
                        String stringValue = new String(Arrays.copyOfRange(code, pos, pos + strLen), StandardCharsets.UTF_8);
                        info.value = stringValue;
                        sb.append("  [CONST] string: \"").append(stringValue).append("\"\n");
                        pos += strLen;
                        sectionSize[0] += strLen;
                    } else {
                        sb.append("  [CONST] string: <incorrect length or incomplete data>\n");
                        pos = code.length;
                    }
                } else {
                    sb.append("  [CONST] string: <incomplete length data>\n");
                    pos = code.length;
                }
            } else {
                sb.append("  [CONST] unknown type: ").append(typeId).append("\n");
            }
            constants.add(info);
        }
        return pos;
    }

    private static int readGlobals(byte[] code, StringBuilder sb, int pos, List<GlobalVarInfo> globals) {
        sb.append("\n=== GLOBALS SECTION ===\n");
        sb.append("[GLOBALS] Count: ");
        int globalCount = getUnsignedShort(code, pos);
        if (globalCount < 0 || globalCount > 1000) {
            sb.append(globalCount).append(" (possibly incorrect count)\n");
        } else {
            sb.append(globalCount).append("\n");
        }
        pos += 2;

        for (int i = 0; i < globalCount && pos < code.length; i++) {
            int nameLen = getUnsignedByte(code[pos++]);
            if (nameLen < 0 || nameLen > 255) {
                sb.append("  [GLOBAL] <incorrect name length: ").append(nameLen).append(">\n");
                break;
            }
            if (nameLen == 0) {
                sb.append("  [GLOBAL] <empty variable name>\n");
                continue;
            }
            if (pos + nameLen <= code.length) {
                String name = new String(Arrays.copyOfRange(code, pos, pos + nameLen), StandardCharsets.UTF_8);
                pos += nameLen;
                if (pos < code.length) {
                    byte typeId = code[pos++];
                    GlobalVarInfo globalInfo = new GlobalVarInfo(name, typeId);
                    if (typeId == BytecodeConstants.TypeId.ARRAY && pos + 5 <= code.length) {
                        byte elemTypeId = code[pos++];
                        int size = ((code[pos] & 0xFF) << 24) | ((code[pos + 1] & 0xFF) << 16) |
                                ((code[pos + 2] & 0xFF) << 8) | (code[pos + 3] & 0xFF);
                        globalInfo.elemTypeId = elemTypeId;
                        globalInfo.size = size;
                        sb.append("  [GLOBAL] ").append(name).append(" : ").append(getTypeDescription(typeId))
                                .append(" (elemType: ").append(getTypeDescription(elemTypeId))
                                .append(", size: ").append(size).append(")\n");
                        pos += 4;
                    } else {
                        sb.append("  [GLOBAL] ").append(name).append(" : ").append(getTypeDescription(typeId)).append("\n");
                    }
                    globals.add(globalInfo);
                } else {
                    sb.append("  [GLOBAL] ").append(name).append(" : <incomplete data>\n");
                    pos = code.length;
                }
            } else {
                sb.append("  [GLOBAL] <incomplete name>\n");
                pos = code.length;
            }
        }
        return pos;
    }

    private static int readFunctions(byte[] code, StringBuilder sb, int pos, List<FunctionInfo> functions, int mainFunctionIndex, List<ConstantInfo> constants, List<GlobalVarInfo> globals) {
        sb.append("\n=== FUNCTIONS SECTION ===\n");
        sb.append("[FUNCTIONS] Count: ");
        int funcCount = getUnsignedShort(code, pos);
        if (funcCount < 0 || funcCount > 10000) {
            sb.append(funcCount).append(" (possibly incorrect count)\n");
        } else {
            sb.append(funcCount).append("\n");
        }
        pos += 2;

        for (int i = 0; i < funcCount && pos < code.length; i++) {
            int nameLen = getUnsignedByte(code[pos++]);
            if (nameLen < 0 || nameLen > 255) {
                sb.append("  [FUNC] <incorrect name length: ").append(nameLen).append(">\n");
                break;
            }
            if (nameLen == 0) {
                sb.append("  [FUNC] <empty function name>\n");
                continue;
            }
            if (pos + nameLen <= code.length) {
                String funcName = new String(Arrays.copyOfRange(code, pos, pos + nameLen), StandardCharsets.UTF_8);
                pos += nameLen;
                if (pos + 7 <= code.length) {
                    int paramCount = getUnsignedByte(code[pos++]);
                    int returnType = getUnsignedByte(code[pos++]);
                    int localVarCount = getUnsignedShort(code, pos);
                    pos += 2;
                    int codeLen = ((code[pos] & 0xFF) << 24) | ((code[pos + 1] & 0xFF) << 16) |
                            ((code[pos + 2] & 0xFF) << 8) | (code[pos + 3] & 0xFF);
                    pos += 4;
                    int codeOffset = pos;
                    boolean isMain = (i == mainFunctionIndex);
                    FunctionInfo funcInfo = new FunctionInfo(funcName, paramCount, returnType, localVarCount, codeLen, codeOffset, isMain);
                    functions.add(funcInfo);
                    sb.append("  [FUNC] ").append(funcInfo).append("\n");
                    if (codeLen > 0 && pos + codeLen <= code.length) {
                        // Helper method to get a string from the constant pool and global variables
                        sb.append(dumpInstructions(code, pos, codeLen, "      ", constants, globals, functions));
                        pos += codeLen;
                    } else {
                        sb.append("      <incomplete function code>\n");
                        pos = code.length;
                    }
                } else {
                    sb.append("  [FUNC] ").append(funcName).append(" : <incomplete metadata>\n");
                    pos = code.length;
                }
            } else {
                sb.append("  [FUNC] <incomplete name>\n");
                pos = code.length;
            }
        }
        return pos;
    }

    private static int readIntrinsics(byte[] code, StringBuilder sb, int pos, List<IntrinsicInfo> intrinsics) {
        sb.append("\n=== INTRINSICS SECTION ===\n");
        sb.append("[INTRINSICS] Count: ");
        int intrinsicCount = getUnsignedShort(code, pos);
        if (intrinsicCount < 0 || intrinsicCount > 1000) {
            sb.append(intrinsicCount).append(" (possibly incorrect count)\n");
        } else {
            sb.append(intrinsicCount).append("\n");
        }
        pos += 2;

        for (int i = 0; i < intrinsicCount && pos < code.length; i++) {
            int nameLen = getUnsignedByte(code[pos++]);
            if (nameLen < 0 || nameLen > 255) {
                sb.append("  [INTRINSIC] <incorrect name length: ").append(nameLen).append(">\n");
                break;
            }
            if (nameLen == 0) {
                sb.append("  [INTRINSIC] <empty name>\n");
                continue;
            }
            if (pos + nameLen <= code.length) {
                String intrinsicName = new String(Arrays.copyOfRange(code, pos, pos + nameLen), StandardCharsets.UTF_8);
                pos += nameLen;
                if (pos + 2 <= code.length) {
                    int paramCount = getUnsignedByte(code[pos++]);
                    int returnType = getUnsignedByte(code[pos++]);
                    IntrinsicInfo intrinsicInfo = new IntrinsicInfo(intrinsicName, paramCount, returnType);
                    intrinsics.add(intrinsicInfo);
                    sb.append("  [INTRINSIC] ").append(intrinsicInfo).append("\n");
                } else {
                    sb.append("  [INTRINSIC] ").append(intrinsicName).append(" : <incomplete data>\n");
                    pos = code.length;
                }
            } else {
                sb.append("  [INTRINSIC] <incomplete name>\n");
                pos = code.length;
            }
        }
        return pos;
    }

    private static int readGlobalCode(byte[] code, StringBuilder sb, int pos, List<ConstantInfo> constants,
                                       List<GlobalVarInfo> globals, List<FunctionInfo> functions,
                                       List<IntrinsicInfo> intrinsics, int mainFunctionIndex) {
        sb.append("\n=== GLOBAL CODE SECTION ===\n");
        sb.append("[GLOBAL CODE] Length: ");
        if (pos + 4 > code.length) {
            sb.append("<incomplete length>\n");
            return code.length;
        }
        
        // Read global code length (4 bytes, big-endian)
        long rawCodeLen = ((long)(code[pos] & 0xFF) << 24) | 
                ((long)(code[pos + 1] & 0xFF) << 16) |
                ((long)(code[pos + 2] & 0xFF) << 8) | 
                (long)(code[pos + 3] & 0xFF);

        pos += 4;
        
        // Convert to int for further use
        int globalCodeLen = (int)(rawCodeLen & 0x7FFFFFFF);
        
        // Check if global code length makes sense
        // Maximum reasonable size for global code is 1 MB
        if (globalCodeLen < 0 || globalCodeLen > 1_000_000) {
            sb.append(globalCodeLen).append(" (warning: suspiciously large size, limited to 100KB)\n");
            
            // Try to find a more reasonable length:
            // Look for bytes that might be the length of the global code
            int estimatedLen = 0;
            // Check if the length is recorded in different positions nearby
            for (int offset = -8; offset <= 8; offset += 4) {
                if (pos - 4 + offset >= 0 && pos - 4 + offset + 3 < code.length) {
                    int testLen = ((code[pos - 4 + offset] & 0xFF) << 24) | 
                                ((code[pos - 4 + offset + 1] & 0xFF) << 16) |
                                ((code[pos - 4 + offset + 2] & 0xFF) << 8) | 
                                (code[pos - 4 + offset + 3] & 0xFF);
                    if (testLen > 0 && testLen < 1000) {
                        // Assume this might be the length of the global code at offset " + offset + " from the current position: " + testLen);
                        estimatedLen = testLen;
                    }
                }
            }
            
            // If a suitable value is found, use it
            // Если нашли подходящее значение, используем его
            if (estimatedLen > 0) {
                // System.out.println("Используем оценочную длину глобального кода: " + estimatedLen);
                globalCodeLen = estimatedLen;
            } else {
                // Иначе ограничиваем до разумного размера
                globalCodeLen = Math.min(100_000, Math.max(0, globalCodeLen));
            }
        } else {
            sb.append(globalCodeLen).append("\n");
        }

        if (globalCodeLen > 0) {
            // Проверяем, хватает ли данных для чтения всего глобального кода
            if (pos + globalCodeLen > code.length) {
                int availableLen = code.length - pos;
                sb.append("Скорректировано до: ").append(availableLen).append(" (предупреждение: выходит за границы файла)\n");
                globalCodeLen = availableLen;
            }
            
            // Ищем вызов функции main в глобальном коде
            boolean hasMainCall = false;
            int mainCallOffset = -1;
            if (mainFunctionIndex >= 0 && globalCodeLen >= 3) {
                for (int i = pos; i < pos + globalCodeLen - 2; i++) {
                    if (code[i] == BytecodeConstants.Opcode.CALL &&
                            getUnsignedShort(code, i + 1) == mainFunctionIndex) {
                        hasMainCall = true;
                        mainCallOffset = i;
                        break;
                    }
                }
            }

            if (hasMainCall) {
                sb.append("  [Найден вызов main в глобальном коде на смещении 0x")
                        .append(String.format("%04X", mainCallOffset - pos)).append("]\n");
                if (mainCallOffset + 3 < pos + globalCodeLen - 1) {
                    boolean foundHalt = false;
                    for (int i = mainCallOffset + 3; i < pos + globalCodeLen; i++) {
                        if (code[i] == BytecodeConstants.Opcode.HALT) {
                            foundHalt = true;
                            break;
                        }
                    }
                    if (!foundHalt) {
                        sb.append("  [Предупреждение: инструкции после вызова main до HALT]\n");
                    }
                }
            } else if (mainFunctionIndex >= 0) {
                sb.append("  [Вызов main не найден в глобальном коде]\n");
            }

            if (globalCodeLen > 0 && pos + globalCodeLen <= code.length) {
                sb.append(dumpInstructions(code, pos, globalCodeLen, "  ", constants, globals, functions));
                pos += globalCodeLen;
            } else {
                sb.append("  <неполные данные глобального кода>\n");
                pos = code.length;
            }
        }

        return pos;
    }

    // Неиспользуемый метод analyzeGlobalCode был удален, так как секция с глобальными переменными больше не отображается

    // Метод appendGlobalValues был удален, так как его функциональность перенесена в метод readGlobalCode

    private static void appendBytecodeStatistics(StringBuilder sb, List<ConstantInfo> constants, List<FunctionInfo> functions,
                                                 List<GlobalVarInfo> globals, int totalSize) {
        sb.append("\n=== BYTECODE STATISTICS ===\n");
        sb.append("  [Общий размер] ").append(totalSize).append(" байт (0x")
                .append(Integer.toHexString(totalSize).toUpperCase()).append(")\n");

        int functionCodeSize = functions.stream().mapToInt(f -> f.codeLength).sum();
        sb.append("  [Секции] Заголовок: 8 байт, Константы: ").append(2)
                .append(" байт (").append(constants.size()).append(" записей), Функции: ")
                .append(functionCodeSize).append(" байт кода (").append(functions.size())
                .append(" функций), Глобальный код: ").append(totalSize - 2 - functionCodeSize - 8)
                .append(" байт\n");
        sb.append("  [Глобальные переменные] ").append(globals.size()).append(" переменных\n");
    }

    private static String dumpInstructions(byte[] code, int start, int length, String indent,
                                           List<ConstantInfo> constants, List<GlobalVarInfo> globals,
                                           List<FunctionInfo> functions) {
        StringBuilder sb = new StringBuilder();
        int pos = start;
        int end = Math.min(start + length, code.length);

        while (pos < end) {
            int offset = pos - start;
            byte opcode = code[pos++];
            sb.append(indent).append(String.format("%04X: ", offset));

            String opcodeName = OPCODE_NAMES.getOrDefault((int) opcode & 0xFF, "UNKNOWN_" + String.format("%02X", opcode & 0xFF));
            
            // Для опкодов, которые требуют дополнительных параметров, их нужно обрабатывать в switch
            if (opcode == BytecodeConstants.Opcode.PUSH_CONST || 
                opcode == BytecodeConstants.Opcode.PUSH_LOCAL || 
                opcode == BytecodeConstants.Opcode.PUSH_GLOBAL || 
                opcode == BytecodeConstants.Opcode.STORE_LOCAL || 
                opcode == BytecodeConstants.Opcode.STORE_GLOBAL || 
                opcode == BytecodeConstants.Opcode.JMP || 
                opcode == BytecodeConstants.Opcode.JMP_IF_FALSE || 
                opcode == BytecodeConstants.Opcode.JMP_IF_TRUE || 
                opcode == BytecodeConstants.Opcode.CALL || 
                opcode == BytecodeConstants.Opcode.INTRINSIC_CALL || 
                opcode == BytecodeConstants.Opcode.NEW_ARRAY || 
                opcode == BytecodeConstants.Opcode.GET_ARRAY || 
                opcode == BytecodeConstants.Opcode.SET_ARRAY) {
                // Продолжаем выполнение, чтобы обработать параметры в switch
            } else {
                sb.append(opcodeName).append("\n");
                continue;
            }

            switch (opcode) {
                case BytecodeConstants.Opcode.PUSH_CONST:
                    if (pos + 1 < end) {
                        int constIdx = getUnsignedShort(code, pos);
                        sb.append("PUSH_CONST ").append(constIdx);
                        if (constants != null && constIdx >= 0 && constIdx < constants.size()) {
                            Object constValue = constants.get(constIdx).value;
                            ConstantInfo constInfo = constants.get(constIdx);
                            // Улучшенное отображение значения константы
                            sb.append(" (").append(getTypeDescription(constInfo.typeId)).append(": ").append(constValue).append(")");
                            
                            // Если это константа для индекса массива и следующая инструкция - SET_ARRAY
                            // добавляем понятный комментарий с индексом элемента
                            if (constValue instanceof Long && 
                                pos + 2 < end && 
                                pos + 3 < code.length && 
                                code[pos + 3] == BytecodeConstants.Opcode.SET_ARRAY) {
                                sb.append(" [индекс элемента: ").append(constValue).append("]");
                            }
                        }
                        pos += 2;
                    } else {
                        sb.append("PUSH_CONST [truncated]");
                        pos = end;
                    }
                    break;
                case BytecodeConstants.Opcode.PUSH_LOCAL:
                    if (pos + 1 < end) {
                        int localIdx = getUnsignedShort(code, pos);
                        sb.append("PUSH_LOCAL ").append(localIdx);
                        if (functions != null) {
                            for (FunctionInfo func : functions) {
                                if (pos >= func.codeOffset && pos < func.codeOffset + func.codeLength) {
                                    sb.append(localIdx < func.paramCount ? " (параметр #" : " (локальная переменная #")
                                            .append(localIdx < func.paramCount ? localIdx : localIdx - func.paramCount).append(")");
                                    break;
                                }
                            }
                        }
                        pos += 2;
                    } else {
                        sb.append("PUSH_LOCAL [truncated]");
                        pos = end;
                    }
                    break;
                case BytecodeConstants.Opcode.PUSH_GLOBAL:
                    if (pos + 1 < end) {
                        int globalIdx = getUnsignedShort(code, pos);
                        sb.append("PUSH_GLOBAL ").append(globalIdx);
                        if (globals != null && globalIdx >= 0 && globalIdx < globals.size()) {
                            GlobalVarInfo varInfo = globals.get(globalIdx);
                            sb.append(" (").append(varInfo.name);
                            
                            // Улучшенное отображение типа переменной
                            String typeDesc = getTypeDescription(varInfo.typeId);
                            if (varInfo.typeId == BytecodeConstants.TypeId.ARRAY && varInfo.elemTypeId > 0) {
                                typeDesc = "array of " + getTypeDescription(varInfo.elemTypeId) + "[" + varInfo.size + "]";
                            }
                            sb.append(", ").append(typeDesc).append(")");
                        }
                        pos += 2;
                    } else {
                        sb.append("PUSH_GLOBAL [truncated]");
                        pos = end;
                    }
                    break;
                case BytecodeConstants.Opcode.STORE_LOCAL:
                    if (pos + 1 < end) {
                        int storeLocalIdx = getUnsignedShort(code, pos);
                        sb.append("STORE_LOCAL ").append(storeLocalIdx);
                        if (functions != null) {
                            for (FunctionInfo func : functions) {
                                if (pos >= func.codeOffset && pos < func.codeOffset + func.codeLength) {
                                    sb.append(storeLocalIdx < func.paramCount ? " (параметр #" : " (локальная переменная #")
                                            .append(storeLocalIdx < func.paramCount ? storeLocalIdx : storeLocalIdx - func.paramCount).append(")");
                                    break;
                                }
                            }
                        }
                        pos += 2;
                    } else {
                        sb.append("STORE_LOCAL [truncated]");
                        pos = end;
                    }
                    break;
                case BytecodeConstants.Opcode.STORE_GLOBAL:
                    if (pos + 1 < end) {
                        int storeGlobalIdx = getUnsignedShort(code, pos);
                        sb.append("STORE_GLOBAL ").append(storeGlobalIdx);
                        if (globals != null && storeGlobalIdx >= 0 && storeGlobalIdx < globals.size()) {
                            GlobalVarInfo varInfo = globals.get(storeGlobalIdx);
                            sb.append(" (").append(varInfo.name);
                            
                            // Улучшенное отображение типа переменной
                            String typeDesc = getTypeDescription(varInfo.typeId);
                            if (varInfo.typeId == BytecodeConstants.TypeId.ARRAY && varInfo.elemTypeId > 0) {
                                typeDesc = "array of " + getTypeDescription(varInfo.elemTypeId) + "[" + varInfo.size + "]";
                            }
                            sb.append(", ").append(typeDesc).append(")");
                        }
                        pos += 2;
                    } else {
                        sb.append("STORE_GLOBAL [truncated]");
                        pos = end;
                    }
                    break;
                case BytecodeConstants.Opcode.JMP:
                case BytecodeConstants.Opcode.JMP_IF_FALSE:
                case BytecodeConstants.Opcode.JMP_IF_TRUE:
                    if (pos + 1 < end) {
                        short jmpOffset = (short) getUnsignedShort(code, pos);
                        int destPos = pos + 2 + jmpOffset;
                        int destOffset = destPos - start;
                        String jmpType = opcode == BytecodeConstants.Opcode.JMP ? "JMP" :
                                opcode == BytecodeConstants.Opcode.JMP_IF_FALSE ? "JMP_IF_FALSE" : "JMP_IF_TRUE";
                        sb.append(jmpType).append(" ").append(jmpOffset)
                                .append(" [to 0x").append(String.format("%04X", destOffset)).append("]");
                        pos += 2;
                    } else {
                        sb.append(opcode == BytecodeConstants.Opcode.JMP ? "JMP" :
                                        opcode == BytecodeConstants.Opcode.JMP_IF_FALSE ? "JMP_IF_FALSE" : "JMP_IF_TRUE")
                                .append(" [truncated]");
                        pos = end;
                    }
                    break;
                case BytecodeConstants.Opcode.CALL:
                    if (pos + 1 < end) {
                        int funcIdx = getUnsignedShort(code, pos);
                        sb.append("CALL ").append(funcIdx);
                        if (functions != null && funcIdx >= 0 && funcIdx < functions.size()) {
                            FunctionInfo funcInfo = functions.get(funcIdx);
                            sb.append(" (").append(funcInfo.name);
                            if (funcInfo.paramCount > 0) {
                                sb.append(", ").append(funcInfo.paramCount).append(" параметр");
                                if (funcInfo.paramCount > 1 && funcInfo.paramCount < 5) {
                                    sb.append("а");
                                } else if (funcInfo.paramCount >= 5) {
                                    sb.append("ов");
                                }
                            }
                            sb.append(")");
                        }
                        pos += 2;
                    } else {
                        sb.append("CALL [truncated]");
                        pos = end;
                    }
                    break;
                case BytecodeConstants.Opcode.INTRINSIC_CALL:
                    if (pos + 1 < end) {
                        int intrinsicIdx = getUnsignedShort(code, pos);
                        sb.append("INTRINSIC_CALL ").append(intrinsicIdx);
                        pos += 2;
                    } else {
                        sb.append("INTRINSIC_CALL [truncated]");
                        pos = end;
                    }
                    break;
                case BytecodeConstants.Opcode.NEW_ARRAY:
                    if (pos + 2 < end) {
                        int arraySize = getUnsignedShort(code, pos);
                        sb.append("NEW_ARRAY ").append(arraySize);
                        pos += 2;
                        byte elemTypeId = code[pos++];
                        sb.append(" ").append(getTypeDescription(elemTypeId));
                    } else {
                        sb.append("NEW_ARRAY [truncated]");
                        pos = end;
                    }
                    break;
                case BytecodeConstants.Opcode.GET_ARRAY:
                    // GET_ARRAY не принимает аргументов, он использует значения со стека (массив и индекс)
                    sb.append("GET_ARRAY");
                    // Если предыдущая инструкция PUSH_CONST, то это индекс массива
                    if (pos - 3 >= 0 && code[pos - 3] == BytecodeConstants.Opcode.PUSH_CONST && constants != null) {
                        int prevConstIdx = getUnsignedShort(code, pos - 2);
                        if (prevConstIdx >= 0 && prevConstIdx < constants.size()) {
                            Object constValue = constants.get(prevConstIdx).value;
                            sb.append(" [индекс: ").append(constValue).append("]");
                        }
                    }
                    break;
                case BytecodeConstants.Opcode.SET_ARRAY:
                    // SET_ARRAY не принимает аргументов, он использует значения со стека (массив, индекс и значение)
                    sb.append("SET_ARRAY");
                    // Если предыдущая инструкция PUSH_CONST, то это индекс массива
                    if (pos - 3 >= 0 && code[pos - 3] == BytecodeConstants.Opcode.PUSH_CONST && constants != null) {
                        int prevConstIdx = getUnsignedShort(code, pos - 2);
                        if (prevConstIdx >= 0 && prevConstIdx < constants.size()) {
                            Object constValue = constants.get(prevConstIdx).value;
                            sb.append(" [индекс: ").append(constValue).append("]");
                        }
                    }
                    break;
                default:
                    sb.append("UNKNOWN_").append(String.format("%02X", opcode & 0xFF));
                    break;
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private static String getTypeDescription(int typeId) {
        return switch (typeId) {
            case BytecodeConstants.TypeId.VOID -> "void";
            case BytecodeConstants.TypeId.I32 -> "i32";
            case BytecodeConstants.TypeId.USIZE -> "usize";
            case BytecodeConstants.TypeId.STRING -> "string";
            case BytecodeConstants.TypeId.ARRAY -> "array";
            default -> "unknown(" + typeId + ")";
        };
    }

    private static int getUnsignedByte(byte b) {
        return b & 0xFF;
    }

    private static int getUnsignedShort(byte[] code, int pos) {
        return ((code[pos] & 0xFF) << 8) | (code[pos + 1] & 0xFF);
    }
}