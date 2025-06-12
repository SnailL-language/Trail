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
        public static final byte NOP = 0x00;
        // Операции со стеком и памятью
        public static final byte PUSH_CONST = 0x01;
        public static final byte PUSH_LOCAL = 0x02;
        public static final byte PUSH_GLOBAL = 0x03; // Was 0x04 in old spec, 0x03 is correct
        public static final byte STORE_LOCAL = 0x04; // Was 0x03 in old spec, 0x04 is correct
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
        /** 
         * Безусловный переход на указанное смещение (в байтах) от текущего положения.
         * Аргумент: 2 байта со знаком (big-endian), смещение в байтах относительно текущей позиции
         */
        public static final byte JMP = 0x30;
        
        /**
         * Условный переход, если верхнее значение стека равно 0 (false).
         * Аргумент: 2 байта со знаком (big-endian), смещение в байтах относительно текущей позиции
         */
        public static final byte JMP_IF_FALSE = 0x31;
        
        /**
         * Условный переход, если верхнее значение стека не равно 0 (true).
         * Аргумент: 2 байта со знаком (big-endian), смещение в байтах относительно текущей позиции
         */
        public static final byte JMP_IF_TRUE = 0x35; // Ensure this is present
        
        /**
         * Вызов функции по индексу из таблицы функций.
         * Аргумент: 2 байта (big-endian), индекс функции
         */
        public static final byte CALL = 0x32;
        
        /**
         * Возврат из функции (со значением, если оно есть на стеке).
         */
        public static final byte RET = 0x33;
        
        /**
         * Остановка виртуальной машины.
         */
        public static final byte HALT = 0x34;

        // Операции с массивами
        public static final byte NEW_ARRAY = 0x40;
        public static final byte GET_ARRAY = 0x41;
        public static final byte SET_ARRAY = 0x42;

        /**
         * Инициализирует массив значениями со стека.
         * Аргумент: u16 size - количество элементов для инициализации.
         * Стек: [array_ref, elem_0, ..., elem_{size-1}] -> [array_ref]
         */
        public static final byte INIT_ARRAY = 0x43;

        // Встроенные функции
        public static final byte INTRINSIC_CALL = 0x50;
    }
}