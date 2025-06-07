package io.github.snaill.ast;

/**
 * Интерфейс посетителя для AST Snail.
 * Определяет методы visit для каждого конкретного типа узла AST.
 * @param <T> Тип возвращаемого значения методов visit.
 */
public interface ASTVisitor<T> {
    // T visit(ProgramNode node) throws IOException; // Корневым узлом будет Scope
    T visit(Scope scopeNode);
    T visit(FunctionDeclaration funcDeclNode);
    T visit(VariableDeclaration varDeclNode);
    T visit(Parameter paramNode);
    T visit(IfStatement ifStmtNode);
    T visit(WhileLoop whileLoopNode);
    T visit(ForLoop forLoopNode);
    T visit(ReturnStatement retStmtNode);
    T visit(BreakStatement breakStmtNode);
    T visit(AssignmentExpression assignmentExprNode); // Исправлена опечатка
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
    // Добавляйте другие методы visit по мере рефакторинга/обнаружения узлов AST
} 