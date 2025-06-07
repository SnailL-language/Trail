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
    : assignmentExpression
    ;

// Level 1: Assignment (right-associative)
assignmentExpression
    : identifier assigmentOperator=('='| '+=' | '-=' | '*=' | '/=') assignmentExpression
    | logicalOrExpression
    ;

// Level 2: Logical OR (left-associative)
logicalOrExpression
    : logicalAndExpression ( '||' logicalAndExpression )*
    ;

// Level 3: Logical AND (left-associative)
logicalAndExpression
    : equalityExpression ( '&&' equalityExpression )*
    ;

// Level 4: Equality (left-associative)
equalityExpression
    : relationalExpression ( ( '==' | '!=' ) relationalExpression )*
    ;

// Level 5: Relational (left-associative)
relationalExpression
    : additiveExpression ( ( '>' | '<' | '>=' | '<=' ) additiveExpression )*
    ;

// Level 6: Additive (left-associative)
additiveExpression
    : multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )*
    ;

// Level 7: Multiplicative (left-associative)
multiplicativeExpression
    : unaryExpression ( ( '*' | '/' ) unaryExpression )*
    ;

// Level 8: Unary (prefix, right-associative)
unaryExpression
    : unaryOperator=('-' | '!') unaryExpression
    | primaryExpression
    ;

// Level 9: Primary expressions
primaryExpression
    : literal
    | identifier             // Covers variableIdentifier and arrayElement
    | functionCall
    | arrayLiteral
    | '(' expression ')'     // Parenthesized expression, restarts precedence
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

arrayElement : IDENTIFIER ('[' expression ']')+ ;

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