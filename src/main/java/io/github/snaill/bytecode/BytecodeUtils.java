package io.github.snaill.bytecode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Утилитарные методы для работы с байтами в SnailVM.
 */
public class BytecodeUtils {
    /**
     * Записывает 8-битное беззнаковое значение.
     *
     * @param out поток для записи
     * @param value значение
     * @throws IOException если произошла ошибка ввода-вывода
     */
    public static void writeU8(ByteArrayOutputStream out, int value) throws IOException {
        out.write((byte) value);
    }

    /**
     * Записывает 16-битное беззнаковое значение в big-endian формате.
     *
     * @param out поток для записи
     * @param value значение
     * @throws IOException если произошла ошибка ввода-вывода
     */
    public static void writeU16(ByteArrayOutputStream out, int value) throws IOException {
        out.write(new byte[]{
                (byte) ((value >> 8) & 0xFF),
                (byte) (value & 0xFF)
        });
    }

    /**
     * Записывает 32-битное беззнаковое значение в big-endian формате.
     *
     * @param out поток для записи
     * @param value значение
     * @throws IOException если произошла ошибка ввода-вывода
     */
    public static void writeU32(ByteArrayOutputStream out, int value) throws IOException {
        out.write(new byte[]{
                (byte) ((value >> 24) & 0xFF),
                (byte) ((value >> 16) & 0xFF),
                (byte) ((value >> 8) & 0xFF),
                (byte) (value & 0xFF)
        });
    }

    /**
     * Записывает 32-битное знаковое значение в big-endian формате.
     *
     * @param out поток для записи
     * @param value значение
     * @throws IOException если произошла ошибка ввода-вывода
     */
    public static void writeI32(ByteArrayOutputStream out, long value) throws IOException {
        out.write(new byte[]{
                (byte) ((value >> 24) & 0xFF),
                (byte) ((value >> 16) & 0xFF),
                (byte) ((value >> 8) & 0xFF),
                (byte) (value & 0xFF)
        });
    }
}