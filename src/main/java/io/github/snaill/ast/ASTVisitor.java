package io.github.snaill.ast;

import java.io.IOException;

/**
 * Visitor interface for the Snail AST.
 * Defines visit methods for each concrete AST node type.
 * @param <T> The return type of the visit methods.
 */
public interface ASTVisitor<T> {
    // T visit(ProgramNode node) throws IOException; // Root node will be a Scope
    T visit(Scope scopeNode) throws IOException;
    T visit(FunctionDeclaration funcDeclNode) throws IOException;
    T visit(VariableDeclaration varDeclNode) throws IOException;
    T visit(Parameter paramNode) throws IOException;
    T visit(IfStatement ifStmtNode) throws IOException;
    T visit(WhileLoop whileLoopNode) throws IOException;
    T visit(ForLoop forLoopNode) throws IOException;
    T visit(ReturnStatement retStmtNode) throws IOException;
    T visit(BreakStatement breakStmtNode) throws IOException;
    T visit(AssignmentExpression assignmentExprNode) throws IOException; // Corrected typo
    T visit(BinaryExpression binExprNode) throws IOException;
    T visit(UnaryExpression unExprNode) throws IOException;
    T visit(FunctionCall funcCallNode) throws IOException;
    T visit(Identifier idNode) throws IOException;
    T visit(NumberLiteral numLitNode) throws IOException;
    T visit(StringLiteral strLitNode) throws IOException;
    T visit(BooleanLiteral boolLitNode) throws IOException;
    T visit(ArrayLiteral arrayLitNode) throws IOException;
    T visit(ArrayElement arrayElNode) throws IOException;
    T visit(VariableReference variableReferenceNode) throws IOException;
    T visit(ArrayAssignment arrayAssignmentNode) throws IOException;
    T visit(ArrayAccess arrayAccessNode) throws IOException;
    T visit(PrimitiveType primitiveTypeNode) throws IOException;
    T visit(ArrayType arrayTypeNode) throws IOException;
    // Add other visit methods as AST nodes are refactored/discovered
} 