package io.github.snaill.ast;

import java.io.IOException;

/**
 * Visitor interface for the Snail AST.
 * Defines visit methods for each concrete AST node type.
 * @param <T> The return type of the visit methods.
 */
public interface ASTVisitor<T> {
    // T visit(ProgramNode node) throws IOException; // Root node will be a Scope
    T visit(Scope scopeNode);
    T visit(FunctionDeclaration funcDeclNode);
    T visit(VariableDeclaration varDeclNode);
    T visit(Parameter paramNode);
    T visit(IfStatement ifStmtNode);
    T visit(WhileLoop whileLoopNode);
    T visit(ForLoop forLoopNode);
    T visit(ReturnStatement retStmtNode);
    T visit(BreakStatement breakStmtNode);
    T visit(AssignmentExpression assignmentExprNode); // Corrected typo
    T visit(BinaryExpression binExprNode);
    T visit(UnaryExpression unExprNode);
    T visit(FunctionCall funcCallNode);
    T visit(Identifier idNode);
    T visit(NumberLiteral numLitNode);
    T visit(StringLiteral strLitNode);
    T visit(BooleanLiteral boolLitNode);
    T visit(ArrayLiteral arrayLitNode);
    T visit(ArrayElement arrayElNode);
    T visit(VariableReference variableReferenceNode);
    T visit(ArrayAssignment arrayAssignmentNode);
    T visit(ArrayAccess arrayAccessNode);
    T visit(PrimitiveType primitiveTypeNode);
    T visit(ArrayType arrayTypeNode);
    // Add other visit methods as AST nodes are refactored/discovered
} 