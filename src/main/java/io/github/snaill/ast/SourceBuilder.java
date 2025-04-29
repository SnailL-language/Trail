package io.github.snaill.ast;

import java.util.stream.Collectors;

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
                    if (stmt instanceof Expression) {
                        sb.append(';');
                    }
                }
                if (!isRoot) {
                    sb.insert(0, "{").append("}");
                }
                yield sb.toString();
            }
            case FunctionDeclaration func -> {
                StringBuilder sb = new StringBuilder();
                sb.append("fn ").append(func.getName()).append("(");
                String params = func.getParameterList().stream()
                        .map(param -> toSourceCode(param, false))
                        .collect(Collectors.joining(", "));
                sb.append(params).append(")");
                String returnType = toSourceCode(func.getReturnType(), false);
                if (!returnType.equals("void")) {
                    sb.append(" -> ").append(returnType);
                }
                sb.append(toSourceCode(func.getScope(), false));
                yield sb.toString();
            }
            case Parameter param -> param.getName() + ": " + toSourceCode(param.getType(), false);
            case VariableDeclaration var -> "let " + var.getName() +
                    ": " + toSourceCode(var.getType(), false) +
                    " = " + toSourceCode(var.getValue(), false) +
                    ";";
            case StringLiteral stringLiteral -> stringLiteral.getValue();
            case NumberLiteral numberLiteral -> String.valueOf(numberLiteral.getValue());
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
            case ForLoop forLoop -> "for (" + toSourceCode(forLoop.getDeclaration(), false) +
                    toSourceCode(forLoop.getCondition(), false) + "; " + toSourceCode(forLoop.getStep(), false) +
                    ")" +
                    toSourceCode(forLoop.getBody(), false);
            case IfStatement ifStmt -> "if (" + toSourceCode(ifStmt.getCondition(), false) + ") " +
                    toSourceCode(ifStmt.getBody(), false);
            case BreakStatement _ -> "break;";
            case AssigmentExpression assignExpr -> assignExpr.getVariableName() +
                    " " + assignExpr.getOperator() + " " +
                    toSourceCode(assignExpr.getExpression(), false);
            default -> "// Неизвестный узел: " + node.getClass().getSimpleName();
        };
    }
}
