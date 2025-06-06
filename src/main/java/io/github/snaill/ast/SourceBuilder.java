package io.github.snaill.ast;

import java.util.stream.Collectors;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class SourceBuilder {
    private static final String INDENT = "    "; // Or your preferred indentation string
    private static final String NEW_LINE = System.lineSeparator();

    public static String toSourceCode(Node node) {
        // Start with isRoot = true, indentLevel = 0 for the top-level call
        return toSourceCode(node, true, 0);
    }

    // Overload for internal calls that don't change isRoot or indentLevel from context
    private static String toSourceCode(Node node, boolean isRootOrBlockItem, int indentLevel) {
        if (node == null) {
            return "";
        }
        
        String currentIndent = INDENT.repeat(indentLevel);

        // Most expressions are inline and don't get their own indent prefix unless they are block items.
        // The 'isRootOrBlockItem' flag helps distinguish. 'true' if it's a statement in a scope, 'false' for sub-expressions.
        String prefix = isRootOrBlockItem ? currentIndent : ""; 

        return switch (node) {
            case Scope scope -> {
                StringBuilder sb = new StringBuilder();
                if (!isRootOrBlockItem) { // If it's a nested scope like in an if/for/fn, it gets braces
                    sb.append("{");
                }
                for (Statement stmt : scope.getStatements()) {
                    sb.append(NEW_LINE);
                    // Statements in a scope are block items, pass true and increment indent level
                    sb.append(toSourceCode(stmt, true, isRootOrBlockItem ? indentLevel : indentLevel + 1)); 
                }
                if (!scope.getStatements().isEmpty()) {
                    sb.append(NEW_LINE);
                    // Indent the closing brace to the scope's own level
                    sb.append(INDENT.repeat(isRootOrBlockItem ? indentLevel : indentLevel + 1)); 
                }
                if (!isRootOrBlockItem) {
                    sb.append("}");
                }
                yield sb.toString();
            }
            case FunctionDeclaration func -> {
                StringBuilder sb = new StringBuilder(prefix);
                sb.append("fn ").append(func.getName()).append("(");
                String params = func.getParameters().stream()
                        .map(param -> toSourceCode(param, false, indentLevel)) // params are inline
                        .collect(Collectors.joining(", "));
                sb.append(params).append(")");
                String returnTypeStr = toSourceCode(func.getReturnType(), false, indentLevel); // return type is inline
                if (!returnTypeStr.equals("void")) {
                    sb.append(" -> ").append(returnTypeStr);
                }
                // The function body (a Scope) is a block, pass false for isRootOrBlockItem (it's not a root program scope)
                // and increment indentLevel for its contents.
                sb.append(" ").append(toSourceCode(func.getBody(), false, indentLevel)); // Scope handles its own newlines and indent for content
                yield sb.toString();
            }
            case Parameter param -> prefix + param.getName() + ": " + toSourceCode(param.getType(), false, indentLevel);
            case VariableDeclaration var -> prefix + "let " + var.getName() +
                    ": " + toSourceCode(var.getType(), false, indentLevel) +
                    " = " + toSourceCode(var.getValue(), false, indentLevel) + ";";
            case StringLiteral stringLiteral -> prefix + "\"" + stringLiteral.getValue() + "\"";
            case NumberLiteral numberLiteral -> prefix + numberLiteral.getValue();
            case BooleanLiteral booleanLiteral -> prefix + booleanLiteral.getValue();
            case ArrayElement arrayElement -> {
                StringBuilder sb = new StringBuilder(prefix + toSourceCode(arrayElement.getIdentifier(), false, indentLevel));
                for (Expression dim : arrayElement.getDims()) {
                    sb.append('[').append(toSourceCode(dim, false, indentLevel)).append(']');
                }
                yield sb.toString();
            }
            case Identifier identifier -> prefix + identifier.getName();
            case PrimitiveType primitiveType -> prefix + primitiveType.getName();
            case ArrayType arrayType -> prefix + "[" + toSourceCode(arrayType.getElementType(), false, indentLevel) +
                    "; " + toSourceCode(arrayType.getSize(), false, indentLevel) + "]";
            case BinaryExpression binExpr -> prefix + toSourceCode(binExpr.getLeft(), false, indentLevel) +
                    " " + binExpr.getOperator() + " " +
                    toSourceCode(binExpr.getRight(), false, indentLevel);
            case UnaryExpression unExpr -> prefix + unExpr.getOperator() +
                    toSourceCode(unExpr.getArgument(), false, indentLevel);
            case ReturnStatement ret -> {
                String returnable = toSourceCode(ret.getReturnable(), false, indentLevel);
                yield prefix + "return" + (returnable.isEmpty() ? "" : " " + returnable) + ";";
            }
            case FunctionCall funcCall -> {
                StringBuilder sb = new StringBuilder(prefix);
                sb.append(funcCall.getName()).append("(");
                String args = funcCall.getArguments().stream()
                        .map(arg -> toSourceCode(arg, false, indentLevel))
                        .collect(Collectors.joining(", "));
                sb.append(args).append(")");
                yield sb.toString();
            }
            case ArrayLiteral arrayLiteral -> {
                StringBuilder sb = new StringBuilder(prefix);
                sb.append("[");
                String elements = arrayLiteral.getElements().stream()
                        .map(elem -> toSourceCode(elem, false, indentLevel))
                        .collect(Collectors.joining(", "));
                sb.append(elements).append("]");
                yield sb.toString();
            }
            case WhileLoop whileLoop -> prefix + "while (" + toSourceCode(whileLoop.getCondition(), false, indentLevel) + ") " +
                    toSourceCode(whileLoop.getBody(), false, indentLevel); // Scope handles its own newlines/indent
            case ForLoop forLoop -> {
                StringBuilder partBuilder = new StringBuilder(prefix);
                partBuilder.append("for(");
                // Get the initializer statement directly from the ForLoop node
                String initStr = toSourceCode(forLoop.getInitialization(), false, indentLevel); // inline, no extra indent
                // Remove trailing semicolon if present, as it's part of the for-loop syntax not the statement itself here
                if (initStr.endsWith(";")) {
                    initStr = initStr.substring(0, initStr.length() - 1);
                }
                partBuilder.append(initStr).append("; ");
                partBuilder.append(toSourceCode(forLoop.getCondition(), false, indentLevel)).append("; "); // inline
                partBuilder.append(toSourceCode(forLoop.getStep(), false, indentLevel)); // inline
                partBuilder.append(") ");
                // The body (a Scope) is a block. Pass false for isRootOrBlockItem.
                // The Scope case itself will handle indenting its statements relative to 'indentLevel'.
                partBuilder.append(toSourceCode(forLoop.getBody(), false, indentLevel)); 
                yield partBuilder.toString();
            }
            case IfStatement ifStmt -> {
                StringBuilder sb = new StringBuilder(prefix);
                sb.append("if (").append(toSourceCode(ifStmt.getCondition(), false, indentLevel)).append(") ");
                sb.append(toSourceCode(ifStmt.getBody(), false, indentLevel)); // Scope handles its own newlines/indent
                if (ifStmt.getElseBody() != null) {
                    sb.append(NEW_LINE).append(currentIndent); // else on new line, same indent as if
                    sb.append("else ");
                    sb.append(toSourceCode(ifStmt.getElseBody(), false, indentLevel)); // Scope handles its own newlines/indent
                }
                yield sb.toString();
            }
            case @SuppressWarnings("unused") BreakStatement breakStatement -> prefix + "break;";
            case AssignmentExpression assignExpr -> {
                String op = "=";
                Expression assignLeft = assignExpr.getLeft();
                Expression assignRight = assignExpr.getRight();
                String tempPrefix = isRootOrBlockItem ? currentIndent : ""; // Use currentIndent only if it's a statement

                if (assignRight instanceof BinaryExpression bin) {
                    Expression binLeft = bin.getLeft();
                    Expression binRightVal = bin.getRight();
                    boolean sameVar = false;
                    if (assignLeft instanceof Identifier assignId && binLeft instanceof Identifier binId && assignId.getName().equals(binId.getName())) {
                        sameVar = true;
                    } else if (assignLeft instanceof ArrayElement && binLeft instanceof ArrayElement && 
                               toSourceCode(assignLeft, false, indentLevel).equals(toSourceCode(binLeft, false, indentLevel))) {
                        sameVar = true;
                    }

                    if (sameVar) {
                        String bop = bin.getOperator();
                        if ("+".equals(bop) || "-".equals(bop) || "*".equals(bop) || "/".equals(bop)) {
                            op = bop + "=";
                            yield tempPrefix + toSourceCode(assignLeft, false, indentLevel) + " " + op + " " + toSourceCode(binRightVal, false, indentLevel);
                        }
                    }
                }
                yield tempPrefix + toSourceCode(assignLeft, false, indentLevel) + " = " + toSourceCode(assignRight, false, indentLevel);
            }
            case ExpressionStatement exprStmt -> // Expression statement itself applies indent, its expression is inline to it.
                    prefix + toSourceCode(exprStmt.getExpression(), false, indentLevel) + ";";
            case AST astNode -> // The AST node itself is a container (like ASTImpl which wraps the root Scope).
                // Its source representation comes from its root element.
                // Pass isRootOrBlockItem and indentLevel through, as they apply
                // to how the AST's content (the root scope) should be rendered.
                    toSourceCode(astNode.root(), isRootOrBlockItem, indentLevel);
            default -> prefix + "// Неизвестный узел: " + node.getClass().getSimpleName();
        };
    }

    public static String toSourceCode(ParserRuleContext ctx) {
        if (ctx == null) return "";
        Token start = ctx.getStart();
        Token stop = ctx.getStop();
        int startLine = start != null ? start.getLine() : -1;
        int startChar = start != null ? start.getCharPositionInLine() : -1;
        int stopLine = stop != null ? stop.getLine() : -1;
        int stopChar = stop != null ? stop.getCharPositionInLine() : -1;
        String text = ctx.getText();
        return String.format("[line %d:%d - %d:%d] %s", startLine, startChar, stopLine, stopChar, text);
    }

    // Возвращает исходную строку и строку-указатель с ^ под ошибкой
    public static String toSourceLine(String source, int line, int charPosition, int length) {
        if (source == null || line < 1) return "";
        String[] lines = source.split("\r?\n");
        if (line > lines.length) return "";
        String codeLine = lines[line - 1];
        StringBuilder pointer = new StringBuilder();
        for (int i = 0; i < charPosition; i++) pointer.append(codeLine.charAt(i) == '\t' ? '\t' : ' ');
        pointer.append("^".repeat(Math.max(1, length)));
        return codeLine + System.lineSeparator() + pointer;
    }
}
