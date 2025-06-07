package io.github.snaill.ast;

// Type is in the same package (io.github.snaill.ast), no import needed.
// SourceInfo.java was not found, so its import is removed.
import io.github.snaill.parser.SnailParser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import io.github.snaill.result.ErrorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Создает абстрактное синтаксическое дерево (AST) из разобранной программы Snail.
 */
public class ASTReflectionBuilder implements ASTBuilder {

    private static final Logger logger = LoggerFactory.getLogger(ASTReflectionBuilder.class);

    @Override
    public Node build(SnailParser.ProgramContext ctx) throws io.github.snaill.exception.FailedCheckException {
        List<Statement> statements = new ArrayList<>();
        Scope rootScope = new Scope(statements, null);

        int i = 0;
        // Обрабатываем глобальные переменные (перед первой функцией)
        label:
        while (i < ctx.getChildCount()) {
            ParseTree child = ctx.getChild(i);
            switch (child) {
                case SnailParser.VariableDeclarationContext varCtx:
                    VariableDeclaration globalVar = parseVariableDeclaration(varCtx, rootScope);
                    if (globalVar != null) {
                        rootScope.addDeclaration(globalVar);
                        statements.add(globalVar);
                    }
                    i++;
                    break;
                case SnailParser.FuncDeclarationContext funcDeclarationContext:  // Если началась функция, прекращаем искать переменные
                    break label;
                case TerminalNode terminalNode when terminalNode.getSymbol().getType() == SnailParser.EOF:
                    break label; // Достигли конца файла раньше, чем нашли функции
                default:
                    // Неожиданный элемент на верхнем уровне перед функциями
                    String errorSource = child.getText();
                    if (child instanceof org.antlr.v4.runtime.ParserRuleContext prc && prc.getStart() != null && prc.getStart().getInputStream() != null) {
                        errorSource = SourceBuilder.toSourceLine(prc.getStart().getInputStream().toString(), prc.getStart().getLine(), prc.getStart().getCharPositionInLine(), prc.getText().length());
                    }
                    throw new io.github.snaill.exception.FailedCheckException(
                            new io.github.snaill.result.CompilationError(
                                    ErrorType.SYNTAX_ERROR,
                                    errorSource,
                                    "Unexpected token at global scope. Expected variable or function declaration.",
                                    "Ensure all global variable declarations are before any function declarations."
                            ).toString()
                    );
            }
        }

        // Проверяем, есть ли вообще функции
        boolean hasFunctions = false;
        for (int j = i; j < ctx.getChildCount(); j++) {
            if (ctx.getChild(j) instanceof SnailParser.FuncDeclarationContext) {
                hasFunctions = true;
                break;
            }
            if (ctx.getChild(j) instanceof TerminalNode && ((TerminalNode) ctx.getChild(j)).getSymbol().getType() == SnailParser.EOF) {
                break; // Дошли до EOF, функций нет
            }
        }

        if (!hasFunctions) {
            String errorSource = "program";
            if (ctx.getStart() != null && ctx.getStart().getInputStream() != null) {
                errorSource = SourceBuilder.toSourceLine(ctx.getStart().getInputStream().toString(), ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), ctx.getText().length());
            }
             throw new io.github.snaill.exception.FailedCheckException(
                new io.github.snaill.result.CompilationError(
                    ErrorType.SYNTAX_ERROR,
                    errorSource,
                    "No function declarations found. A Snail program must contain at least one function.",
                    "Define at least one function, e.g., 'fn main() -> void {}'."
                ).toString()
            );
        }

        // Затем обрабатываем функции
        for (; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof SnailParser.FuncDeclarationContext funcCtx) {
                Statement func = (Statement) parseFuncDeclaration(funcCtx, rootScope);
                if (func != null) statements.add(func);
            } else if (child instanceof TerminalNode && ((TerminalNode) child).getSymbol().getType() == SnailParser.EOF) {
                break; // Достигли конца файла
            } else {
                // Если после функций идет что-то, кроме EOF
                String errorSource = child.getText();
                 if (child instanceof org.antlr.v4.runtime.ParserRuleContext prc && prc.getStart() != null && prc.getStart().getInputStream() != null) {
                     errorSource = SourceBuilder.toSourceLine(prc.getStart().getInputStream().toString(), prc.getStart().getLine(), prc.getStart().getCharPositionInLine(), prc.getText().length());
                 }
                throw new io.github.snaill.exception.FailedCheckException(
                    new io.github.snaill.result.CompilationError(
                        ErrorType.SYNTAX_ERROR,
                        errorSource,
                        "Unexpected token after function declarations. Expected EOF.",
                        "Ensure all global variable declarations are before function declarations, and no statements follow the last function declaration."
                    ).toString()
                );
            }
        }
        
        // Проверка, что последний обработанный или следующий элемент - это EOF
        if (i >= ctx.getChildCount() || !(ctx.getChild(i) instanceof TerminalNode && ((TerminalNode) ctx.getChild(i)).getSymbol().getType() == SnailParser.EOF)) {
            ParseTree problematicChild = (i < ctx.getChildCount()) ? ctx.getChild(i) : (ctx.getChildCount() > 0 ? ctx.getChild(ctx.getChildCount() -1) : ctx) ;
            String errorSource = problematicChild.getText();
            if (problematicChild instanceof org.antlr.v4.runtime.ParserRuleContext prc && prc.getStart() != null && prc.getStart().getInputStream() != null) {
                 errorSource = SourceBuilder.toSourceLine(prc.getStart().getInputStream().toString(), prc.getStart().getLine(), prc.getStart().getCharPositionInLine(), prc.getText().length());
            } else if (ctx.getStart() != null && ctx.getStart().getInputStream() != null) {
                 errorSource = SourceBuilder.toSourceLine(ctx.getStart().getInputStream().toString(), ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), ctx.getText().length());
            }
            throw new io.github.snaill.exception.FailedCheckException(
                new io.github.snaill.result.CompilationError(
                    ErrorType.SYNTAX_ERROR,
                    errorSource,
                    "Program must end with EOF after all declarations.",
                    "Remove any trailing tokens after the last function declaration."
                ).toString()
            );
        }

        rootScope.setChildren(new ArrayList<>(statements));
        return rootScope;
    }

    private Node parseStatement(SnailParser.StatementContext ctx, Scope parent) throws io.github.snaill.exception.FailedCheckException {
        if (ctx.variableDeclaration() != null) {
            return parseVariableDeclaration(ctx.variableDeclaration(), parent);
        }
        if (ctx.forLoop() != null) {
            return parseForLoop(ctx.forLoop(), parent);
        }
        if (ctx.funcDeclaration() != null) {
            // Разрешаем вложенные объявления функций
            return parseFuncDeclaration(ctx.funcDeclaration(), parent);
        }
        if (ctx.whileLoop() != null) {
            return parseWhileLoop(ctx.whileLoop(), parent);
        }
        if (ctx.ifCondition() != null) {
            return parseIfCondition(ctx.ifCondition(), parent);
        }
        if (ctx.breakStatement() != null) {
            return parseBreakStatement(ctx.breakStatement());
        }
        if (ctx.returnStatement() != null) {
            return parseReturnStatement(ctx.returnStatement(), parent);
        }
        if (ctx.expression() != null) {
            Expression expr = (Expression) parseExpression(ctx.expression(), parent);
            if (expr == null) {
                String before = ctx.getStart() != null ?
                    io.github.snaill.ast.SourceBuilder.toSourceLine(ctx.getStart().getInputStream().toString(), ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), ctx.getText().length()) :
                    io.github.snaill.ast.SourceBuilder.toSourceCode(ctx);
                throw new io.github.snaill.exception.FailedCheckException(
                    new io.github.snaill.result.CompilationError(
                        io.github.snaill.result.ErrorType.UNKNOWN_TYPE,
                        before,
                        "Empty expression",
                        ""
                    ).toString()
                );
            }
            ExpressionStatement stmt = new ExpressionStatement(expr);
            if (ctx.getStart() != null) {
                stmt.setSourceInfo(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), ctx.getStart().getInputStream().toString());
            }
            return stmt;
        }
        // Если оператор не распознан — выбрасываем ошибку
        String before = ctx.getStart() != null ?
            io.github.snaill.ast.SourceBuilder.toSourceLine(ctx.getStart().getInputStream().toString(), ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), ctx.getText().length()) :
            io.github.snaill.ast.SourceBuilder.toSourceCode(ctx);
        throw new io.github.snaill.exception.FailedCheckException(
            new io.github.snaill.result.CompilationError(
                io.github.snaill.result.ErrorType.UNKNOWN_TYPE,
                before,
                "Unknown or empty statement",
                ""
            ).toString()
        );
    }

    private VariableDeclaration parseVariableDeclaration(SnailParser.VariableDeclarationContext ctx, Scope parentScopeContext) throws io.github.snaill.exception.FailedCheckException {
        String name = ctx.IDENTIFIER().getText();
        Type type = (Type) parseType(ctx.type(), parentScopeContext);
        // Передаем parentScopeContext для разрешения выражений в контексте собственной области видимости переменной
        Expression expr = (Expression) parseExpression(ctx.expression(), parentScopeContext);
        VariableDeclaration varDecl = new VariableDeclaration(name, type, expr);
        // Установка окружающей области видимости и добавление в localDeclarations будет обработано методом addDeclaration вызывающей области видимости.
        // Информация об источнике устанавливается здесь, так как она напрямую связана с контекстом парсинга самого объявления.
        if (ctx.IDENTIFIER() != null && ctx.IDENTIFIER().getSymbol() != null && ctx.start != null && ctx.start.getInputStream() != null) {
            varDecl.setSourceInfo(ctx.IDENTIFIER().getSymbol().getLine(), ctx.IDENTIFIER().getSymbol().getCharPositionInLine(), ctx.start.getInputStream().toString());
        } else if (ctx.getStart() != null && ctx.start.getInputStream() != null) { // Запасной вариант, если символ IDENTIFIER по какой-то причине равен null
            varDecl.setSourceInfo(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), ctx.start.getInputStream().toString());
        }
        return varDecl;
    }

    private Node parseFuncDeclaration(SnailParser.FuncDeclarationContext ctx, Scope parent) throws io.github.snaill.exception.FailedCheckException {
        if (ctx.IDENTIFIER() == null) {
            String errorSource = "function declaration";
            if (ctx.getStart() != null && ctx.getStart().getInputStream() != null) {
                errorSource = SourceBuilder.toSourceLine(ctx.getStart().getInputStream().toString(), ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), ctx.getText().length());
            }
            throw new io.github.snaill.exception.FailedCheckException(
                new io.github.snaill.result.CompilationError(
                    ErrorType.SYNTAX_ERROR,
                    errorSource,
                    "Function declaration is missing an identifier.",
                    "Provide a name for the function, e.g., 'fn myFunction() ...'."
                ).toString()
            );
        }
        String name = ctx.IDENTIFIER().getText();
        // Parameters are parsed first, they need the 'parent' scope for their own type resolution if complex types were allowed for params.
        List<Parameter> params = ctx.paramList() != null ?
                parseParamList(ctx.paramList(), parent) : List.of();

        Type returnType;
        if (ctx.type() != null) {
            returnType = (Type) parseType(ctx.type(), parent); // Return type is resolved in the parent scope of the function.
        } else {
            returnType = new PrimitiveType("void");
            if (returnType instanceof AbstractNode rn) {
                 rn.setEnclosingScope(parent); 
                 if (ctx.IDENTIFIER() != null && ctx.IDENTIFIER().getSymbol() != null) {
                    rn.setSourceInfo(ctx.IDENTIFIER().getSymbol().getLine(), ctx.IDENTIFIER().getSymbol().getCharPositionInLine(), parent.getSource());
                 } else if (ctx.getStart() != null) {
                    rn.setSourceInfo(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), parent.getSource());
                 }
            }
        }

        // Create the function's body scope. This scope's parent is the scope where the function is declared.
        Scope funcBodyScope = new Scope(new ArrayList<>(), parent, null); // Enclosing function for this scope will be set by funcDecl.
        boolean explicitReturn = ctx.type() != null;
        
        FunctionDeclaration funcDecl = new FunctionDeclaration(name, params, returnType, funcBodyScope, explicitReturn);
        funcBodyScope.setEnclosingFunctionContext(funcDecl); // Link the scope to its function declaration.
        funcDecl.setEnclosingScope(parent); // The function declaration itself resides in the parent scope.

        // Add parameters as VariableDeclarations to the function's body scope
        if (params != null) {
            for (Parameter param : params) {
                VariableDeclaration paramVarDecl = new VariableDeclaration(param.getName(), param.getType(), null /* no initial value for param */);
                if (param instanceof AbstractNode paramAbstractNode) { // Use pattern variable
                    paramVarDecl.setSourceInfo(paramAbstractNode.getLine(), paramAbstractNode.getCharPosition(), paramAbstractNode.getSource());
                }
                paramVarDecl.setEnclosingScope(funcBodyScope); // Parameter's scope is the function body
                try {
                    funcBodyScope.addDeclaration(paramVarDecl);
                    logger.debug("Added parameter '{}' as VariableDeclaration to scope of function '{}'", param.getName(), name);
                } catch (io.github.snaill.exception.FailedCheckException e) {
                    logger.error("Failed to add parameter '{}' as VariableDeclaration to scope of function '{}'", param.getName(), name, e);
                    // Convert to a more specific error or add to a list of errors to be reported later.
                    // For now, rethrow as a RuntimeException to make it visible during development.
                    throw new RuntimeException("Failed to add parameter '" + param.getName() + "' to function scope: " + e.getMessage(), e);
                }
            }
        }

        // Now parse the function body statements. These statements will be added to funcBodyScope.
        // Pass funcDecl so statements within the body know their enclosing function (e.g., for 'return' statements).
        Scope parsedBodyStatementsContainer = parseScope(ctx.scope(), funcBodyScope, funcDecl); 
        funcBodyScope.setChildren(parsedBodyStatementsContainer.getChildren()); // Populate funcBodyScope with actual statements.

        // Set source information for the FunctionDeclaration node itself
        if (ctx.IDENTIFIER() != null && ctx.IDENTIFIER().getSymbol() != null && parent != null && parent.getSource() != null) {
            funcDecl.setSourceInfo(ctx.IDENTIFIER().getSymbol().getLine(), ctx.IDENTIFIER().getSymbol().getCharPositionInLine(), parent.getSource());
        } else if (ctx.getStart() != null && parent != null && parent.getSource() != null) {
            funcDecl.setSourceInfo(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), parent.getSource());
        }

        logger.debug("Parsed function '{}' with {} parameters. Body scope ID: {}. Parent scope ID: {}", 
            name, 
            (params != null ? params.size() : 0), 
            System.identityHashCode(funcBodyScope),
            (parent != null ? System.identityHashCode(parent) : "null")
        );

        return funcDecl;
    }

    private List<Parameter> parseParamList(SnailParser.ParamListContext ctx, Scope parentScope) throws io.github.snaill.exception.FailedCheckException {
    List<Parameter> parameters = new ArrayList<>();
    if (ctx != null && ctx.param() != null) {
        for (SnailParser.ParamContext paramCtx : ctx.param()) {
            Node paramNode = parseParam(paramCtx, parentScope);
            if (paramNode instanceof Parameter) {
                parameters.add((Parameter) paramNode);
            } else {
                // This case should ideally not happen if parseParam is correct
                String errorSource = SourceBuilder.toSourceLine(paramCtx.getStart().getInputStream().toString(), paramCtx.getStart().getLine(), paramCtx.getStart().getCharPositionInLine(), paramCtx.getText().length());
                throw new io.github.snaill.exception.FailedCheckException(
                    new io.github.snaill.result.CompilationError(
                        ErrorType.INTERNAL_ERROR,
                        errorSource,
                        "Expected a Parameter node but got " + (paramNode != null ? paramNode.getClass().getSimpleName() : "null") + ".",
                        "Please check the grammar and AST builder logic for parameter parsing."
                    ).toString()
                );
            }
        }
    }
    return parameters;
}

private Node parseParam(SnailParser.ParamContext ctx, Scope containingScope) {
        String name = ctx.IDENTIFIER().getText();
        Type type;
        try {
            type = (Type) parseType(ctx.type(), containingScope);
        } catch (io.github.snaill.exception.FailedCheckException e) {
            throw new RuntimeException(e);
        }
        Parameter paramNode = new Parameter(name, type);
        // Сам узел Parameter (представляющий переменную 'name') находится в области видимости тела функции.
        // Однако его тип ('type') разрешается в 'containingScope' (область видимости объявления функции).
        // Окружающая область видимости для узла Parameter (как объявления) будет установлена при его добавлении в область видимости FunctionDeclaration.
        // Пока что убедимся, что узел Parameter имеет информацию об источнике.
        if (ctx.IDENTIFIER() != null && ctx.IDENTIFIER().getSymbol() != null) {
            paramNode.setSourceInfo(ctx.IDENTIFIER().getSymbol().getLine(), ctx.IDENTIFIER().getSymbol().getCharPositionInLine(), containingScope.getSource());
        } else if (ctx.getStart() != null) {
            paramNode.setSourceInfo(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), containingScope.getSource());
        }
        // Узел 'type' внутри Parameter уже имеет установленную окружающую область видимости благодаря parseType.
        return paramNode;
    }

    private Scope parseScope(SnailParser.ScopeContext ctx, Scope parent) throws io.github.snaill.exception.FailedCheckException {
        return parseScope(ctx, parent, null);
    }

    private Scope parseScope(SnailParser.ScopeContext ctx, Scope parent, FunctionDeclaration enclosingFunction) throws io.github.snaill.exception.FailedCheckException {
        if (ctx == null) {
            String errorSource = "Unknown location (ScopeContext was null)";
            String problemLocation = "a code block";
            if (enclosingFunction != null) {
                problemLocation = "the body of function '" + enclosingFunction.getName() + "'";
                if (enclosingFunction.getLine() != -1 && enclosingFunction.getSource() != null) {
                     errorSource = "function '" + enclosingFunction.getName() + "' declared at line " + enclosingFunction.getLine() + " in " + enclosingFunction.getSource();
                } else {
                    errorSource = "function '" + enclosingFunction.getName() + "'";
                }
            } else if (parent != null && parent.getSource() != null && parent.getLine() != -1) {
                 errorSource = "a scope starting near line " + parent.getLine() + " in " + parent.getSource();
            }

            throw new io.github.snaill.exception.FailedCheckException(
                new io.github.snaill.result.CompilationError(
                    io.github.snaill.result.ErrorType.SYNTAX_ERROR,
                    errorSource,
                    "Syntax error: Expected " + problemLocation + " (e.g., using '{...}') but it was missing or malformed.",
                    "Ensure the block is correctly defined with '{' and '}' or check for syntax errors preventing its recognition."
                ).toString()
            );
        }
        Scope currentScope = new Scope(new ArrayList<>(), parent, enclosingFunction);
        List<Node> children = new ArrayList<>();
        for (var stmtCtx : ctx.statement()) {
            Statement stmt;
            if (stmtCtx.variableDeclaration() != null) {
                VariableDeclaration varDecl = parseVariableDeclaration(stmtCtx.variableDeclaration(), currentScope);
                if (varDecl != null) {
                    currentScope.addDeclaration(varDecl); // Add to current scope's symbol table
                    stmt = varDecl;
                } else {
                    stmt = null; // Не должно произойти, если parseVariableDeclaration выбрасывает ошибку при ошибке
                }
            } else if (stmtCtx.funcDeclaration() != null) {
                String errorSourceFuncScope = stmtCtx.funcDeclaration().getStart() != null ?
                    io.github.snaill.ast.SourceBuilder.toSourceLine(stmtCtx.funcDeclaration().getStart().getInputStream().toString(), stmtCtx.funcDeclaration().getStart().getLine(), stmtCtx.funcDeclaration().getStart().getCharPositionInLine(), stmtCtx.funcDeclaration().getText().length()) :
                    io.github.snaill.ast.SourceBuilder.toSourceCode(stmtCtx.funcDeclaration());
                throw new io.github.snaill.exception.FailedCheckException(
                    new io.github.snaill.result.CompilationError(
                        ErrorType.SEMANTIC_ERROR,
                        errorSourceFuncScope,
                        "Function declarations are not allowed inside blocks.",
                        "Define functions only at the top level or directly within the global scope."
                    ).toString()
                );
            } else {
                stmt = (Statement) parseStatement(stmtCtx, currentScope);
            }
            if (stmt != null) {
                children.add(stmt);
                currentScope.setChildren(new ArrayList<>(children));
            }
        }
        return currentScope;
    }
        
    private Node parseForLoop(SnailParser.ForLoopContext ctx, Scope parent) throws io.github.snaill.exception.FailedCheckException {
        // Создаем новую область видимости для самого цикла for. Эта область будет содержать переменную цикла.
        Scope forScope = new Scope(new ArrayList<>(), parent, parent.getEnclosingFunction());

        // Разбираем инициализатор. Узел VariableDeclaration создается с его инициализатором, разрешенным в РОДИТЕЛЬСКОЙ области видимости.
        VariableDeclaration loopVarDecl = (VariableDeclaration) parseVariableDeclaration(ctx.variableDeclaration(), parent);
        if (loopVarDecl != null) {
             // Добавляем объявление переменной цикла в forScope, делая ее видимой внутри цикла.
             forScope.addDeclaration(loopVarDecl);
        }
        // Это оператор инициализации для узла ForLoop.

        // Разбираем условие, используя forScope (переменная цикла здесь видима).
        Expression condition = (Expression) parseExpression(ctx.expression(0), forScope);

        // Разбираем шаг, используя forScope (переменная цикла здесь видима).
        Expression step = (Expression) parseExpression(ctx.expression(1), forScope);

        // Разбираем область видимости тела. Ее родитель - forScope.
        Scope body = parseScope(ctx.scope(), forScope);
        
        ForLoop forLoopNode = new ForLoop(loopVarDecl, condition, step, body);
        if (ctx.getStart() != null && ctx.getStart().getInputStream() != null) {
            forLoopNode.setSourceInfo(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), ctx.getStart().getInputStream().toString());
        }
        return forLoopNode;
    }

    private Node parseWhileLoop(SnailParser.WhileLoopContext ctx, Scope parent) throws io.github.snaill.exception.FailedCheckException {
        // Условие разрешается в родительской области видимости
        Expression condition = (Expression) parseExpression(ctx.expression(), parent);
        if (condition == null) {
            String before = ctx.getStart() != null ?
                io.github.snaill.ast.SourceBuilder.toSourceLine(ctx.getStart().getInputStream().toString(), ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), ctx.getText().length()) :
                io.github.snaill.ast.SourceBuilder.toSourceCode(ctx);
            throw new io.github.snaill.exception.FailedCheckException(
                new io.github.snaill.result.CompilationError(
                    io.github.snaill.result.ErrorType.UNKNOWN_TYPE,
                    before,
                    "Empty or invalid while condition",
                    ""
                ).toString()
            );
        }

        // Создаем новую область видимости для тела цикла while
        Scope whileBodyScopeContext = new Scope(new ArrayList<>(), parent, parent.getEnclosingFunction());
        Scope body = parseScope(ctx.scope(), whileBodyScopeContext); // Тело разбирается в этой новой области видимости
        
        WhileLoop whileLoop = new WhileLoop(condition, body);
        if (ctx.getStart() != null && ctx.getStart().getInputStream() != null) {
            whileLoop.setSourceInfo(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), ctx.getStart().getInputStream().toString());
        }
        return whileLoop;
    }

    private Node parseIfCondition(SnailParser.IfConditionContext ctx, Scope parent) throws io.github.snaill.exception.FailedCheckException {
        Expression condition = (Expression) parseExpression(ctx.expression(), parent);
        if (condition == null) {
            String before = ctx.getStart() != null ?
                io.github.snaill.ast.SourceBuilder.toSourceLine(ctx.getStart().getInputStream().toString(), ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), ctx.getText().length()) :
                io.github.snaill.ast.SourceBuilder.toSourceCode(ctx);
            throw new io.github.snaill.exception.FailedCheckException(
                new io.github.snaill.result.CompilationError(
                    io.github.snaill.result.ErrorType.UNKNOWN_TYPE,
                    before,
                    "Empty or invalid if condition",
                    ""
                ).toString()
            );
        }

        // Создаем новую область видимости для ветви 'then'
        Scope thenScopeContext = new Scope(new ArrayList<>(), parent, parent.getEnclosingFunction());
        Scope thenScope = ctx.scope(0) != null ? parseScope(ctx.scope(0), thenScopeContext) : new Scope(new ArrayList<>(), thenScopeContext, parent.getEnclosingFunction());
        if (thenScope == null && ctx.scope(0) != null) { 
             String before = ctx.getStart() != null ?
                io.github.snaill.ast.SourceBuilder.toSourceLine(ctx.getStart().getInputStream().toString(), ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), ctx.getText().length()) :
                io.github.snaill.ast.SourceBuilder.toSourceCode(ctx);
            throw new io.github.snaill.exception.FailedCheckException(
                new io.github.snaill.result.CompilationError(
                    io.github.snaill.result.ErrorType.UNKNOWN_TYPE,
                    before,
                    "Empty or invalid then block in if",
                    ""
                ).toString()
            );
        }

        Scope elseScope = null;
        if (ctx.scope().size() > 1 && ctx.scope(1) != null) {
            Scope elseScopeContext = new Scope(new ArrayList<>(), parent, parent.getEnclosingFunction());
            elseScope = parseScope(ctx.scope(1), elseScopeContext);
            if (elseScope == null) { 
                String before = ctx.getStart() != null ?
                    io.github.snaill.ast.SourceBuilder.toSourceLine(ctx.getStart().getInputStream().toString(), ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), ctx.getText().length()) :
                    io.github.snaill.ast.SourceBuilder.toSourceCode(ctx);
                throw new io.github.snaill.exception.FailedCheckException(
                    new io.github.snaill.result.CompilationError(
                        io.github.snaill.result.ErrorType.UNKNOWN_TYPE,
                        before,
                        "Empty or invalid else block in if",
                        ""
                    ).toString()
                );
            }
        }
        
        IfStatement ifStmt = new IfStatement(condition, thenScope, elseScope);
        if (ctx.getStart() != null && ctx.getStart().getInputStream() != null) {
            ifStmt.setSourceInfo(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), ctx.getStart().getInputStream().toString());
        }
        return ifStmt;
    }

    private Node parseBreakStatement(SnailParser.BreakStatementContext ctx) {
        return new BreakStatement();
    }

    private Node parseReturnStatement(SnailParser.ReturnStatementContext ctx, Scope parent) throws io.github.snaill.exception.FailedCheckException {
        Expression expr = ctx.expression() != null ? (Expression) parseExpression(ctx.expression(), parent) : null;
        ReturnStatement ret = new ReturnStatement(expr);
        if (ctx.getStart() != null) {
            ret.setSourceInfo(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), ctx.getStart().getInputStream().toString());
        }
        return ret;
    }

    private Node parseExpression(SnailParser.ExpressionContext ctx, Scope parent) throws io.github.snaill.exception.FailedCheckException {
        if (ctx == null) {
            return null;
        }
        // The new grammar structure: expression -> assignmentExpression
        return parseAssignmentExpression(ctx.assignmentExpression(), parent);
    }

    // Level 1: Assignment (right-associative)
    private Node parseAssignmentExpression(SnailParser.AssignmentExpressionContext ctx, Scope parent) throws io.github.snaill.exception.FailedCheckException {
        if (ctx == null) return null;

        // Check if this context represents an assignment (identifier op expression) or falls through to logicalOrExpression
        if (ctx.assigmentOperator != null) { // Grammar: identifier assigmentOperator=('='|...) assignmentExpression
            Expression left = (Expression) parseIdentifier(ctx.identifier(), parent);
            String op = ctx.assigmentOperator.getText(); // Corrected: access token directly
            // For right-associativity, the RHS is also an assignmentExpression
            Expression right = (Expression) parseAssignmentExpression(ctx.assignmentExpression(), parent); // Grammar ensures this is the RHS assignmentExpression
            
            AssignmentExpression assign = new AssignmentExpression(left, op, right);
            if (ctx.assigmentOperator != null && ctx.start != null && ctx.start.getInputStream() != null) {
                assign.setSourceInfo(ctx.assigmentOperator.getLine(), ctx.assigmentOperator.getCharPositionInLine(), ctx.start.getInputStream().toString());
            }
            if (parent != null) {
                assign.setEnclosingScope(parent);
            }
            return assign;
        } else { // Grammar: | logicalOrExpression
            return parseLogicalOrExpression(ctx.logicalOrExpression(), parent);
        }
    }

    // Level 2: Logical OR (left-associative)
    private Node parseLogicalOrExpression(SnailParser.LogicalOrExpressionContext ctx, Scope parent) throws io.github.snaill.exception.FailedCheckException {
        if (ctx == null) return null;
        Expression left = (Expression) parseLogicalAndExpression(ctx.logicalAndExpression(0), parent);
        for (int i = 1; i < ctx.logicalAndExpression().size(); i++) {
            String op = ctx.getChild(i * 2 - 1).getText(); // Operator is between expressions: logicalAndExpression OP logicalAndExpression
            Expression right = (Expression) parseLogicalAndExpression(ctx.logicalAndExpression(i), parent);
            BinaryExpression binExpr = new BinaryExpression(left, op, right);
            if (ctx.getChild(i * 2 - 1) instanceof TerminalNode opNode && ((TerminalNode)ctx.getChild(i*2-1)).getSymbol() != null) {
                binExpr.setSourceInfo(opNode.getSymbol().getLine(), opNode.getSymbol().getCharPositionInLine(), opNode.getSymbol().getInputStream().toString());
            }
            if (parent != null) {
                binExpr.setEnclosingScope(parent);
            }
            left = binExpr;
        }
        return left;
    }

    // Level 3: Logical AND (left-associative)
    private Node parseLogicalAndExpression(SnailParser.LogicalAndExpressionContext ctx, Scope parent) throws io.github.snaill.exception.FailedCheckException {
        if (ctx == null) return null;
        Expression left = (Expression) parseEqualityExpression(ctx.equalityExpression(0), parent);
        for (int i = 1; i < ctx.equalityExpression().size(); i++) {
            String op = ctx.getChild(i * 2 - 1).getText(); 
            Expression right = (Expression) parseEqualityExpression(ctx.equalityExpression(i), parent);
            BinaryExpression binExpr = new BinaryExpression(left, op, right);
            if (ctx.getChild(i * 2 - 1) instanceof TerminalNode opNode && ((TerminalNode)ctx.getChild(i*2-1)).getSymbol() != null) {
                binExpr.setSourceInfo(opNode.getSymbol().getLine(), opNode.getSymbol().getCharPositionInLine(), opNode.getSymbol().getInputStream().toString());
            }
            if (parent != null) {
                binExpr.setEnclosingScope(parent);
            }
            left = binExpr;
        }
        return left;
    }

    // Level 4: Equality (left-associative)
    private Node parseEqualityExpression(SnailParser.EqualityExpressionContext ctx, Scope parent) throws io.github.snaill.exception.FailedCheckException {
        if (ctx == null) return null;
        Expression left = (Expression) parseRelationalExpression(ctx.relationalExpression(0), parent);
        for (int i = 1; i < ctx.relationalExpression().size(); i++) {
            String op = ctx.getChild(i * 2 - 1).getText(); 
            Expression right = (Expression) parseRelationalExpression(ctx.relationalExpression(i), parent);
            BinaryExpression binExpr = new BinaryExpression(left, op, right);
            if (ctx.getChild(i * 2 - 1) instanceof TerminalNode opNode && ((TerminalNode)ctx.getChild(i*2-1)).getSymbol() != null) {
                binExpr.setSourceInfo(opNode.getSymbol().getLine(), opNode.getSymbol().getCharPositionInLine(), opNode.getSymbol().getInputStream().toString());
            }
            if (parent != null) {
                binExpr.setEnclosingScope(parent);
            }
            left = binExpr;
        }
        return left;
    }

    // Level 5: Relational (left-associative)
    private Node parseRelationalExpression(SnailParser.RelationalExpressionContext ctx, Scope parent) throws io.github.snaill.exception.FailedCheckException {
        if (ctx == null) return null;
        Expression left = (Expression) parseAdditiveExpression(ctx.additiveExpression(0), parent);
        for (int i = 1; i < ctx.additiveExpression().size(); i++) {
            String op = ctx.getChild(i * 2 - 1).getText(); 
            Expression right = (Expression) parseAdditiveExpression(ctx.additiveExpression(i), parent);
            BinaryExpression binExpr = new BinaryExpression(left, op, right);
            if (ctx.getChild(i * 2 - 1) instanceof TerminalNode opNode && ((TerminalNode)ctx.getChild(i*2-1)).getSymbol() != null) {
                binExpr.setSourceInfo(opNode.getSymbol().getLine(), opNode.getSymbol().getCharPositionInLine(), opNode.getSymbol().getInputStream().toString());
            }
            if (parent != null) {
                binExpr.setEnclosingScope(parent);
            }
            left = binExpr;
        }
        return left;
    }

    // Level 6: Additive (left-associative)
    private Node parseAdditiveExpression(SnailParser.AdditiveExpressionContext ctx, Scope parent) throws io.github.snaill.exception.FailedCheckException {
        if (ctx == null) return null;
        Expression left = (Expression) parseMultiplicativeExpression(ctx.multiplicativeExpression(0), parent);
        for (int i = 1; i < ctx.multiplicativeExpression().size(); i++) {
            String op = ctx.getChild(i * 2 - 1).getText(); 
            Expression right = (Expression) parseMultiplicativeExpression(ctx.multiplicativeExpression(i), parent);
            BinaryExpression binExpr = new BinaryExpression(left, op, right);
            if (ctx.getChild(i * 2 - 1) instanceof TerminalNode opNode && ((TerminalNode)ctx.getChild(i*2-1)).getSymbol() != null) {
                binExpr.setSourceInfo(opNode.getSymbol().getLine(), opNode.getSymbol().getCharPositionInLine(), opNode.getSymbol().getInputStream().toString());
            }
            if (parent != null) {
                binExpr.setEnclosingScope(parent);
            }
            left = binExpr;
        }
        return left;
    }

    // Level 7: Multiplicative (left-associative)
    private Node parseMultiplicativeExpression(SnailParser.MultiplicativeExpressionContext ctx, Scope parent) throws io.github.snaill.exception.FailedCheckException {
        if (ctx == null) return null;
        Expression left = (Expression) parseUnaryExpression(ctx.unaryExpression(0), parent);
        for (int i = 1; i < ctx.unaryExpression().size(); i++) {
            String op = ctx.getChild(i * 2 - 1).getText(); 
            Expression right = (Expression) parseUnaryExpression(ctx.unaryExpression(i), parent);
            BinaryExpression binExpr = new BinaryExpression(left, op, right);
            if (ctx.getChild(i * 2 - 1) instanceof TerminalNode opNode && ((TerminalNode)ctx.getChild(i*2-1)).getSymbol() != null) {
                binExpr.setSourceInfo(opNode.getSymbol().getLine(), opNode.getSymbol().getCharPositionInLine(), opNode.getSymbol().getInputStream().toString());
            }
            if (parent != null) {
                binExpr.setEnclosingScope(parent);
            }
            left = binExpr;
        }
        return left;
    }

    // Level 8: Unary (prefix, right-associative)
    private Node parseUnaryExpression(SnailParser.UnaryExpressionContext ctx, Scope parent) throws io.github.snaill.exception.FailedCheckException {
        if (ctx == null) return null;

        if (ctx.unaryOperator != null) { // Grammar: unaryOperator=('-'|'!') unaryExpression
            String operator = ctx.unaryOperator.getText();
            Expression argument = (Expression) parseUnaryExpression(ctx.unaryExpression(), parent); // Recursive call
            UnaryExpression unaryExpr = new UnaryExpression(operator, argument);
            if (parent != null) {
                unaryExpr.setEnclosingScope(parent);
            }
            if (ctx.unaryOperator != null && ctx.start != null && ctx.start.getInputStream() != null) {
                unaryExpr.setSourceInfo(ctx.unaryOperator.getLine(), ctx.unaryOperator.getCharPositionInLine(), ctx.start.getInputStream().toString());
            }
            return unaryExpr;
        } else { // Grammar: | primaryExpression
            return parsePrimaryExpression(ctx.primaryExpression(), parent);
        }
    }

    // Level 9: Primary expressions
    private Node parsePrimaryExpression(SnailParser.PrimaryExpressionContext ctx, Scope parent) throws io.github.snaill.exception.FailedCheckException {
        if (ctx == null) {
             throw new io.github.snaill.exception.FailedCheckException(
                new io.github.snaill.result.CompilationError(
                    io.github.snaill.result.ErrorType.UNKNOWN_TYPE,
                    "^", // Placeholder for source info
                    "Empty primary expression",
                    ""
                ).toString()
            );
        }
        if (ctx.literal() != null) {
            return parseLiteral(ctx.literal());
        }
        if (ctx.identifier() != null) {
            // parseIdentifier handles both simple identifiers and array accesses via SnailParser.IdentifierContext
            return parseIdentifier(ctx.identifier(), parent);
        }
        if (ctx.expression() != null) { // LPAREN expression RPAREN
            Node innerExpr = parseExpression(ctx.expression(), parent);
            if (innerExpr instanceof Expression) {
                return new ParenthesizedExpression((Expression) innerExpr);
            } else {
                // This case should ideally not happen if parseExpression always returns an Expression
                // or if the grammar ensures expression() results in an AST Expression node.
                // Handle error: perhaps throw an exception or return an error node.
                String location = "Line " + ctx.getStart().getLine() + ":" + ctx.getStart().getCharPositionInLine();
                throw new io.github.snaill.exception.FailedCheckException(
                    new io.github.snaill.result.CompilationError(
                        io.github.snaill.result.ErrorType.SYNTAX_ERROR,
                        location, // 'before' string
                        "Parenthesized expression did not yield an Expression node",
                        "" // 'after' string, can be empty or provide more context if needed
                    ).toString()
                );
            }
        }
        if (ctx.functionCall() != null) {
            return parseFunctionCall(ctx.functionCall(), parent);
        }
        if (ctx.arrayLiteral() != null) { // ADDED THIS BLOCK
            return parseArrayLiteral(ctx.arrayLiteral(), parent);
        }
        // If none of the above, it's an unhandled case or a grammar/parser issue.
        String locationUnhandled = "Line " + ctx.getStart().getLine() + ":" + ctx.getStart().getCharPositionInLine();
        throw new io.github.snaill.exception.FailedCheckException(
            new io.github.snaill.result.CompilationError(
                io.github.snaill.result.ErrorType.SYNTAX_ERROR,
                locationUnhandled, // 'before' string
                "Unknown or unhandled primary expression type: " + ctx.getText(),
                "" // 'after' string
            ).toString()
        );
    }



    private Node parseLiteral(SnailParser.LiteralContext ctx) throws io.github.snaill.exception.FailedCheckException {
        if (ctx.numberLiteral() != null) {
            return parseNumberLiteral(ctx.numberLiteral());
        } else if (ctx.stringLiteral() != null) {
            return parseStringLiteral(ctx.stringLiteral());
        } else if (ctx.booleanLiteral() != null) {
            return parseBooleanLiteral(ctx.booleanLiteral());
        }
        throw new io.github.snaill.exception.FailedCheckException(
            new io.github.snaill.result.CompilationError(
                io.github.snaill.result.ErrorType.UNKNOWN_TYPE,
                (ctx != null && ctx.getStart() != null) ?
                    io.github.snaill.ast.SourceBuilder.toSourceLine(ctx.getStart().getInputStream().toString(), ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), ctx.getText().length()) :
                    io.github.snaill.ast.SourceBuilder.toSourceCode(ctx),
                "Unknown literal",
                ""
            ).toString()
        );
    }

    private Node parseNumberLiteral(SnailParser.NumberLiteralContext ctx) {
        return new NumberLiteral(Long.parseLong(ctx.NUMBER().getText()));
    }

    private Node parseStringLiteral(SnailParser.StringLiteralContext ctx) {
        String text = ctx.STRING().getText();
        return new StringLiteral(text.substring(1, text.length() - 1));
    }

    private Node parseBooleanLiteral(SnailParser.BooleanLiteralContext ctx) {
        return new BooleanLiteral(ctx.getText().equals("true"));
    }

    private Node parseIdentifier(SnailParser.IdentifierContext ctx, Scope parent) throws io.github.snaill.exception.FailedCheckException {
        if (ctx.variableIdentifier() != null) {
            String name = ctx.variableIdentifier().IDENTIFIER().getText();
            if (parent != null && parent.resolveVariable(name) == null) {
                String before = ctx.getStart() != null ?
                    io.github.snaill.ast.SourceBuilder.toSourceLine(ctx.getStart().getInputStream().toString(), ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), name.length()) :
                    io.github.snaill.ast.SourceBuilder.toSourceCode(ctx);
                throw new io.github.snaill.exception.FailedCheckException(
                    new io.github.snaill.result.CompilationError(
                        io.github.snaill.result.ErrorType.UNKNOWN_VARIABLE,
                        before,
                        "Unknown variable: " + name,
                        ""
                    ).toString()
                );
            }
            Identifier id = new Identifier(name);
            if (parent != null) { // Устанавливаем окружающую область видимости
                id.setEnclosingScope(parent);
            }
            if (ctx.variableIdentifier().IDENTIFIER() != null) {
                id.setSourceInfo(ctx.variableIdentifier().IDENTIFIER().getSymbol().getLine(), ctx.variableIdentifier().IDENTIFIER().getSymbol().getCharPositionInLine(), ctx.start.getInputStream().toString());
            }
            return id;
        } else if (ctx.arrayElement() != null) {
            return parseArrayElement(ctx.arrayElement(), parent);
        }
        throw new io.github.snaill.exception.FailedCheckException(
            new io.github.snaill.result.CompilationError(
                io.github.snaill.result.ErrorType.UNKNOWN_TYPE,
                (ctx != null && ctx.getStart() != null) ?
                    io.github.snaill.ast.SourceBuilder.toSourceLine(ctx.getStart().getInputStream().toString(), ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), ctx.getText().length()) :
                    io.github.snaill.ast.SourceBuilder.toSourceCode(ctx),
                "Invalid identifier",
                ""
            ).toString()
        );
    }

    private Node parseArrayElement(SnailParser.ArrayElementContext ctx, Scope parent) throws io.github.snaill.exception.FailedCheckException {
        Identifier identifierNode = new Identifier(ctx.IDENTIFIER().getText());
        if (ctx.IDENTIFIER() != null && ctx.IDENTIFIER().getSymbol() != null) {
            identifierNode.setSourceInfo(ctx.IDENTIFIER().getSymbol().getLine(), ctx.IDENTIFIER().getSymbol().getCharPositionInLine(), parent.getSource());
        }
        identifierNode.setEnclosingScope(parent);

        List<Expression> indices = new ArrayList<>();
        if (ctx.expression() != null) {
            for (SnailParser.ExpressionContext exprCtx : ctx.expression()) {
                indices.add((Expression) parseExpression(exprCtx, parent));
            }
        }

        ArrayElement arrayElementNode = new ArrayElement(identifierNode, indices);
        arrayElementNode.setSourceInfo(ctx.start.getLine(), ctx.start.getCharPositionInLine(), parent.getSource());
        arrayElementNode.setEnclosingScope(parent);
        return arrayElementNode;
    }

    private Node parseFunctionCall(SnailParser.FunctionCallContext ctx, Scope parent) {
        String name = ctx.IDENTIFIER().getText();
        List<Expression> args = ctx.argumentList() != null ?
                parseArgumentList(ctx.argumentList(), parent) : List.of();
        FunctionCall call = new FunctionCall(name, args);
        if (ctx.IDENTIFIER() != null && ctx.IDENTIFIER().getSymbol() != null && ctx.start != null && ctx.start.getInputStream() != null) {
            call.setSourceInfo(ctx.IDENTIFIER().getSymbol().getLine(), ctx.IDENTIFIER().getSymbol().getCharPositionInLine(), ctx.start.getInputStream().toString());
        }
        if (parent != null) {
            call.setEnclosingScope(parent);
        }
        return call;
    }

    private List<Expression> parseArgumentList(SnailParser.ArgumentListContext ctx, Scope parent) {
        return ctx.expression().stream()
                .map(expr -> {
                    try {
                        return (Expression) parseExpression(expr, parent);
                    } catch (io.github.snaill.exception.FailedCheckException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    private Node parseArrayLiteral(SnailParser.ArrayLiteralContext ctx, Scope parent) throws io.github.snaill.exception.FailedCheckException {
        List<Expression> elements = new ArrayList<>();
        if (ctx.expression() != null) {
            for (var exprCtx : ctx.expression()) {
                if (exprCtx == null) continue;
                Expression expr = (Expression) parseExpression(exprCtx, parent);
                if (expr == null) continue;
                elements.add(expr);
            }
        }
        return new ArrayLiteral(elements);
    }

    private Node parseType(SnailParser.TypeContext ctx, Scope currentScope) throws io.github.snaill.exception.FailedCheckException {
        if (ctx.primitiveType() != null) {
            return parsePrimitiveType(ctx.primitiveType(), currentScope);
        } else if (ctx.arrayType() != null) {
            return parseArrayType(ctx.arrayType(), currentScope);
        }
        throw new io.github.snaill.exception.FailedCheckException(
            new io.github.snaill.result.CompilationError(
                io.github.snaill.result.ErrorType.UNKNOWN_TYPE,
                (ctx != null && ctx.getStart() != null) ?
                    io.github.snaill.ast.SourceBuilder.toSourceLine(ctx.getStart().getInputStream().toString(), ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), ctx.getText().length()) :
                    io.github.snaill.ast.SourceBuilder.toSourceCode(ctx),
                "Unknown type",
                ""
            ).toString()
        );
    }

    private Node parseArrayType(SnailParser.ArrayTypeContext ctx, Scope currentScope) throws io.github.snaill.exception.FailedCheckException {
        Type elementType = (Type) parseType(ctx.type(), currentScope);
        NumberLiteral size = new NumberLiteral(Long.parseLong(ctx.numberLiteral().getText()));
        // Устанавливаем информацию об источнике для NumberLiteral, если это AbstractNode и ему это нужно
        if (size instanceof AbstractNode sn) {
            sn.setSourceInfo(ctx.numberLiteral().start.getLine(), ctx.numberLiteral().start.getCharPositionInLine(), currentScope.getSource());
            sn.setEnclosingScope(currentScope);
        }
        ArrayType at = new ArrayType(elementType, size);
        at.setSourceInfo(ctx.start.getLine(), ctx.start.getCharPositionInLine(), currentScope.getSource());
        at.setEnclosingScope(currentScope);
        return at;
    }

     private Node parsePrimitiveType(SnailParser.PrimitiveTypeContext ctx, Scope currentScope) {
        // С текущей грамматикой (primitiveType : 'i32' | 'usize' | 'void' | 'string' | 'bool'),
        // IDENTIFIER не является альтернативой, поэтому ctx.IDENTIFIER() не будет доступен.
        // Этот метод будет создавать узлы PrimitiveType только для предопределенных ключевых слов.
        // Для поддержки пользовательских типов правило грамматики 'primitiveType' или 'type' должно включать IDENTIFIER.
        PrimitiveType pt = new PrimitiveType(ctx.getText());
        pt.setSourceInfo(ctx.start.getLine(), ctx.start.getCharPositionInLine(), currentScope.getSource());
        pt.setEnclosingScope(currentScope);
        return pt;
    }
}