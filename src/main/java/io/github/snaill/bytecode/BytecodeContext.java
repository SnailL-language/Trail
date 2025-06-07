package io.github.snaill.bytecode;

import io.github.snaill.ast.*;
import java.util.*;

/**
 * Context for bytecode generation. Stores tables of constants, global variables, functions, and local variables.
 * Provides methods for getting indices and adding new elements.
 */
public class BytecodeContext {
    private final List<Object> constants = new ArrayList<>();
    private final Map<Object, Integer> constantIndices = new HashMap<>();
    private final List<String> globalVariables = new ArrayList<>();
    private final Map<String, Integer> globalVarIndices = new HashMap<>();
    /**
     * List of all global expressions and declarations
     */
    private List<Statement> globalStatements = new ArrayList<>();
    
    /**
     * Function table containing all declared functions
     */
    private final List<FunctionDeclaration> functionTable = new ArrayList<>();
    
    /**
     * Function indices in the function table, for fast access by name
     */
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

    /**
     * Adds a function to the function table and returns its index
     *
     * @param func function to add
     */
    public void addFunction(FunctionDeclaration func) {
        if (!functionIndices.containsKey(func.getName())) {
            functionTable.add(func);
            functionIndices.put(func.getName(), functionTable.size() - 1);
        }
        functionIndices.get(func.getName());
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

    /**
     * Adds a global expression to the list
     * @param stmt global expression or declaration
     */
    public void addGlobalStatement(Statement stmt) {
        globalStatements.add(stmt);
    }
    
    /**
     * Sets the list of global expressions
     * @param statements list of global expressions
     */
    public void setGlobalStatements(List<Statement> statements) {
        this.globalStatements = new ArrayList<>(statements);
    }
    
    /**
     * Gets the list of global expressions
     * @return list of global expressions
     */
    public List<Statement> getGlobalStatements() {
        return globalStatements;
    }
    
    public List<Object> getConstants() { return constants; }
    public List<String> getGlobalVariables() { return globalVariables; }
    /**
     * Returns the function table
     * @return list of functions
     */
    public List<FunctionDeclaration> getFunctions() { return functionTable; }
    public Map<String, Integer> getGlobalVarIndices() { return globalVarIndices; }
    public Map<FunctionDeclaration, Map<String, Integer>> getLocalVarIndices() { return localVarIndices; }
} 