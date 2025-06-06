package io.github.snaill.ast;

import io.github.snaill.bytecode.BytecodeContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.snaill.result.CompilationError;
import io.github.snaill.result.ErrorType;
import io.github.snaill.result.Result;
import io.github.snaill.result.Warning;
import io.github.snaill.result.WarningType;

/**
 * Представляет блок кода (scope) в AST.
 * Генерирует байткод для всех операторов в блоке.
 */
public class Scope extends AbstractNode implements Statement /*, BytecodeEmittable */ {
    private static final Logger logger = LoggerFactory.getLogger(Scope.class);
    private final Scope parent;
    private final FunctionDeclaration enclosingFunction;
    private final List<VariableDeclaration> localDeclarations = new ArrayList<>();

    public Scope(List<Statement> children) {
        this(children, null, null);
    }

    public Scope(List<Statement> children, Scope parent) {
        this(children, parent, null);
    }

    public Scope(List<Statement> children, Scope parent, FunctionDeclaration enclosingFunction) {
        super(children);
        this.parent = parent;
        this.enclosingFunction = enclosingFunction;
    }

    public Scope getParent() {
        return parent;
    }

    public FunctionDeclaration getEnclosingFunction() {
        return this.enclosingFunction;
    }

    /**
     * Adds a variable declaration to this scope. 
     * Checks for redeclaration within the same scope.
     * @param decl The VariableDeclaration to add.
     * @throws io.github.snaill.exception.FailedCheckException if the variable is already declared in this scope.
     */
    public void addDeclaration(VariableDeclaration decl) throws io.github.snaill.exception.FailedCheckException {
        for (VariableDeclaration existingDecl : this.localDeclarations) {
            if (existingDecl.getName().equals(decl.getName())) {
                String before = decl.getSource() != null ?
                    io.github.snaill.ast.SourceBuilder.toSourceLine(decl.getSource(), decl.getLine(), decl.getCharPosition(), decl.getName().length()) :
                    io.github.snaill.ast.SourceBuilder.toSourceCode(decl);
                throw new io.github.snaill.exception.FailedCheckException(
                    new io.github.snaill.result.CompilationError(
                        io.github.snaill.result.ErrorType.REDECLARED_VARIABLE,
                        before,
                        "Variable '" + decl.getName() + "' is already declared in this scope.",
                        "Previously declared at line " + existingDecl.getLine() 
                    ).toString()
                );
            }
        }
        this.localDeclarations.add(decl);
        decl.setEnclosingScope(this); // Ensure the declaration knows its scope
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);

    }

    @Override
    public void setChildren(Collection<Node> children) {
        List<Statement> statements = new ArrayList<>(children.size());
        for (Node n : children) {
            if (n instanceof Statement) {
                statements.add((Statement) n);
            } else {
                throw new IllegalArgumentException(
                    "Scope children must be of type Statement. Found: " +
                    (n != null ? n.getClass().getName() : "null")
                );
            }
        }
        // Safely pass to superclass by creating a new List<Node>
        super.setChildren(new ArrayList<>(statements));
    }

    @Override
    public List<Node> getChildren() {
        // Возвращаем как List<Node>, но реально это List<Statement>
        return new ArrayList<>(super.getChildren());
    }

    public List<Statement> getStatements() {
        return super.getChildren().stream().map(Statement.class::cast).toList();
    }

    @Override
    public void emitBytecode(ByteArrayOutputStream out, BytecodeContext context) throws IOException, io.github.snaill.exception.FailedCheckException {
        emitBytecode(out, context, null);
    }

    public void emitBytecode(ByteArrayOutputStream out, BytecodeContext context, FunctionDeclaration currentFunction) throws IOException, io.github.snaill.exception.FailedCheckException {
        for (Statement stmt : getStatements()) {
            try {
                stmt.getClass().getMethod("emitBytecode", ByteArrayOutputStream.class, BytecodeContext.class, FunctionDeclaration.class)
                    .invoke(stmt, out, context, currentFunction);
            } catch (NoSuchMethodException e) {
                stmt.emitBytecode(out, context);
            } catch (Exception e) {
                throw new IOException(e);
            }
        }
    }

    @Override
    public void checkUnusedVariables(Set<VariableDeclaration> unused) {
        super.checkUnusedVariables(unused);
    }

    @Override
    public void checkUnusedFunctions(Set<FunctionDeclaration> unused) {
        super.checkUnusedFunctions(unused);
    }

    @Override
    public void check() throws io.github.snaill.exception.FailedCheckException {
        boolean currentSegmentIsDead = false;
        for (int i = 0; i < getChildren().size(); i++) {
            Node childNode = getChildren().get(i);
            if (currentSegmentIsDead) {
                // This childNode is the first statement in a dead code segment within this scope.
                // Report it if it hasn't been reported by another analysis pass (e.g., checkDeadCode).
                if (childNode instanceof AbstractNode an && !an.wasDeadCodeReported) {
                    String sourceCode = io.github.snaill.ast.SourceBuilder.toSourceCode(childNode);
                    // Using System.out.print as per original behavior for this specific check method.
                    // A more robust solution would use the CompilationError and Result system.
                    // This specific print is for a different kind of dead code check than the one Trail.java handles.
                    System.out.print("ERROR:" + sourceCode + ";"); 
                    markSubtreeAsDeadCodeReported(childNode); // Mark this and its children as reported.
                }
                // After the first dead statement is found and reported by check(), stop further checks in this scope.
                // The checkDeadCode() method (called from Trail.java) will handle more detailed dead code analysis and reporting via CompilationError.
                break; 
            }

            // Check the child itself (will throw FailedCheckException if there's an error within it)
            childNode.check();

            // Determine if this childNode makes the rest of the current scope dead
            if (childNode instanceof ReturnStatement || childNode instanceof BreakStatement) {
                currentSegmentIsDead = true;
            } else if (childNode instanceof IfStatement ifStmt) {
                // An if-else makes the subsequent code dead if both branches end with return/break.
                if (ifStmt.isHasElse() && 
                    endsWithReturnOrBreak(ifStmt.getBody()) && 
                    endsWithReturnOrBreak(ifStmt.getElseBody())) {
                    currentSegmentIsDead = true;
                }
            } else if (childNode instanceof Scope subScope) {
                // A sub-scope makes the subsequent code dead if it ends with return/break.
                if (endsWithReturnOrBreak(subScope)) {
                    currentSegmentIsDead = true;
                }
            }
        }
        // Unused symbol checking is now handled by getUnusedSymbolWarnings() and invoked from Trail.java
    }

    /**
     * Collects warnings for unused global variables and functions.
     * This method should typically be called on the root scope.
     * @return A list of Warning objects for unused symbols.
     */
    public List<Warning> getUnusedSymbolWarnings() {
        List<Warning> warnings = new ArrayList<>();
        // Variables declared in *this* scope (includes globals if this is root scope)
        // For function scopes, localDeclarations should also include parameters (to be fixed in ASTBuilder).
        Set<VariableDeclaration> potentiallyUnusedLocals = new java.util.HashSet<>(this.localDeclarations);

        System.out.println("FORCED_DEBUG_UNUSED: Scope " + this.hashCode() + " (Parent: " + (this.parent != null ? this.parent.hashCode() : "ROOT") + ") processing. Initial potentiallyUnusedLocals for this scope: " + potentiallyUnusedLocals.stream().map(vd -> vd.getName() + "(" + vd.hashCode() + ")").collect(java.util.stream.Collectors.toList()));

        // Potentially unused global functions. This set is built by the root scope
        // and passed down so FunctionCalls anywhere can mark them as used.
        Set<FunctionDeclaration> potentiallyUnusedGlobalFunctions = new java.util.HashSet<>();
        if (this.parent == null) { // If this is the root scope, collect global functions
            for (Node child : getChildren()) {
                if (child instanceof FunctionDeclaration fn && !fn.getName().equals("main")) {
                    potentiallyUnusedGlobalFunctions.add(fn);
                }
            }
            logger.debug("DEBUG_UNUSED: Root scope collected potentiallyUnusedGlobalFunctions: {}", 
                potentiallyUnusedGlobalFunctions.stream().map(fd -> fd.getName() + "(" + fd.hashCode() + ")").toList());
        }

        // Traverse children of *this* current scope to mark its locals (potentiallyUnusedLocals)
        // and global functions (potentiallyUnusedGlobalFunctions, if this is root) as used.
        // If this is not the root scope, potentiallyUnusedGlobalFunctions will be empty but passed along.
        collectUsageInSubtree(this, potentiallyUnusedLocals, potentiallyUnusedGlobalFunctions);
        
        logger.debug("DEBUG_UNUSED: Scope {} after collectUsageInSubtree. Remaining potentiallyUnusedLocals: {}", 
            this.hashCode(), 
            potentiallyUnusedLocals.stream().map(vd -> vd.getName() + "(" + vd.hashCode() + ")").toList());
        if (this.parent == null) {
             logger.debug("DEBUG_UNUSED: Root scope after collectUsageInSubtree. Remaining potentiallyUnusedGlobalFunctions: {}", 
                potentiallyUnusedGlobalFunctions.stream().map(fd -> fd.getName() + "(" + fd.hashCode() + ")").toList());
        }

        // Add warnings for unused local variables of *this* scope.
        // If this is the root scope, these are global variables.
        for (VariableDeclaration localVar : potentiallyUnusedLocals) {
            warnings.add(new Warning(WarningType.UNUSED, SourceBuilder.toSourceCode(localVar)));
            logger.debug("DEBUG_UNUSED: Scope {} reporting UNUSED VAR: {} (Hash: {}) Type: {}", 
                this.hashCode(), localVar.getName(), localVar.hashCode(), (this.parent == null ? "Global" : "Local"));
        }

        // If this is the root scope, add warnings for any remaining unused global functions.
        if (this.parent == null) {
            for (FunctionDeclaration fn : potentiallyUnusedGlobalFunctions) {
                warnings.add(new Warning(WarningType.UNUSED, SourceBuilder.toSourceCode(fn)));
                 logger.debug("DEBUG_UNUSED: Root Scope reporting UNUSED FUNCTION: {} (Hash: {})", 
                    fn.getName(), fn.hashCode());
            }
        }

        // Recursively collect warnings from child scopes that define their own locals.
        for (Node childNode : getChildren()) {
            if (childNode instanceof FunctionDeclaration) {
                Scope functionBody = ((FunctionDeclaration) childNode).getBody();
                if (functionBody != null) {
                    // Pass down the global functions set for checking usage within the function body.
                    // The function body will handle its own local variables.
                    warnings.addAll(functionBody.getUnusedSymbolWarnings(potentiallyUnusedGlobalFunctions));
                }
            } else if (childNode instanceof Scope && childNode != this) { // General nested block
                warnings.addAll(((Scope) childNode).getUnusedSymbolWarnings(potentiallyUnusedGlobalFunctions));
            } else if (childNode instanceof IfStatement ifStmt) { 
                 if (ifStmt.getBody() != null) warnings.addAll(ifStmt.getBody().getUnusedSymbolWarnings(potentiallyUnusedGlobalFunctions));
                 if (ifStmt.getElseBody() != null) warnings.addAll(ifStmt.getElseBody().getUnusedSymbolWarnings(potentiallyUnusedGlobalFunctions));
            }
            // TODO: Add other control structures like WhileStatement, ForStatement if their bodies are Scopes
            // and need to be processed for their own local unused variables.
        }
        return warnings;
    }

    // Overloaded version for recursive calls, carrying the global functions set.
    private List<Warning> getUnusedSymbolWarnings(Set<FunctionDeclaration> globalFunctionsToTrack) {
        List<Warning> warnings = new ArrayList<>();
        Set<VariableDeclaration> potentiallyUnusedLocals = new java.util.HashSet<>(this.localDeclarations);
        logger.debug("DEBUG_UNUSED: Scope {} (Parent: {}) recursive call. Initial potentiallyUnusedLocals for this scope: {}", 
            this.hashCode(), (this.parent != null ? this.parent.hashCode() : "ERROR_SHOULD_HAVE_PARENT"), 
            potentiallyUnusedLocals.stream().map(vd -> vd.getName() + "(" + vd.hashCode() + ")").toList());

        // Use the passed-in set of global functions for usage marking.
        collectUsageInSubtree(this, potentiallyUnusedLocals, globalFunctionsToTrack);

        logger.debug("DEBUG_UNUSED: Scope {} after collectUsageInSubtree (recursive call). Remaining potentiallyUnusedLocals: {}", 
            this.hashCode(), 
            potentiallyUnusedLocals.stream().map(vd -> vd.getName() + "(" + vd.hashCode() + ")").toList());

        for (VariableDeclaration localVar : potentiallyUnusedLocals) {
            warnings.add(new Warning(WarningType.UNUSED, SourceBuilder.toSourceCode(localVar)));
            logger.debug("DEBUG_UNUSED: Scope {} reporting UNUSED VAR: {} (Hash: {}) Type: Local (recursive call)", 
                this.hashCode(), localVar.getName(), localVar.hashCode());
        }

        for (Node childNode : getChildren()) {
            if (childNode instanceof FunctionDeclaration) {
                Scope functionBody = ((FunctionDeclaration) childNode).getBody();
                if (functionBody != null) {
                    warnings.addAll(functionBody.getUnusedSymbolWarnings(globalFunctionsToTrack));
                }
            } else if (childNode instanceof Scope && childNode != this) {
                warnings.addAll(((Scope) childNode).getUnusedSymbolWarnings(globalFunctionsToTrack));
            } else if (childNode instanceof IfStatement ifStmt) {
                 if (ifStmt.getBody() != null) warnings.addAll(ifStmt.getBody().getUnusedSymbolWarnings(globalFunctionsToTrack));
                 if (ifStmt.getElseBody() != null) warnings.addAll(ifStmt.getElseBody().getUnusedSymbolWarnings(globalFunctionsToTrack));
            }
        }
        return warnings;
    }

    // Traverses the direct children of `scopeNode` to mark usage of variables 
    // in `activeUnusedVarsForScope` and functions in `activeUnusedGlobalFunctions`.
    private void collectUsageInSubtree(Scope scopeNode, Set<VariableDeclaration> activeUnusedVarsForScope, Set<FunctionDeclaration> activeUnusedGlobalFunctions) {
        for (Node child : scopeNode.getChildren()) { // Iterate over statements within this scope.
            if (child != null) {
                // Let the child statement and its descendants mark usage.
                traverseForUsageMarking(child, activeUnusedVarsForScope, activeUnusedGlobalFunctions);
            }
        }
    }

    // Recursive helper for collectUsageInSubtree
    private void traverseForUsageMarking(Node currentNode, Set<VariableDeclaration> activeUnusedVars, Set<FunctionDeclaration> activeUnusedFunctions) {
        // The node itself (e.g., a VariableReference or FunctionCall) gets a chance to mark usage.
        currentNode.checkUnusedVariables(activeUnusedVars);
        currentNode.checkUnusedFunctions(activeUnusedFunctions);

        // Recurse for children of currentNode.
        // This ensures that expressions within statements, etc., are checked.
        for (Node childNodeComponent : currentNode.getChildren()) {
            if (childNodeComponent != null) {
                 // If a child component is a FunctionDeclaration or a Scope, we do NOT recursively call 
                 // getUnusedSymbolWarnings here. That's handled by the main loop in getUnusedSymbolWarnings.
                 // We just want this subtree to mark usage against the *current* active sets.
                 traverseForUsageMarking(childNodeComponent, activeUnusedVars, activeUnusedFunctions);
            }
        }
    }

    private void markSubtreeAsDeadCodeReported(Node node) {
        if (node instanceof AbstractNode abstractNode) {
            abstractNode.wasDeadCodeReported = true;
        }
        for (Node child : node.getChildren()) {
            markSubtreeAsDeadCodeReported(child);
        }
    }

    private boolean endsWithReturnOrBreak(Scope scope) {
        if (scope == null || scope.getChildren().isEmpty()) {
            return false;
        }
        List<Node> stmts = scope.getChildren();
        Node lastEffectiveStatement = null;
        // Iterate backwards to find the last actual statement
        for (int i = stmts.size() - 1; i >= 0; i--) {
            Node n = stmts.get(i);
            // Skip empty scopes if they are considered non-effective
            if (n instanceof Scope && ((Scope)n).getChildren().isEmpty()) {
                continue;
            }
            if (n instanceof Statement) { 
                 lastEffectiveStatement = n;
                 break;
            }
        }

        if (lastEffectiveStatement == null) return false; // No effective statements found

        if (lastEffectiveStatement instanceof ReturnStatement || lastEffectiveStatement instanceof BreakStatement) {
            return true;
        }
        // If the last statement is an IfStatement, it must have an else, and both branches must end with return/break
        if (lastEffectiveStatement instanceof IfStatement ifs) {
            return ifs.isHasElse() &&
                   endsWithReturnOrBreak(ifs.getBody()) &&
                   endsWithReturnOrBreak(ifs.getElseBody());
        }
        // If the last statement is a non-empty scope, recurse into it
        if (lastEffectiveStatement instanceof Scope s && !s.getChildren().isEmpty()) {
            return endsWithReturnOrBreak(s);
        }
        // Other statements (loops, assignments, etc.) don't inherently make the block end with return/break by themselves.
        return false;
    }

    @Override
    public Node getParentNode() {
        return parent;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Scope) {
            return super.equals(obj);
        }
        return false;
    }

    @Override
    public List<Result> checkDeadCode() {
        List<Result> results = new ArrayList<>();
        boolean currentSegmentIsDead = false;

        for (Node childNode : getChildren()) {
            if (!(childNode instanceof Statement stmt)) {
                continue; 
            }
            AbstractNode abstractStmt = (stmt instanceof AbstractNode) ? (AbstractNode) stmt : null;

            if (currentSegmentIsDead) {
                if (abstractStmt != null && !abstractStmt.wasDeadCodeReported) {
                    String beforeString = abstractStmt != null && abstractStmt.getSource() != null ? 
                                          SourceBuilder.toSourceLine(abstractStmt.getSource(), abstractStmt.getLine(), abstractStmt.getCharPosition(), SourceBuilder.toSourceCode(abstractStmt).length()) :
                                          SourceBuilder.toSourceCode(stmt);
                    CompilationError err = new CompilationError(
                        ErrorType.DEAD_CODE,
                        beforeString,
                        "Statement is unreachable.",
                        ""
                    );
                    results.add(err);
                    markSubtreeAsDeadCodeReported(abstractStmt); 
                }
            } else {
                results.addAll(stmt.checkDeadCode());

                if (stmt instanceof ReturnStatement || stmt instanceof BreakStatement) {
                    currentSegmentIsDead = true;
                } else if (stmt instanceof IfStatement ifStmt) {
                    if (ifStmt.isHasElse() && 
                        ifStmt.getBody() != null && endsWithReturnOrBreak(ifStmt.getBody()) && 
                        ifStmt.getElseBody() != null && endsWithReturnOrBreak(ifStmt.getElseBody())) {
                        currentSegmentIsDead = true;
                    }
                } else if (stmt instanceof Scope subScope) {
                    if (endsWithReturnOrBreak(subScope)) {
                        currentSegmentIsDead = true;
                    }
                }
            }
        }
        return results;
    }

    public VariableDeclaration resolveVariable(String name) {
        return resolveVariable(name, null); // Call the more specific version with null context
    }

    /**
     * Resolves a variable considering the context of resolution, e.g., to prevent self-reference in initializers.
     * @param name The name of the variable to resolve.
     * @param resolutionContext The AST Node from where the resolution is being requested.
     * @return The VariableDeclaration if found, otherwise null.
     */
    public VariableDeclaration resolveVariable(String name, Node resolutionContext) {
        logger.debug("DEBUG: Resolving variable: {} with context: {}", name, resolutionContext != null ? resolutionContext.getClass().getSimpleName() : "null");

        // Check parameters if in a function scope
        FunctionDeclaration func = getEnclosingFunction();
        if (func != null) {
            for (Parameter param : func.getParameters()) {
                if (param.getName().equals(name)) {
                    logger.debug("DEBUG: Found parameter {} in function {}", name, func.getName());
                    // Create a temporary VariableDeclaration for the parameter
                    VariableDeclaration paramVarDecl = new VariableDeclaration(param.getName(), param.getType(), null);
                    // If Parameter has source location, and VariableDeclaration needs it set explicitly:
                    if (param instanceof AbstractNode pn) {
                        paramVarDecl.setSourceInfo(pn.getLine(), pn.getCharPosition(), pn.getSource()); // Corrected to setSourceInfo
                    }
                    return paramVarDecl;
                }
            }
        }

        // Check local variables in the current scope (this.localDeclarations)
        for (VariableDeclaration varDecl : this.localDeclarations) {
            if (varDecl.getName().equals(name)) {
                logger.debug("DEBUG: Found variable {} in localDeclarations of current scope", name);
                // Prevent resolving a variable in its own initializer if resolutionContext is that initializer.
                // The resolutionContext is the Expression node being resolved.
                // varDecl.getValue() is the initializer expression of the declaration.
                if (resolutionContext != null && varDecl.getValue() == resolutionContext) {
                     logger.debug("DEBUG: Attempt to resolve variable {} in its own initializer. Denied.", name);
                     continue; // Skip this declaration, look in parent or for other declarations.
                }
                return varDecl;
            }
        }

        // Recursively check parent scope
        if (parent != null) {
            return parent.resolveVariable(name, resolutionContext);
        } else {
            // This is the root scope, check its direct children for global variables
            for (Node child : getChildren()) {
                if (child instanceof VariableDeclaration globalVarDecl && globalVarDecl.getName().equals(name)) {
                    logger.debug("DEBUG: Found global variable {} in root scope children", name);
                    // Prevent resolving a variable in its own initializer (though less common for globals defined at top level)
                    if (resolutionContext != null && globalVarDecl.getValue() == resolutionContext) {
                        logger.debug("DEBUG: Attempt to resolve global variable {} in its own initializer. Denied.", name);
                        continue; 
                    }
                    return globalVarDecl;
                }
            }
        }

        return null;
    }

    public FunctionDeclaration resolveFunction(String name) {
        for (Node child : getChildren()) {
            if (child instanceof FunctionDeclaration func && func.getName().equals(name)) {
                return func;
            }
        }
        if (parent != null) return parent.resolveFunction(name);
        return null;
    }
}
