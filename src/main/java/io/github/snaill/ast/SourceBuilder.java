package io.github.snaill.ast;

import java.util.stream.Collectors;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class SourceBuilder {
    public static String toSourceCode(Node node) {
        return toSourceCode(node, true);
    }

    private static String toSourceCode(Node node, boolean isRoot) {
        if (node == null) {
            return "";
        }

        return switch (node) {
            case Scope scope -> {
                StringBuilder sb = new StringBuilder();
                for (Statement stmt : scope.getStatements()) {
                    sb.append(toSourceCode(stmt, false));
                }
                if (!isRoot) {
                    sb.insert(0, "{").append("}");
                }
                yield sb.toString();
            }
            case FunctionDeclaration func -> {
                StringBuilder sb = new StringBuilder();
                sb.append("fn ").append(func.getName()).append("(");
                String params = func.getParameters().stream()
                        .map(param -> toSourceCode(param, false))
                        .collect(Collectors.joining(", "));
                sb.append(params).append(")");
                String returnType = toSourceCode(func.getReturnType(), false);
                if (!returnType.equals("void")) {
                    sb.append(" -> ").append(returnType);
                }
                sb.append(toSourceCode(func.getBody(), false));
                yield sb.toString();
            }
            case Parameter param -> param.getName() + ": " + toSourceCode(param.getType(), false);
            case VariableDeclaration var -> "let " + var.getName() +
                    ": " + toSourceCode(var.getType(), false) +
                    " = " + toSourceCode(var.getValue(), false) + ";";
            case StringLiteral stringLiteral -> "\"" + stringLiteral.getValue() + "\"";
            case NumberLiteral numberLiteral -> String.valueOf(numberLiteral.getValue());
            case BooleanLiteral booleanLiteral -> String.valueOf(booleanLiteral.getValue());
            case ArrayElement arrayElement -> {
                StringBuilder sb = new StringBuilder(toSourceCode(arrayElement.getIdentifier()));
                for (Expression num : arrayElement.getDims()) {
                    sb.append('[').append(toSourceCode(num)).append(']');
                }
                yield sb.toString();
            }
            case Identifier identifier -> identifier.getName();
            case PrimitiveType primitiveType -> primitiveType.getName();
            case ArrayType arrayType -> "[" + toSourceCode(arrayType.getElementType(), false) +
                    "; " + toSourceCode(arrayType.getSize(), false) + "]";
            case BinaryExpression binExpr -> toSourceCode(binExpr.getLeft(), false) +
                    " " + binExpr.getOperator() + " " +
                    toSourceCode(binExpr.getRight(), false);
            case UnaryExpression unExpr -> unExpr.getOperator() +
                    toSourceCode(unExpr.getArgument(), false);
            case ReturnStatement ret -> {
                String returnable = toSourceCode(ret.getReturnable(), false);
                yield "return" + (returnable.isEmpty() ? "" : " " + returnable) + ";";
            }
            case FunctionCall funcCall -> {
                StringBuilder sb = new StringBuilder();
                sb.append(funcCall.getName()).append("(");
                String args = funcCall.getArguments().stream()
                        .map(arg -> toSourceCode(arg, false))
                        .collect(Collectors.joining(", "));
                sb.append(args).append(")");
                yield sb.toString();
            }
            case ArrayLiteral arrayLiteral -> {
                StringBuilder sb = new StringBuilder();
                sb.append("[");
                String elements = arrayLiteral.getElements().stream()
                        .map(elem -> toSourceCode(elem, false))
                        .collect(Collectors.joining(", "));
                sb.append(elements).append("]");
                yield sb.toString();
            }
            case WhileLoop whileLoop -> "while (" + toSourceCode(whileLoop.getCondition(), false) + ") " +
                    toSourceCode(whileLoop.getBody(), false);
            case ForLoop forLoop -> {
                String decl = toSourceCode((VariableDeclaration) forLoop.getBody().getChildren().get(0), false);
                String cond = toSourceCode(forLoop.getCondition(), false);
                String step = toSourceCode(forLoop.getStep(), false);
                java.util.List<Statement> stmts = forLoop.getBody().getStatements();
                StringBuilder body = new StringBuilder();
                for (int i = 1; i < stmts.size(); i++) {
                    body.append(toSourceCode(stmts.get(i), false));
                }
                yield "for(" + decl + cond + "; " + step + "){" + body + "}";
            }
            case IfStatement ifStmt -> "if (" + toSourceCode(ifStmt.getCondition(), false) + ") " +
                    toSourceCode(ifStmt.getBody(), false) + (ifStmt.getElseBody() != null ? "else " + toSourceCode(ifStmt.getElseBody(), false) : "");
            case @SuppressWarnings("unused") BreakStatement breakStatement -> "break;";
            case AssignmentExpression assignExpr -> {
                String op = "=";
                if (assignExpr.getRight() instanceof BinaryExpression bin &&
                    bin.getLeft().equals(assignExpr.getLeft())) {
                    String bop = bin.getOperator();
                    if (bop.equals("+") || bop.equals("-") || bop.equals("*") || bop.equals("/")) {
                        op = bop + "=";
                        yield toSourceCode(assignExpr.getLeft(), false) + op + toSourceCode(bin.getRight(), false);
                    }
                }
                yield toSourceCode(assignExpr.getLeft(), false) + " = " + toSourceCode(assignExpr.getRight(), false);
            }
            case ExpressionStatement exprStmt -> {
                yield toSourceCode(exprStmt.getExpression(), false) + ";";
            }
            default -> "// Неизвестный узел: " + node.getClass().getSimpleName();
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
        for (int i = 0; i < Math.max(1, length); i++) pointer.append('^');
        return codeLine + System.lineSeparator() + pointer;
    }
}
