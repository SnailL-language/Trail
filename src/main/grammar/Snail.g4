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
    : 'if' '(' expression ')' scope ('else' scope)?
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
    : assigmentExpression
    | binaryExpression
    | unaryExpression
    | primaryExpression
    | '(' expression ')'
    ;

assigmentExpression : identifier assigmentOperator=('='| '+=' | '-=' | '*=' | '/=') expression;

binaryExpression : (primaryExpression | '(' expression ')') binaryOperator=('||' | '&&' | '==' | '!=' | '>' | '<' | '>=' | '<=' | '+' | '-' | '*' | '/') expression;

unaryExpression : unaryOperator=('-' | '!') expression;

// Основные выражения
primaryExpression
    : literal
    | identifier
    | arrayElement
    | functionCall
    | arrayLiteral
    ;

// Литералы
literal
    : numberLiteral
    | stringLiteral
    | booleanLiteral
    ;

stringLiteral : STRING;

numberLiteral : NUMBER;

booleanLiteral : 'true' | 'false' ;

identifier : variableIdentifier | arrayElement;

variableIdentifier : IDENTIFIER;

arrayElement : IDENTIFIER '[' expression ']' | arrayElement '[' numberLiteral ']' ;

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
    : primitiveType
    | arrayType
    ;

arrayType : '[' type ';' numberLiteral ']';

primitiveType : 'i32' | 'usize' | 'void' | 'string' | 'bool';

// === Лексерные правила ===
NUMBER : [0-9]+;
STRING : '"' ~["]* '"';
IDENTIFIER : [a-zA-Z_][a-zA-Z0-9_]*;
WS : [ \t\r\n]+ -> skip;
LINE_COMMENT : '//' ~[\r\n]* -> skip;
BLOCK_COMMENT : '/*' .*? '*/' -> skip;