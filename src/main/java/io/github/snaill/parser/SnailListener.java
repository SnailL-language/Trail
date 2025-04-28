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
	 * Enter a parse tree produced by the {@code VarDeclStatement}
	 * labeled alternative in {@link SnailParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterVarDeclStatement(SnailParser.VarDeclStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code VarDeclStatement}
	 * labeled alternative in {@link SnailParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitVarDeclStatement(SnailParser.VarDeclStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExprStmt}
	 * labeled alternative in {@link SnailParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterExprStmt(SnailParser.ExprStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExprStmt}
	 * labeled alternative in {@link SnailParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitExprStmt(SnailParser.ExprStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ForLoopStmt}
	 * labeled alternative in {@link SnailParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterForLoopStmt(SnailParser.ForLoopStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ForLoopStmt}
	 * labeled alternative in {@link SnailParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitForLoopStmt(SnailParser.ForLoopStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code WhileLoopStmt}
	 * labeled alternative in {@link SnailParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterWhileLoopStmt(SnailParser.WhileLoopStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code WhileLoopStmt}
	 * labeled alternative in {@link SnailParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitWhileLoopStmt(SnailParser.WhileLoopStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code IfConditionStmt}
	 * labeled alternative in {@link SnailParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterIfConditionStmt(SnailParser.IfConditionStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code IfConditionStmt}
	 * labeled alternative in {@link SnailParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitIfConditionStmt(SnailParser.IfConditionStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BreakStmt}
	 * labeled alternative in {@link SnailParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterBreakStmt(SnailParser.BreakStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BreakStmt}
	 * labeled alternative in {@link SnailParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitBreakStmt(SnailParser.BreakStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ReturnStmt}
	 * labeled alternative in {@link SnailParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterReturnStmt(SnailParser.ReturnStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ReturnStmt}
	 * labeled alternative in {@link SnailParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitReturnStmt(SnailParser.ReturnStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnailParser#expressionStatement}.
	 * @param ctx the parse tree
	 */
	void enterExpressionStatement(SnailParser.ExpressionStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnailParser#expressionStatement}.
	 * @param ctx the parse tree
	 */
	void exitExpressionStatement(SnailParser.ExpressionStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnailParser#globalVariableDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterGlobalVariableDeclaration(SnailParser.GlobalVariableDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnailParser#globalVariableDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitGlobalVariableDeclaration(SnailParser.GlobalVariableDeclarationContext ctx);
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
	 * Enter a parse tree produced by the {@code LogicalOrExpr}
	 * labeled alternative in {@link SnailParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterLogicalOrExpr(SnailParser.LogicalOrExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LogicalOrExpr}
	 * labeled alternative in {@link SnailParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitLogicalOrExpr(SnailParser.LogicalOrExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MultiplicativeExpr}
	 * labeled alternative in {@link SnailParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterMultiplicativeExpr(SnailParser.MultiplicativeExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MultiplicativeExpr}
	 * labeled alternative in {@link SnailParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitMultiplicativeExpr(SnailParser.MultiplicativeExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code EqualityExpr}
	 * labeled alternative in {@link SnailParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterEqualityExpr(SnailParser.EqualityExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code EqualityExpr}
	 * labeled alternative in {@link SnailParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitEqualityExpr(SnailParser.EqualityExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AdditiveExpr}
	 * labeled alternative in {@link SnailParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAdditiveExpr(SnailParser.AdditiveExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AdditiveExpr}
	 * labeled alternative in {@link SnailParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAdditiveExpr(SnailParser.AdditiveExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code PrimaryExpr}
	 * labeled alternative in {@link SnailParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterPrimaryExpr(SnailParser.PrimaryExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code PrimaryExpr}
	 * labeled alternative in {@link SnailParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitPrimaryExpr(SnailParser.PrimaryExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AssignmentExpr}
	 * labeled alternative in {@link SnailParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentExpr(SnailParser.AssignmentExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AssignmentExpr}
	 * labeled alternative in {@link SnailParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentExpr(SnailParser.AssignmentExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NotExpr}
	 * labeled alternative in {@link SnailParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterNotExpr(SnailParser.NotExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NotExpr}
	 * labeled alternative in {@link SnailParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitNotExpr(SnailParser.NotExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code RelationalExpr}
	 * labeled alternative in {@link SnailParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterRelationalExpr(SnailParser.RelationalExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code RelationalExpr}
	 * labeled alternative in {@link SnailParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitRelationalExpr(SnailParser.RelationalExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LogicalAndExpr}
	 * labeled alternative in {@link SnailParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterLogicalAndExpr(SnailParser.LogicalAndExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LogicalAndExpr}
	 * labeled alternative in {@link SnailParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitLogicalAndExpr(SnailParser.LogicalAndExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NegateExpr}
	 * labeled alternative in {@link SnailParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterNegateExpr(SnailParser.NegateExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NegateExpr}
	 * labeled alternative in {@link SnailParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitNegateExpr(SnailParser.NegateExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnailParser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentOperator(SnailParser.AssignmentOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnailParser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentOperator(SnailParser.AssignmentOperatorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LiteralPrimaryExpr}
	 * labeled alternative in {@link SnailParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterLiteralPrimaryExpr(SnailParser.LiteralPrimaryExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LiteralPrimaryExpr}
	 * labeled alternative in {@link SnailParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitLiteralPrimaryExpr(SnailParser.LiteralPrimaryExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code IdentifierPrimaryExpr}
	 * labeled alternative in {@link SnailParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterIdentifierPrimaryExpr(SnailParser.IdentifierPrimaryExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code IdentifierPrimaryExpr}
	 * labeled alternative in {@link SnailParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitIdentifierPrimaryExpr(SnailParser.IdentifierPrimaryExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FunctionCallPrimaryExpr}
	 * labeled alternative in {@link SnailParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterFunctionCallPrimaryExpr(SnailParser.FunctionCallPrimaryExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FunctionCallPrimaryExpr}
	 * labeled alternative in {@link SnailParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitFunctionCallPrimaryExpr(SnailParser.FunctionCallPrimaryExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ArrayLiteralPrimaryExpr}
	 * labeled alternative in {@link SnailParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterArrayLiteralPrimaryExpr(SnailParser.ArrayLiteralPrimaryExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ArrayLiteralPrimaryExpr}
	 * labeled alternative in {@link SnailParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitArrayLiteralPrimaryExpr(SnailParser.ArrayLiteralPrimaryExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ParenthesizedPrimaryExpr}
	 * labeled alternative in {@link SnailParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterParenthesizedPrimaryExpr(SnailParser.ParenthesizedPrimaryExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ParenthesizedPrimaryExpr}
	 * labeled alternative in {@link SnailParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitParenthesizedPrimaryExpr(SnailParser.ParenthesizedPrimaryExprContext ctx);
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
}