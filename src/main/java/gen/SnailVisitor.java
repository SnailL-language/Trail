package gen;
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
	 * Visit a parse tree produced by the {@code FuncDeclStatement}
	 * labeled alternative in {@link SnailParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncDeclStatement(SnailParser.FuncDeclStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code VarDeclStatement}
	 * labeled alternative in {@link SnailParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarDeclStatement(SnailParser.VarDeclStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ExprStmt}
	 * labeled alternative in {@link SnailParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprStmt(SnailParser.ExprStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ForLoopStmt}
	 * labeled alternative in {@link SnailParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForLoopStmt(SnailParser.ForLoopStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code WhileLoopStmt}
	 * labeled alternative in {@link SnailParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileLoopStmt(SnailParser.WhileLoopStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IfConditionStmt}
	 * labeled alternative in {@link SnailParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfConditionStmt(SnailParser.IfConditionStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BreakStmt}
	 * labeled alternative in {@link SnailParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBreakStmt(SnailParser.BreakStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ReturnStmt}
	 * labeled alternative in {@link SnailParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnStmt(SnailParser.ReturnStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnailParser#expressionStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionStatement(SnailParser.ExpressionStatementContext ctx);
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
	 * Visit a parse tree produced by the {@code LogicalOrExpr}
	 * labeled alternative in {@link SnailParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalOrExpr(SnailParser.LogicalOrExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code MultiplicativeExpr}
	 * labeled alternative in {@link SnailParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiplicativeExpr(SnailParser.MultiplicativeExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code EqualityExpr}
	 * labeled alternative in {@link SnailParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEqualityExpr(SnailParser.EqualityExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code AdditiveExpr}
	 * labeled alternative in {@link SnailParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAdditiveExpr(SnailParser.AdditiveExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code PrimaryExpr}
	 * labeled alternative in {@link SnailParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimaryExpr(SnailParser.PrimaryExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code AssignmentExpr}
	 * labeled alternative in {@link SnailParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignmentExpr(SnailParser.AssignmentExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NotExpr}
	 * labeled alternative in {@link SnailParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNotExpr(SnailParser.NotExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code RelationalExpr}
	 * labeled alternative in {@link SnailParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelationalExpr(SnailParser.RelationalExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code LogicalAndExpr}
	 * labeled alternative in {@link SnailParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalAndExpr(SnailParser.LogicalAndExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NegateExpr}
	 * labeled alternative in {@link SnailParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNegateExpr(SnailParser.NegateExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnailParser#assignmentOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignmentOperator(SnailParser.AssignmentOperatorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code LiteralPrimaryExpr}
	 * labeled alternative in {@link SnailParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteralPrimaryExpr(SnailParser.LiteralPrimaryExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IdentifierPrimaryExpr}
	 * labeled alternative in {@link SnailParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdentifierPrimaryExpr(SnailParser.IdentifierPrimaryExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FunctionCallPrimaryExpr}
	 * labeled alternative in {@link SnailParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionCallPrimaryExpr(SnailParser.FunctionCallPrimaryExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ArrayLiteralPrimaryExpr}
	 * labeled alternative in {@link SnailParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayLiteralPrimaryExpr(SnailParser.ArrayLiteralPrimaryExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ParenthesizedPrimaryExpr}
	 * labeled alternative in {@link SnailParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenthesizedPrimaryExpr(SnailParser.ParenthesizedPrimaryExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnailParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteral(SnailParser.LiteralContext ctx);
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
}