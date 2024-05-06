grammar Stat;

program : declaration* EOF;
declaration : varDecl | funcDecl | constDecl | deviceDecl;
varDecl : varDef (AFFECT expression)? SEMICOLON;
constDecl : CONST NUMBERTYPE ID AFFECT NUMBER SEMICOLON;
deviceDecl : DEVICE ID AFFECT NUMBER SEMICOLON;
funcDecl : ID LPAR parameters? RPAR (COLON type)? block;

type : BOOLEANTYPE
     | NUMBERTYPE
     ;

parameters : varDef(COMMA varDef)*;
varDef : type ID;
block: LBRACES (statement | varDecl)* RBRACES;

statement : IF LPAR condition=expression RPAR block (ELSE block)?   #ifStmt
          | ID AFFECT right=expression SEMICOLON                    #assignStmt
          | expression SEMICOLON                                    #exprStmt
          | RETURN expression SEMICOLON                             #returnStmt
          ;

expression  : LPAR expression RPAR                                                                  #parenExpr
            | ID                                                                                    #varExpr
            | ID LPAR (expression (COMMA expression)*)? RPAR                                        #callExpr
            | QUOTES ID QUOTES                                                                      #propertyExpr
            | NUMBER                                                                                #numberExpr
            | MINUS expression                                                                      #unaryMinusExpr
            | leftOperand=expression operator=(TIMES | DIVIDE) rightOperand=expression              #multDivideExpr
            | leftOperand=expression operator=(PLUS | MINUS) rightOperand=expression                #addSubExpr
            | TRUE                                                                                  #trueExpr
            | FALSE                                                                                 #falseExpr
            | leftOperand=expression operator=(EQUAL | DIFF) rightOperand=expression                #equalExpr
            | leftOperand=expression operator=(LT | GT | LTE | GTE) rightOperand=expression         #compExpr
            | leftOperand=expression operator=(AND | OR) rightOperand=expression                    #logicExpr
            | NOT expression                                                                        #notExpr
            ;


//--------Words---------

LPAR: '(';
RPAR: ')';
LBRACES: '{';
RBRACES: '}';

COMMA: ',';
SEMICOLON: ';';
DOT: '.';
COLON: ':';
RETURN: 'return';
QUOTES: '"';

PLUS: '+';
MINUS: '-';
TIMES: '*';
DIVIDE: '/';
AFFECT: '=';

EQUAL: '==';
DIFF: '!=';
LT: '<';
GT: '>';
LTE: '<=';
GTE: '>=';
NOT: '!';

TRUE: 'true';
FALSE: 'false';
AND: '&&';
OR: '||';

CONST: 'const';
DEVICE: 'device';
BOOLEANTYPE: 'boolean';
NUMBERTYPE: 'number';

IF: 'if';
ELSE: 'else';

ID : LETTER (LETTER | DIGIT)* ;
fragment DIGIT : '0'..'9';
fragment LETTER : 'A'..'Z' | 'a'..'z';

NUMBER : '-'? INT ('.' [0-9] +)?;
fragment INT : '0' | [1-9] [0-9]*;

WS : [ \t\n]+ -> skip;