package io.github.snaill.bytecode;

import io.github.snaill.ast.*;
import java.util.*;

/**
 * Контекст генерации байткода. Хранит таблицы констант, глобальных переменных, функций и локальных переменных.
 * Предоставляет методы для получения индексов и добавления новых элементов.
 */
public class BytecodeContext {
    private final List<Object> constants = new ArrayList<>();
    private final Map<Object, Integer> constantIndices = new HashMap<>();
    private final List<String> globalVariables = new ArrayList<>();
    private final Map<String, Integer> globalVarIndices = new HashMap<>();
    private final List<FunctionDeclaration> functions = new ArrayList<>();
    private final Map<String, Integer> functionIndices = new HashMap<>();
    private final Map<FunctionDeclaration, Map<String, Integer>> localVarIndices = new HashMap<>();

    public int addConstant(Object value) {
        if (!constantIndices.containsKey(value)) {
            constants.add(value);
            constantIndices.put(value, constants.size() - 1);
        }
        return constantIndices.get(value);
    }

    public int addGlobalVariable(String name) {
        if (!globalVarIndices.containsKey(name)) {
            globalVariables.add(name);
            globalVarIndices.put(name, globalVariables.size() - 1);
        }
        return globalVarIndices.get(name);
    }

    public int addFunction(FunctionDeclaration func) {
        if (!functionIndices.containsKey(func.getName())) {
            functions.add(func);
            functionIndices.put(func.getName(), functions.size() - 1);
        }
        return functionIndices.get(func.getName());
    }

    public void setLocalVarIndices(FunctionDeclaration func, Map<String, Integer> indices) {
        if (localVarIndices.containsKey(func)) {
            throw new IllegalStateException("Local variable indices already set for function: " + func.getName());
        }
        localVarIndices.put(func, new HashMap<>(indices));
    }

    public int getLocalVarIndex(FunctionDeclaration func, String name) {
        Map<String, Integer> map = localVarIndices.get(func);
        if (map == null || !map.containsKey(name)) return -1;
        return map.get(name);
    }

    public int getGlobalVarIndex(String name) {
        return globalVarIndices.getOrDefault(name, -1);
    }

    public int getConstantIndex(Object value) {
        return constantIndices.getOrDefault(value, -1);
    }

    public int getFunctionIndex(String name) {
        return functionIndices.getOrDefault(name, -1);
    }

    public List<Object> getConstants() { return constants; }
    public List<String> getGlobalVariables() { return globalVariables; }
    public List<FunctionDeclaration> getFunctions() { return functions; }
    public Map<String, Integer> getGlobalVarIndices() { return globalVarIndices; }
    public Map<FunctionDeclaration, Map<String, Integer>> getLocalVarIndices() { return localVarIndices; }
} 