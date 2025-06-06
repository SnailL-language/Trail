package io.github.snaill.bytecode;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Утилиты для работы с байткодом.
 */
public class BytecodeUtils {
    /**
     * Записывает 8-битное беззнаковое целое число
     */
    public static void writeU8(OutputStream out, int value) throws IOException {
        out.write(value & 0xFF);
    }

    /**
     * Записывает 16-битное беззнаковое целое число
     */
    public static void writeU16(OutputStream out, int value) throws IOException {
        out.write((value >> 8) & 0xFF);
        out.write(value & 0xFF);
    }

    /**
     * Записывает 32-битное целое число со знаком
     */
    public static void writeI32(OutputStream out, int value) throws IOException {
        out.write((value >> 24) & 0xFF);
        out.write((value >> 16) & 0xFF);
        out.write((value >> 8) & 0xFF);
        out.write(value & 0xFF);
    }

    /**
     * Записывает 64-битное число с плавающей точкой
     */
    public static void writeF64(OutputStream out, double value) throws IOException {
        byte[] bytes = ByteBuffer.allocate(8).putDouble(value).array();
        out.write(bytes);
    }

    /**
     * Записывает строку в формате UTF-8 с длиной
     */
    public static void writeString(OutputStream out, String value) throws IOException {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        writeU16(out, bytes.length);
        out.write(bytes);
    }

    /**
     * Читает 8-битное беззнаковое целое число
     */
    public static int readU8(byte[] bytes, int offset) {
        return bytes[offset] & 0xFF;
    }

    /**
     * Читает 16-битное беззнаковое целое число
     */
    public static int readU16(byte[] bytes, int offset) {
        return ((bytes[offset] & 0xFF) << 8) | (bytes[offset + 1] & 0xFF);
    }

    /**
     * Читает 32-битное целое число со знаком
     */
    public static int readI32(byte[] bytes, int offset) {
        return ((bytes[offset] & 0xFF) << 24) |
               ((bytes[offset + 1] & 0xFF) << 16) |
               ((bytes[offset + 2] & 0xFF) << 8) |
               (bytes[offset + 3] & 0xFF);
    }

    /**
     * Читает 64-битное число с плавающей точкой
     */
    public static double readF64(byte[] bytes, int offset) {
        return ByteBuffer.wrap(bytes, offset, 8).getDouble();
    }

    /**
     * Читает строку в формате UTF-8 с длиной
     */
    public static String readString(byte[] bytes, int offset) {
        int length = readU16(bytes, offset);
        return new String(bytes, offset + 2, length, StandardCharsets.UTF_8);
    }

    /**
     * Обновляет 16-битное беззнаковое целое число в массиве байт по указанному смещению.
     */
    public static void patchU16(byte[] bytes, int offset, int value) {
        if (bytes == null || offset < 0 || offset + 1 >= bytes.length) {
            throw new IllegalArgumentException("Invalid arguments for patchU16");
        }
        bytes[offset] = (byte) ((value >> 8) & 0xFF);
        bytes[offset + 1] = (byte) (value & 0xFF);
    }
}