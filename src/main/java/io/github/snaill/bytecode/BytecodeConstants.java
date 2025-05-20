package io.github.snaill.bytecode;

/**
 * Константы для SnailVM, включая опкоды, типы и магическое число.
 */
public class BytecodeConstants {
    /** Магическое число для идентификации файла SnailVM (SNA1). */
    public static final byte[] MAGIC_NUMBER = { 0x53, 0x4E, 0x41, 0x31 };

    /** Текущая версия спецификации байткода */
    public static final int CURRENT_VERSION = 1;

    /**
     * Типы данных SnailVM.
     */
    public enum TypeId {
        VOID(0x00),
        I32(0x01),
        USIZE(0x02),
        STRING(0x03),
        ARRAY(0x04);

        private final byte value;

        TypeId(int value) {
            this.value = (byte) value;
        }

        public byte getValue() {
            return value;
        }
    }

    /**
     * Опкоды инструкций SnailVM.
     */
    public enum Opcode {
        PUSH_CONST(0x01),
        PUSH_LOCAL(0x02),
        STORE_LOCAL(0x03),
        POP(0x04),
        ADD(0x10),
        SUB(0x11),
        MUL(0x12),
        DIV(0x13),
        MOD(0x14),
        EQ(0x20),
        NEQ(0x21),
        LT(0x22),
        LTE(0x23),
        GT(0x24),
        GTE(0x25),
        AND(0x26),
        OR(0x27),
        NOT(0x28),
        JMP(0x30),
        JMP_IF_FALSE(0x31),
        CALL(0x32),
        RET(0x33),
        HALT(0x34),
        NEW_ARRAY(0x40),
        GET_ARRAY(0x41),
        SET_ARRAY(0x42),
        INTRINSIC_CALL(0x50), // Новый опкод для вызова встроенных функций
        NOOP(0xFF);

        private final byte value;

        Opcode(int value) {
            this.value = (byte) value;
        }

        public byte getValue() {
            return value;
        }
    }
}