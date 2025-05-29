package io.github.snaill.bytecode;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Дебаггер/дизассемблер для байткода SnailVM.
 * Выводит подробную расшифровку секций, опкодов и констант.
 */
public class DebugBytecodeViewer {
    public static String disassemble(byte[] code) {
        StringBuilder sb = new StringBuilder();
        int pos = 0;
        // --- Magic number ---
        sb.append("[HEADER] Magic: ");
        byte[] magic = Arrays.copyOfRange(code, pos, pos + 4);
        sb.append(new String(magic, StandardCharsets.UTF_8)).append("\n");
        pos += 4;
        // --- Version ---
        int version = ((code[pos] & 0xFF) << 8) | (code[pos + 1] & 0xFF);
        sb.append("[HEADER] Version: ").append(version).append("\n");
        pos += 2;
        // --- Main function index ---
        int mainIdx = ((code[pos] & 0xFF) << 8) | (code[pos + 1] & 0xFF);
        sb.append("[HEADER] Main function index: ").append(mainIdx).append("\n");
        pos += 2;
        // --- Constant pool ---
        int constCount = ((code[pos] & 0xFF) << 8) | (code[pos + 1] & 0xFF);
        pos += 2;
        sb.append("[CONSTANTS] Count: ").append(constCount).append("\n");
        for (int i = 0; i < constCount; i++) {
            byte type = code[pos++];
            if (type == BytecodeConstants.TypeId.I32) {
                int val = BytecodeUtils.readI32(code, pos);
                sb.append("  [I32] ").append(val).append("\n");
                pos += 4;
            } else if (type == BytecodeConstants.TypeId.STRING) {
                int len = ((code[pos] & 0xFF) << 8) | (code[pos + 1] & 0xFF);
                pos += 2;
                String str = new String(code, pos, len, StandardCharsets.UTF_8);
                sb.append("  [STRING] \"").append(str).append("\"\n");
                pos += len;
            } else {
                sb.append("  [UNKNOWN CONST TYPE] ").append(type).append("\n");
            }
        }
        // --- Globals ---
        int globalsCount = ((code[pos] & 0xFF) << 8) | (code[pos + 1] & 0xFF);
        pos += 2;
        sb.append("[GLOBALS] Count: ").append(globalsCount).append("\n");
        for (int i = 0; i < globalsCount; i++) {
            int nameLen = code[pos++] & 0xFF;
            String name = new String(code, pos, nameLen, StandardCharsets.UTF_8);
            pos += nameLen;
            byte typeId = code[pos++];
            sb.append("  [GLOBAL] ").append(name).append(" : type ").append(typeId).append("\n");
        }
        // --- Functions ---
        int funcCount = ((code[pos] & 0xFF) << 8) | (code[pos + 1] & 0xFF);
        pos += 2;
        sb.append("[FUNCTIONS] Count: ").append(funcCount).append("\n");
        for (int i = 0; i < funcCount; i++) {
            int nameLen = code[pos++] & 0xFF;
            String name = new String(code, pos, nameLen, StandardCharsets.UTF_8);
            pos += nameLen;
            int paramCount = code[pos++] & 0xFF;
            byte retType = code[pos++];
            int localsCount = ((code[pos] & 0xFF) << 8) | (code[pos + 1] & 0xFF);
            pos += 2;
            int codeLen = BytecodeUtils.readI32(code, pos);
            pos += 4;
            sb.append("  [FUNC] ").append(name).append(" (params: ").append(paramCount)
              .append(", retType: ").append(retType).append(", locals: ").append(localsCount)
              .append(", codeLen: ").append(codeLen).append(")\n");
            sb.append(dumpInstructions(code, pos, codeLen, "    "));
            pos += codeLen;
        }
        // --- Global code ---
        int globalCodeLen = BytecodeUtils.readI32(code, pos);
        pos += 4;
        sb.append("[GLOBAL CODE] Length: ").append(globalCodeLen).append("\n");
        sb.append(dumpInstructions(code, pos, globalCodeLen, "  "));
        return sb.toString();
    }

    private static String dumpInstructions(byte[] code, int start, int length, String indent) {
        StringBuilder sb = new StringBuilder();
        int pos = start;
        int end = start + length;
        while (pos < end) {
            int offset = pos - start;
            byte opcode = code[pos++];
            sb.append(indent).append(String.format("%04X: ", offset));
            switch (opcode) {
                case BytecodeConstants.Opcode.PUSH_CONST -> {
                    int idx = ((code[pos] & 0xFF) << 8) | (code[pos + 1] & 0xFF);
                    sb.append("PUSH_CONST ").append(idx).append("\n");
                    pos += 2;
                }
                case BytecodeConstants.Opcode.PUSH_LOCAL -> {
                    int idx = ((code[pos] & 0xFF) << 8) | (code[pos + 1] & 0xFF);
                    sb.append("PUSH_LOCAL ").append(idx).append("\n");
                    pos += 2;
                }
                case BytecodeConstants.Opcode.PUSH_GLOBAL -> {
                    int idx = ((code[pos] & 0xFF) << 8) | (code[pos + 1] & 0xFF);
                    sb.append("PUSH_GLOBAL ").append(idx).append("\n");
                    pos += 2;
                }
                case BytecodeConstants.Opcode.STORE_LOCAL -> {
                    int idx = ((code[pos] & 0xFF) << 8) | (code[pos + 1] & 0xFF);
                    sb.append("STORE_LOCAL ").append(idx).append("\n");
                    pos += 2;
                }
                case BytecodeConstants.Opcode.STORE_GLOBAL -> {
                    int idx = ((code[pos] & 0xFF) << 8) | (code[pos + 1] & 0xFF);
                    sb.append("STORE_GLOBAL ").append(idx).append("\n");
                    pos += 2;
                }
                case BytecodeConstants.Opcode.POP -> sb.append("POP\n");
                case BytecodeConstants.Opcode.ADD -> sb.append("ADD\n");
                case BytecodeConstants.Opcode.SUB -> sb.append("SUB\n");
                case BytecodeConstants.Opcode.MUL -> sb.append("MUL\n");
                case BytecodeConstants.Opcode.DIV -> sb.append("DIV\n");
                case BytecodeConstants.Opcode.MOD -> sb.append("MOD\n");
                case BytecodeConstants.Opcode.EQ -> sb.append("EQ\n");
                case BytecodeConstants.Opcode.NEQ -> sb.append("NEQ\n");
                case BytecodeConstants.Opcode.LT -> sb.append("LT\n");
                case BytecodeConstants.Opcode.LTE -> sb.append("LTE\n");
                case BytecodeConstants.Opcode.GT -> sb.append("GT\n");
                case BytecodeConstants.Opcode.GTE -> sb.append("GTE\n");
                case BytecodeConstants.Opcode.AND -> sb.append("AND\n");
                case BytecodeConstants.Opcode.OR -> sb.append("OR\n");
                case BytecodeConstants.Opcode.NOT -> sb.append("NOT\n");
                case BytecodeConstants.Opcode.JMP -> {
                    int rel = ((code[pos] & 0xFF) << 8) | (code[pos + 1] & 0xFF);
                    sb.append("JMP ").append(rel).append("\n");
                    pos += 2;
                }
                case BytecodeConstants.Opcode.JMP_IF_FALSE -> {
                    int rel = ((code[pos] & 0xFF) << 8) | (code[pos + 1] & 0xFF);
                    sb.append("JMP_IF_FALSE ").append(rel).append("\n");
                    pos += 2;
                }
                case BytecodeConstants.Opcode.CALL -> {
                    int idx = ((code[pos] & 0xFF) << 8) | (code[pos + 1] & 0xFF);
                    sb.append("CALL ").append(idx).append("\n");
                    pos += 2;
                }
                case BytecodeConstants.Opcode.RET -> sb.append("RET\n");
                case BytecodeConstants.Opcode.HALT -> sb.append("HALT\n");
                case BytecodeConstants.Opcode.NEW_ARRAY -> {
                    int size = ((code[pos] & 0xFF) << 8) | (code[pos + 1] & 0xFF);
                    byte typeId = code[pos + 2];
                    sb.append("NEW_ARRAY size=").append(size).append(", typeId=").append(typeId).append("\n");
                    pos += 3;
                }
                case BytecodeConstants.Opcode.GET_ARRAY -> sb.append("GET_ARRAY\n");
                case BytecodeConstants.Opcode.SET_ARRAY -> sb.append("SET_ARRAY\n");
                case BytecodeConstants.Opcode.INTRINSIC_CALL -> {
                    int idx = ((code[pos] & 0xFF) << 8) | (code[pos + 1] & 0xFF);
                    sb.append("INTRINSIC_CALL ").append(idx).append("\n");
                    pos += 2;
                }
                default -> sb.append("UNKNOWN_OPCODE 0x").append(String.format("%02X", opcode)).append("\n");
            }
        }
        return sb.toString();
    }
} 