// Generated from Snail.g4 by ANTLR 4.13.2

package io.github.snaill.parser;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SnailParser}.
 */
public interface SnailListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link SnailParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(SnailParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnailParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(SnailParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnailParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(SnailParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnailParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(SnailParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnailParser#funcDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterFuncDeclaration(SnailParser.FuncDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnailParser#funcDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitFuncDeclaration(SnailParser.FuncDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnailParser#paramList}.
	 * @param ctx the parse tree
	 */
	void enterParamList(SnailParser.ParamListContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnailParser#paramList}.
	 * @param ctx the parse tree
	 */
	void exitParamList(SnailParser.ParamListContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnailParser#param}.
	 * @param ctx the parse tree
	 */
	void enterParam(SnailParser.ParamContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnailParser#param}.
	 * @param ctx the parse tree
	 */
	void exitParam(SnailParser.ParamContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnailParser#argumentList}.
	 * @param ctx the parse tree
	 */
	void enterArgumentList(SnailParser.ArgumentListContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnailParser#argumentList}.
	 * @param ctx the parse tree
	 */
	void exitArgumentList(SnailParser.ArgumentListContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnailParser#scope}.
	 * @param ctx the parse tree
	 */
	void enterScope(SnailParser.ScopeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnailParser#scope}.
	 * @param ctx the parse tree
	 */
	void exitScope(SnailParser.ScopeContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnailParser#variableDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclaration(SnailParser.VariableDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnailParser#variableDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclaration(SnailParser.VariableDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnailParser#forLoop}.
	 * @param ctx the parse tree
	 */
	void enterForLoop(SnailParser.ForLoopContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnailParser#forLoop}.
	 * @param ctx the parse tree
	 */
	void exitForLoop(SnailParser.ForLoopContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnailParser#whileLoop}.
	 * @param ctx the parse tree
	 */
	void enterWhileLoop(SnailParser.WhileLoopContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnailParser#whileLoop}.
	 * @param ctx the parse tree
	 */
	void exitWhileLoop(SnailParser.WhileLoopContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnailParser#ifCondition}.
	 * @param ctx the parse tree
	 */
	void enterIfCondition(SnailParser.IfConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnailParser#ifCondition}.
	 * @param ctx the parse tree
	 */
	void exitIfCondition(SnailParser.IfConditionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnailParser#breakStatement}.
	 * @param ctx the parse tree
	 */
	void enterBreakStatement(SnailParser.BreakStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnailParser#breakStatement}.
	 * @param ctx the parse tree
	 */
	void exitBreakStatement(SnailParser.BreakStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnailParser#returnStatement}.
	 * @param ctx the parse tree
	 */
	void enterReturnStatement(SnailParser.ReturnStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnailParser#returnStatement}.
	 * @param ctx the parse tree
	 */
	void exitReturnStatement(SnailParser.ReturnStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnailParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(SnailParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnailParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(SnailParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnailParser#assignmentExpression}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentExpression(SnailParser.AssignmentExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnailParser#assignmentExpression}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentExpression(SnailParser.AssignmentExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnailParser#logicalOrExpression}.
	 * @param ctx the parse tree
	 */
	void enterLogicalOrExpression(SnailParser.LogicalOrExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnailParser#logicalOrExpression}.
	 * @param ctx the parse tree
	 */
	void exitLogicalOrExpression(SnailParser.LogicalOrExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnailParser#logicalAndExpression}.
	 * @param ctx the parse tree
	 */
	void enterLogicalAndExpression(SnailParser.LogicalAndExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnailParser#logicalAndExpression}.
	 * @param ctx the parse tree
	 */
	void exitLogicalAndExpression(SnailParser.LogicalAndExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnailParser#equalityExpression}.
	 * @param ctx the parse tree
	 */
	void enterEqualityExpression(SnailParser.EqualityExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnailParser#equalityExpression}.
	 * @param ctx the parse tree
	 */
	void exitEqualityExpression(SnailParser.EqualityExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnailParser#relationalExpression}.
	 * @param ctx the parse tree
	 */
	void enterRelationalExpression(SnailParser.RelationalExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnailParser#relationalExpression}.
	 * @param ctx the parse tree
	 */
	void exitRelationalExpression(SnailParser.RelationalExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnailParser#additiveExpression}.
	 * @param ctx the parse tree
	 */
	void enterAdditiveExpression(SnailParser.AdditiveExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnailParser#additiveExpression}.
	 * @param ctx the parse tree
	 */
	void exitAdditiveExpression(SnailParser.AdditiveExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnailParser#multiplicativeExpression}.
	 * @param ctx the parse tree
	 */
	void enterMultiplicativeExpression(SnailParser.MultiplicativeExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnailParser#multiplicativeExpression}.
	 * @param ctx the parse tree
	 */
	void exitMultiplicativeExpression(SnailParser.MultiplicativeExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnailParser#unaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterUnaryExpression(SnailParser.UnaryExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnailParser#unaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitUnaryExpression(SnailParser.UnaryExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnailParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterPrimaryExpression(SnailParser.PrimaryExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnailParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitPrimaryExpression(SnailParser.PrimaryExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnailParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteral(SnailParser.LiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnailParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteral(SnailParser.LiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnailParser#stringLiteral}.
	 * @param ctx the parse tree
	 */
	void enterStringLiteral(SnailParser.StringLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnailParser#stringLiteral}.
	 * @param ctx the parse tree
	 */
	void exitStringLiteral(SnailParser.StringLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnailParser#numberLiteral}.
	 * @param ctx the parse tree
	 */
	void enterNumberLiteral(SnailParser.NumberLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnailParser#numberLiteral}.
	 * @param ctx the parse tree
	 */
	void exitNumberLiteral(SnailParser.NumberLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnailParser#booleanLiteral}.
	 * @param ctx the parse tree
	 */
	void enterBooleanLiteral(SnailParser.BooleanLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnailParser#booleanLiteral}.
	 * @param ctx the parse tree
	 */
	void exitBooleanLiteral(SnailParser.BooleanLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnailParser#identifier}.
	 * @param ctx the parse tree
	 */
	void enterIdentifier(SnailParser.IdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnailParser#identifier}.
	 * @param ctx the parse tree
	 */
	void exitIdentifier(SnailParser.IdentifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnailParser#variableIdentifier}.
	 * @param ctx the parse tree
	 */
	void enterVariableIdentifier(SnailParser.VariableIdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnailParser#variableIdentifier}.
	 * @param ctx the parse tree
	 */
	void exitVariableIdentifier(SnailParser.VariableIdentifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnailParser#arrayElement}.
	 * @param ctx the parse tree
	 */
	void enterArrayElement(SnailParser.ArrayElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnailParser#arrayElement}.
	 * @param ctx the parse tree
	 */
	void exitArrayElement(SnailParser.ArrayElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnailParser#functionCall}.
	 * @param ctx the parse tree
	 */
	void enterFunctionCall(SnailParser.FunctionCallContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnailParser#functionCall}.
	 * @param ctx the parse tree
	 */
	void exitFunctionCall(SnailParser.FunctionCallContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnailParser#arrayLiteral}.
	 * @param ctx the parse tree
	 */
	void enterArrayLiteral(SnailParser.ArrayLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnailParser#arrayLiteral}.
	 * @param ctx the parse tree
	 */
	void exitArrayLiteral(SnailParser.ArrayLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnailParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(SnailParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnailParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(SnailParser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnailParser#arrayType}.
	 * @param ctx the parse tree
	 */
	void enterArrayType(SnailParser.ArrayTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnailParser#arrayType}.
	 * @param ctx the parse tree
	 */
	void exitArrayType(SnailParser.ArrayTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnailParser#primitiveType}.
	 * @param ctx the parse tree
	 */
	void enterPrimitiveType(SnailParser.PrimitiveTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnailParser#primitiveType}.
	 * @param ctx the parse tree
	 */
	void exitPrimitiveType(SnailParser.PrimitiveTypeContext ctx);
}