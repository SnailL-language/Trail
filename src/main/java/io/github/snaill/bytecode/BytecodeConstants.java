package io.github.snaill.bytecode;

/**
 * Константы, используемые при генерации байткода.
 */
public class BytecodeConstants {
    /**
     * Магическое число для идентификации файла байткода SnailVM
     */
    public static final byte[] MAGIC_NUMBER = {0x53, 0x4E, 0x41, 0x31}; // "SNA1"

    /**
     * Текущая версия формата байткода
     */
    public static final short CURRENT_VERSION = 1;

    /**
     * Идентификаторы типов
     */
    public static class TypeId {
        public static final byte VOID = 0x00;
        public static final byte I32 = 0x01;
        public static final byte USIZE = 0x02;
        public static final byte STRING = 0x03;
        public static final byte ARRAY = 0x04;
    }

    /**
     * Опкоды инструкций
     */
    public static class Opcode {
        // Операции со стеком и памятью
        public static final byte PUSH_CONST = 0x01;
        public static final byte PUSH_LOCAL = 0x02;
        public static final byte PUSH_GLOBAL = 0x03;
        public static final byte STORE_LOCAL = 0x04;
        public static final byte STORE_GLOBAL = 0x05;
        public static final byte POP = 0x06;

        // Арифметические и логические операции
        public static final byte ADD = 0x10;
        public static final byte SUB = 0x11;
        public static final byte MUL = 0x12;
        public static final byte DIV = 0x13;
        public static final byte MOD = 0x14;
        public static final byte EQ = 0x20;
        public static final byte NEQ = 0x21;
        public static final byte LT = 0x22;
        public static final byte LTE = 0x23;
        public static final byte GT = 0x24;
        public static final byte GTE = 0x25;
        public static final byte AND = 0x26;
        public static final byte OR = 0x27;
        public static final byte NOT = 0x28;

        // Операции управления потоком
        public static final byte JMP = 0x30;
        public static final byte JMP_IF_FALSE = 0x31;
        public static final byte CALL = 0x32;
        public static final byte RET = 0x33;
        public static final byte HALT = 0x34;

        // Операции с массивами
        public static final byte NEW_ARRAY = 0x40;
        public static final byte GET_ARRAY = 0x41;
        public static final byte SET_ARRAY = 0x42;

        // Встроенные функции
        public static final byte INTRINSIC_CALL = 0x50;
    }
}