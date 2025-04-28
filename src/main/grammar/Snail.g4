grammar Snail;

@header {
package io.github.snaill.parser;
}

// === Корневая точка программы ===
program
    : statement+ EOF
    ;

// === Операторы ===
statement
    : funcDeclaration               # FuncDeclStatement
    | variableDeclaration           # VarDeclStatement
    | expressionStatement           # ExprStmt
    | forLoop                       # ForLoopStmt
    | whileLoop                     # WhileLoopStmt
    | ifCondition                   # IfConditionStmt
    | breakStatement                # BreakStmt
    | returnStatement               # ReturnStmt
    ;

expressionStatement
    : expression ';'
    ;

// === Функции ===
funcDeclaration
    : 'fn' IDENTIFIER '(' paramList ')' ('->' type)? scope
    ;

paramList
    : (param (',' param)*)?
    ;

param
    : IDENTIFIER ':' type
    ;

// === Аргументы вызова функции ===
argumentList
    : (expression (',' expression)*)?
    ;

// === Блок кода ===
scope
    : '{' statement* '}'
    ;

// === Объявление переменных ===
variableDeclaration
    : 'let' IDENTIFIER ':' type '=' expression ';'
    ;

// === Циклы ===
forLoop
    : 'for' '(' variableDeclaration expression ';' expression ')' scope
    ;

whileLoop
    : 'while' '(' expression ')' scope
    ;

// === Условия ===
ifCondition
    : 'if' '(' expression ')' scope
    ;

// === Специальные операторы ===
breakStatement
    : 'break' ';'
    ;

returnStatement
    : 'return' expression? ';'
    ;

// === Выражения ===
expression
    : IDENTIFIER assignmentOperator expression          # AssignmentExpr
    | expression '||' expression                        # LogicalOrExpr
    | expression '&&' expression                        # LogicalAndExpr
    | expression ('==' | '!=') expression               # EqualityExpr
    | expression ('>' | '<' | '>=' | '<=') expression   # RelationalExpr
    | expression ('+' | '-') expression                 # AdditiveExpr
    | expression ('*' | '/') expression                 # MultiplicativeExpr
    | '!' expression                                    # NotExpr
    | '-' expression                                    # NegateExpr
    | primaryExpression                                 # PrimaryExpr
    ;

// Присваивание
assignmentOperator
    : '='
    | '+='
    | '-='
    | '*='
    | '/='
    ;

// Основные выражения
primaryExpression
    : literal                            # LiteralPrimaryExpr
    | IDENTIFIER                         # IdentifierPrimaryExpr
    | functionCall                       # FunctionCallPrimaryExpr
    | arrayLiteral                       # ArrayLiteralPrimaryExpr
    | '(' expression ')'                 # ParenthesizedPrimaryExpr
    ;

// Литералы
literal
    : NUMBER
    | STRING
    ;

// Вызов функции
functionCall
    : IDENTIFIER '(' argumentList ')'
    ;

// Массив
arrayLiteral
    : '[' (expression (',' expression)*)? ']'
    ;

// === Типы ===
type
    : PRIMITIVE_TYPE
    | 'string'
    | '[' type ';' NUMBER ']'
    ;

// === Лексерные правила ===
NUMBER : [0-9]+;
STRING : '"' ~["]* '"';
PRIMITIVE_TYPE : 'i32' | 'usize';
IDENTIFIER : [a-zA-Z_][a-zA-Z0-9_]*;
WS : [ \t\r\n]+ -> skip;
LINE_COMMENT : '//' ~[\r\n]* -> skip;
BLOCK_COMMENT : '/*' .*? '*/' -> skip;