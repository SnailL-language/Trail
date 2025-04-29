grammar Snail;

@header {
package io.github.snaill.parser;
}

// === Корневая точка программы ===
program
    : variableDeclaration* funcDeclaration+ EOF
    ;

// === Операторы ===
statement
    : variableDeclaration
    | forLoop
    | funcDeclaration
    | whileLoop
    | ifCondition
    | breakStatement
    | returnStatement
    | expression ';'
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
    : IDENTIFIER assignmentOperator expression
    | binaryExpression
    | unaryExpression
    | primaryExpression
    | '(' expression ')'
    ;

binaryExpression : (primaryExpression | '(' expression ')') binaryOperator=('||' | '&&' | '==' | '!=' | '>' | '<' | '>=' | '<=' | '+' | '-' | '*' | '/') expression;

unaryExpression : unaryOperator=('-' | '!') expression;

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
    : literal
    | unaryExpression
    | identifier
    | functionCall
    | arrayLiteral
    ;

// Литералы
literal
    : NUMBER
    | STRING
    ;

identifier : IDENTIFIER;

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
    | 'void'
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