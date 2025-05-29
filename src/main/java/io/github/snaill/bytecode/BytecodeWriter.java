package io.github.snaill.bytecode;

import io.github.snaill.ast.*;
import io.github.snaill.exception.FailedCheckException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Класс для записи байткода в поток.
 * Обеспечивает корректную запись всех компонентов байткода.
 */
public class BytecodeWriter {
    private final BytecodeContext context;
    private final OutputStream out;

    public BytecodeWriter(BytecodeContext context, OutputStream out) {
        this.context = context;
        this.out = out;
    }

    /**
     * Записывает байткод в поток
     */
    public void write(Scope program) throws IOException, FailedCheckException {
        // Записываем магическое число
        out.write(BytecodeConstants.MAGIC_NUMBER);
        
        // Записываем версию
        BytecodeUtils.writeU16(out, BytecodeConstants.CURRENT_VERSION);
        
        // Индекс main (если есть)
        int mainIdx = context.getFunctionIndex("main");
        BytecodeUtils.writeU16(out, mainIdx >= 0 ? mainIdx : 0xFFFF);
        
        // Записываем константы
        writeConstants();
        
        // Записываем глобальные переменные
        writeGlobalVariables();
        
        // Записываем функции
        writeFunctions();
        
        // Записываем глобальный байткод
        writeGlobalBytecode(program);
    }

    /**
     * Записывает пул констант
     */
    private void writeConstants() throws IOException {
        var constants = context.getConstants();
        BytecodeUtils.writeU16(out, constants.size());
        for (Object c : constants) {
            if (c instanceof Long l) {
                out.write(BytecodeConstants.TypeId.I32);
                BytecodeUtils.writeI32(out, l.intValue());
            } else if (c instanceof String s) {
                out.write(BytecodeConstants.TypeId.STRING);
                BytecodeUtils.writeU16(out, s.getBytes(java.nio.charset.StandardCharsets.UTF_8).length);
                out.write(s.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            }
        }
    }

    /**
     * Записывает глобальные переменные
     */
    private void writeGlobalVariables() throws IOException {
        var globals = context.getGlobalVariables();
        BytecodeUtils.writeU16(out, globals.size());
        for (String name : globals) {
            byte[] nameBytes = name.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            out.write((byte) nameBytes.length);
            out.write(nameBytes);
            // Определяем тип переменной по объявлению (если есть)
            byte typeId = BytecodeConstants.TypeId.I32;
            for (FunctionDeclaration func : context.getFunctions()) {
                // Пропускаем функции, ищем VariableDeclaration с таким именем
                // (В реальной реализации нужен отдельный список глобальных переменных с типами)
            }
            out.write(typeId);
        }
    }

    /**
     * Записывает функции
     */
    private void writeFunctions() throws IOException, FailedCheckException {
        var functions = context.getFunctions();
        BytecodeUtils.writeU16(out, functions.size());
        for (FunctionDeclaration func : functions) {
            byte[] nameBytes = func.getName().getBytes(java.nio.charset.StandardCharsets.UTF_8);
            out.write((byte) nameBytes.length);
            out.write(nameBytes);
            out.write((byte) func.getParameters().size());
            out.write(getTypeId(func.getReturnType()));
            BytecodeUtils.writeU16(out, func.getParameters().size()); // локальные переменные = параметры (упрощённо)
            // Генерируем байткод функции
            ByteArrayOutputStream funcOut = new ByteArrayOutputStream();
            func.getBody().emitBytecode(funcOut, context);
            byte[] code = funcOut.toByteArray();
            BytecodeUtils.writeI32(out, code.length);
            out.write(code);
        }
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

    private void writeGlobalBytecode(Scope program) throws IOException, FailedCheckException {
        ByteArrayOutputStream globalOut = new ByteArrayOutputStream();
        program.emitBytecode(globalOut, context);
        byte[] code = globalOut.toByteArray();
        BytecodeUtils.writeI32(out, code.length);
        out.write(code);
    }
} 