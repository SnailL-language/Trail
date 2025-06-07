package io.github.snaill.ast;

import java.util.stream.Collectors;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class SourceBuilder {
    private static final String INDENT = "    ";
    private static final String NEW_LINE = System.lineSeparator();

    // Operator Precedence: Higher value means higher precedence
    private static int getBinaryOperatorPrecedence(String op) {
        return switch (op) {
            case "*", "/", "%": yield 6;
            case "+", "-": yield 5; // Binary plus/minus
            case "<", ">", "<=", ">=": yield 4;
            case "==", "!=": yield 3;
            case "&&": yield 2;
            case "||": yield 1;
            case "=": yield 0; // Assignment has very low precedence
            default: yield 0; // For other non-binary ops or lowest precedence
        };
    }

    private static int getUnaryOperatorPrecedence(String op) {
        return switch (op) {
            case "!", "-": // Logical NOT, Unary MINUS
                yield 7; // Higher than multiplicative
            default: yield 0;
        };
    }

    private static boolean isLeftAssociative(String op) {
        // Most binary operators are left-associative.
        // Assignment operators (=, +=, -= etc.) are right-associative.
        // Logical OR (||) and Logical AND (&&) are left-associative.
        // Equality (==, !=) and Relational (<, >, <=, >=) are non-associative in some contexts but typically parse left-to-right.
        // Additive (+, -) and Multiplicative (*, /, %) are left-associative.
        switch (op) {
            case "=":
            case "+=":
            case "-=":
            case "*=":
            case "/=":
            case "%=":
                return false; // Right-associative
            default:
                return true; // Assume left-associative for others
        }
    }

    public static String toSourceCode(Node node) {
        return toSourceCodeRecursive(node, true, 0, 0);
    }

    private static String toSourceCodeRecursive(Node node, boolean isRootOrBlockItem, int indentLevel, int parentPrecedence) {
        if (node == null) {
            return "";
        }

        String currentIndent = INDENT.repeat(indentLevel);
        String prefix = isRootOrBlockItem ? currentIndent : "";

        return switch (node) {
            case Scope scope -> {
                StringBuilder sb = new StringBuilder();
                if (!isRootOrBlockItem) { sb.append("{"); }
                for (Statement stmt : scope.getStatements()) {
                    sb.append(NEW_LINE);
                    sb.append(toSourceCodeRecursive(stmt, true, isRootOrBlockItem ? indentLevel : indentLevel + 1, 0));
                }
                if (!scope.getStatements().isEmpty()) {
                    sb.append(NEW_LINE);
                    sb.append(INDENT.repeat(isRootOrBlockItem ? indentLevel : indentLevel + 1)); 
                }
                if (!isRootOrBlockItem) { sb.append("}"); }
                yield sb.toString();
            }
            case FunctionDeclaration func -> {
                StringBuilder sb = new StringBuilder(prefix);
                sb.append("fn ").append(func.getName()).append("(");
                String params = func.getParameters().stream()
                        .map(param -> toSourceCodeRecursive(param, false, indentLevel, 0))
                        .collect(Collectors.joining(", "));
                sb.append(params).append(")");
                Type actualReturnType = func.getReturnType();
                String returnTypeStr = toSourceCodeRecursive(actualReturnType, false, indentLevel, 0);
                if (!(actualReturnType instanceof PrimitiveType && "void".equals(((PrimitiveType) actualReturnType).getName()) && !func.isReturnTypeExplicit())) {
                    sb.append(" -> ").append(returnTypeStr);
                }
                sb.append(" ").append(toSourceCodeRecursive(func.getBody(), false, indentLevel, 0));
                yield sb.toString();
            }
            case Parameter param -> prefix + param.getName() + ": " + toSourceCodeRecursive(param.getType(), false, indentLevel, 0);
            case VariableDeclaration varDecl -> {
                String typeStr = toSourceCodeRecursive(varDecl.getType(), false, indentLevel, 0);
                String valueStr = "";
                if (varDecl.getValue() != null) {
                    valueStr = " = " + toSourceCodeRecursive(varDecl.getValue(), false, indentLevel, 0);
                }
                yield prefix + "let " + varDecl.getName() + ": " + typeStr + valueStr + ";";
            }
            case StringLiteral strLit -> prefix + "\"" + strLit.getValue() + "\"";
            case NumberLiteral numLit -> prefix + numLit.getValue();
            case BooleanLiteral boolLit -> prefix + boolLit.getValue();
            case ArrayElement arrElem -> {
                StringBuilder sb = new StringBuilder(prefix + toSourceCodeRecursive(arrElem.getIdentifier(), false, indentLevel, 0));
                for (Expression dim : arrElem.getDims()) {
                    sb.append('[').append(toSourceCodeRecursive(dim, false, indentLevel, 0)).append(']');
                }
                yield sb.toString();
            }
            case Identifier id -> prefix + id.getName();
            case PrimitiveType primType -> prefix + primType.getName();
            case ArrayType arrType -> prefix + "[" + toSourceCodeRecursive(arrType.getElementType(), false, indentLevel, 0) +
                    "; " + toSourceCodeRecursive(arrType.getSize(), false, indentLevel, 0) + "]";
            case BinaryExpression binExpr -> {
                int currentPrecedence = getBinaryOperatorPrecedence(binExpr.getOperator());
                String op = binExpr.getOperator();

                String leftStr = toSourceCodeRecursive(binExpr.getLeft(), false, indentLevel, currentPrecedence);

                int rightParentPrecedence = currentPrecedence;
                if (isLeftAssociative(op)) {
                    // For left-associative operators, if the right operand has precedence
                    // equal to or lower than the current operator, it might need parentheses
                    // if it's not a primary expression, to enforce left-to-right evaluation visually
                    // or to override it if the AST structure demands (e.g. a - (b+c)).
                    // If right child's precedence is strictly lower, it will be parenthesized by its own rule.
                    // If right child's precedence is equal, we need to force parenthesizing it.
                    // So, pass a slightly higher precedence to its recursive call.
                    if (binExpr.getRight() instanceof BinaryExpression) {
                        BinaryExpression rightBinExpr = (BinaryExpression) binExpr.getRight();
                        if (getBinaryOperatorPrecedence(rightBinExpr.getOperator()) == currentPrecedence) {
                            rightParentPrecedence = currentPrecedence + 1;
                        }
                    }
                } else { // Right-associative operator
                    // For right-associative operators, if the left operand has precedence
                    // equal to currentPrecedence, it needs to be parenthesized.
                    if (binExpr.getLeft() instanceof BinaryExpression) {
                        BinaryExpression leftBinExpr = (BinaryExpression) binExpr.getLeft();
                        if (getBinaryOperatorPrecedence(leftBinExpr.getOperator()) == currentPrecedence) {
                            // Re-generate leftStr with higher parent precedence to force parens
                            leftStr = toSourceCodeRecursive(binExpr.getLeft(), false, indentLevel, currentPrecedence + 1);
                        }
                    }
                }

                String rightStr = toSourceCodeRecursive(binExpr.getRight(), false, indentLevel, rightParentPrecedence);
                
                String content = leftStr + " " + op + " " + rightStr;

                if (currentPrecedence < parentPrecedence) {
                    content = "(" + content + ")";
                } else if (currentPrecedence == parentPrecedence && parentPrecedence != 0) {
                    // If current precedence is same as parent, and parent is not assignment (0) or root (0),
                    // and this operator is different or associativity demands it.
                    // This is to handle cases like (a+b)*c vs a+(b*c)
                    // If parent op is left-associative, and this op is on the right and has same precedence, it needs parens.
                    // This is covered by the rightParentPrecedence logic for the recursive call.
                    // However, if the *parent* is right-associative, and this op is on the left with same precedence.
                    // Example: a = (b = c). Here parent is '=', right child is (b=c).
                    // When processing (b=c), its parentPrecedence is for outer '='.
                    // This specific 'else if' might be too broad or redundant with the recursive call adjustments.
                }
                yield prefix + content;
            }
            case ParenthesizedExpression parenExpr -> {
                // Parent precedence for inner expression should be low (e.g., 0) to prevent extra parens inside
                String innerExprStr = toSourceCodeRecursive(parenExpr.getInnerExpression(), false, indentLevel, 0);
                yield prefix + "(" + innerExprStr + ")";
            }
            case UnaryExpression unExpr -> {
                int currentUnaryPrecedence = getUnaryOperatorPrecedence(unExpr.getOperator());
                Expression argument = unExpr.getArgument();
                String operator = unExpr.getOperator();

                // Determine the precedence to pass to the argument's recursive call.
                // Argument needs to be parenthesized if its own operator precedence is lower OR EQUAL
                // (for unary-unary like -(-a) or unary-binary like -(a*b) where op prec might be close).
                // Passing currentUnaryPrecedence ensures that if argument's precedence is < currentUnaryPrecedence, it's parenthesized.
                // For -(-a): inner -a has precedence 7. Outer - has precedence 7. 7 < 7 is false. So inner -a is not parenthesized by default.
                String argStr = toSourceCodeRecursive(argument, false, indentLevel, currentUnaryPrecedence);

                // Specific fix for nested unary operators of the same kind, e.g., -(-a) or !(!b)
                // If the argument is a UnaryExpression with the same operator, its string form (argStr)
                // needs to be explicitly parenthesized because the recursive call might not have done it
                // (as its precedence wasn't strictly less than the parentPrecedence it received).
                if (argument instanceof UnaryExpression) {
                    UnaryExpression argUnaryExpr = (UnaryExpression) argument;
                    if (argUnaryExpr.getOperator().equals(operator)) {
                        // argStr is like "-a" or "!b". We need "(-a)" or "(!b)".
                        argStr = "(" + argStr + ")";
                    }
                }
                // Also, if a unary operator is applied to a binary expression that is not naturally parenthesized by precedence,
                // e.g. -(a+b). Here '+' (5) is lower than '-' (7). So (a+b) is correctly generated by recursive call.
                // What about -(a*b)? '*' (6) is lower than '-' (7). So (a*b) is generated.
                // This seems fine. The primary issue was unary-unary.

                String content = operator + argStr;

                // Parenthesize the whole current unary expression if its precedence is lower than its parent context's operator.
                if (currentUnaryPrecedence < parentPrecedence) {
                    content = "(" + content + ")";
                }
                yield prefix + content;
            }
            case ReturnStatement retStmt -> {
                String valStr = toSourceCodeRecursive(retStmt.getReturnable(), false, indentLevel, 0);
                yield prefix + "return" + (valStr.isEmpty() ? "" : " " + valStr) + ";";
            }
            case FunctionCall funcCall -> {
                StringBuilder sb = new StringBuilder(prefix);
                sb.append(funcCall.getName()).append("(");
                String args = funcCall.getArguments().stream()
                        .map(arg -> toSourceCodeRecursive(arg, false, indentLevel, 0))
                        .collect(Collectors.joining(", "));
                sb.append(args).append(")");
                yield sb.toString();
            }
            case ArrayLiteral arrLit -> {
                StringBuilder sb = new StringBuilder(prefix);
                sb.append("[");
                String elems = arrLit.getElements().stream()
                        .map(elem -> toSourceCodeRecursive(elem, false, indentLevel, 0))
                        .collect(Collectors.joining(", "));
                sb.append(elems).append("]");
                yield sb.toString();
            }
            case WhileLoop whileLoop -> prefix + "while (" + toSourceCodeRecursive(whileLoop.getCondition(), false, indentLevel, 0) + ") " +
                    toSourceCodeRecursive(whileLoop.getBody(), false, indentLevel, 0);
            case ForLoop forLoop -> {
                StringBuilder sb = new StringBuilder(prefix);
                sb.append("for(");
                String initStr = toSourceCodeRecursive(forLoop.getInitialization(), false, indentLevel, 0);
                if (initStr.endsWith(";")) initStr = initStr.substring(0, initStr.length() - 1);
                sb.append(initStr).append("; ");
                sb.append(toSourceCodeRecursive(forLoop.getCondition(), false, indentLevel, 0)).append("; ");
                String stepStr = toSourceCodeRecursive(forLoop.getStep(), false, indentLevel, 0);
                 if (stepStr.endsWith(";")) stepStr = stepStr.substring(0, stepStr.length() - 1);
                sb.append(stepStr).append(") ");
                sb.append(toSourceCodeRecursive(forLoop.getBody(), false, indentLevel, 0));
                yield sb.toString();
            }
            case IfStatement ifStmt -> {
                StringBuilder sb = new StringBuilder(prefix);
                sb.append("if (").append(toSourceCodeRecursive(ifStmt.getCondition(), false, indentLevel, 0)).append(") ");
                sb.append(toSourceCodeRecursive(ifStmt.getBody(), false, indentLevel, 0));
                if (ifStmt.getElseBody() != null) {
                    sb.append(NEW_LINE).append(currentIndent);
                    sb.append("else ");
                    sb.append(toSourceCodeRecursive(ifStmt.getElseBody(), false, indentLevel, 0));
                }
                yield sb.toString();
            }
            case BreakStatement breakStmt -> prefix + "break;";
            case AssignmentExpression assignExpr -> {
                Expression assignLeft = assignExpr.getLeft();
                Expression assignRight = assignExpr.getRight();
                String tempPrefix = isRootOrBlockItem ? currentIndent : "";

                String leftCode = toSourceCodeRecursive(assignLeft, false, indentLevel, 0); 
                // Pass precedence of '=' for the right-hand side, as assignment is right-associative 
                // and has the lowest precedence.
                String rightCode = toSourceCodeRecursive(assignRight, false, indentLevel, getBinaryOperatorPrecedence("="));
                
                yield tempPrefix + leftCode + " " + assignExpr.getOperator() + " " + rightCode;
            }
            case ExpressionStatement exprStmt -> prefix + toSourceCodeRecursive(exprStmt.getExpression(), false, indentLevel, 0) + ";";
            case AST ast -> toSourceCodeRecursive(ast.root(), isRootOrBlockItem, indentLevel, 0);
            default -> prefix + "// Unknown node: " + node.getClass().getSimpleName();
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

    public static String toSourceLine(String source, int line, int charPosition, int length) {
        if (source == null || line < 1) return "";
        String[] lines = source.split("\\R"); // Use \R for any Unicode linebreak sequence
        if (line > lines.length) return "";
        String codeLine = lines[line - 1];
        StringBuilder pointer = new StringBuilder();
        for (int i = 0; i < charPosition; i++) pointer.append(codeLine.charAt(i) == '\t' ? '\t' : ' ');
        pointer.append("^".repeat(Math.max(1, length)));
        return codeLine + System.lineSeparator() + pointer;
    }
}
