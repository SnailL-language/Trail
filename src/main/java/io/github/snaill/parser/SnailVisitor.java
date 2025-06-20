// Generated from Snail.g4 by ANTLR 4.13.2

package io.github.snaill.parser;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link SnailParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface SnailVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link SnailParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(SnailParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnailParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(SnailParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnailParser#funcDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncDeclaration(SnailParser.FuncDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnailParser#paramList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParamList(SnailParser.ParamListContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnailParser#param}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam(SnailParser.ParamContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnailParser#argumentList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArgumentList(SnailParser.ArgumentListContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnailParser#scope}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScope(SnailParser.ScopeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnailParser#variableDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableDeclaration(SnailParser.VariableDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnailParser#forLoop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForLoop(SnailParser.ForLoopContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnailParser#whileLoop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileLoop(SnailParser.WhileLoopContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnailParser#ifCondition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfCondition(SnailParser.IfConditionContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnailParser#breakStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBreakStatement(SnailParser.BreakStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnailParser#returnStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnStatement(SnailParser.ReturnStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnailParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(SnailParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnailParser#assignmentExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignmentExpression(SnailParser.AssignmentExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnailParser#logicalOrExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalOrExpression(SnailParser.LogicalOrExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnailParser#logicalAndExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalAndExpression(SnailParser.LogicalAndExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnailParser#equalityExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEqualityExpression(SnailParser.EqualityExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnailParser#relationalExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelationalExpression(SnailParser.RelationalExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnailParser#additiveExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAdditiveExpression(SnailParser.AdditiveExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnailParser#multiplicativeExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiplicativeExpression(SnailParser.MultiplicativeExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnailParser#unaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryExpression(SnailParser.UnaryExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnailParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimaryExpression(SnailParser.PrimaryExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnailParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteral(SnailParser.LiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnailParser#stringLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringLiteral(SnailParser.StringLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnailParser#numberLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumberLiteral(SnailParser.NumberLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnailParser#booleanLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleanLiteral(SnailParser.BooleanLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnailParser#identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdentifier(SnailParser.IdentifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnailParser#variableIdentifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableIdentifier(SnailParser.VariableIdentifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnailParser#arrayElement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayElement(SnailParser.ArrayElementContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnailParser#functionCall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionCall(SnailParser.FunctionCallContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnailParser#arrayLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayLiteral(SnailParser.ArrayLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnailParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(SnailParser.TypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnailParser#arrayType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayType(SnailParser.ArrayTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnailParser#primitiveType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimitiveType(SnailParser.PrimitiveTypeContext ctx);
}