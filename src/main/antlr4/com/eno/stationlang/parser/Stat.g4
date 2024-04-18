grammar Stat;

program : declaration* EOF;
declaration : varDecl | funcDecl;
varDecl : varDef (AFFECT expression)? SEMICOLON;
funcDecl : ID LPAR parameters? RPAR (COLON type)? block;

type : BOOLEANTYPE
     | NUMBERTYPE
     | DEVICETYPE
     ;

parameters : varDef(COMMA varDef)*;
varDef : type ID;
block: LBRACES (statement | varDecl)* RBRACES;

statement : IF LPAR condition=expression RPAR block (ELSE block)?   #ifStmt
          | left=expression AFFECT right=expression SEMICOLON       #assignStmt
          | expression SEMICOLON                                    #exprStmt
          | RETURN expression SEMICOLON                             #returnStmt
          ;

expression  : LPAR expression RPAR                                  #parenExpr
            | ID                                                    #varExpr
            | ID LPAR (expression (COMMA expression)*)? RPAR        #callExpr
            | QUOTES ID QUOTES                                      #propertyExpr
            | NUMBER                                                #numberExpr
            | MINUS expression                                      #unaryMinusExpr
            | expression op=(TIMES | DIVIDE | MODULO) expression    #multDivideExpr
            | expression op=(PLUS | MINUS) expression               #addSubExpr
            | TRUE                                                  #trueExpr
            | FALSE                                                 #falseExpr
            | expression op=(EQUAL | DIFF) expression               #equalExpr
            | expression op=(LT | GT | LTE | GTE) expression        #compExpr
            | expression op=(AND | OR) expression                   #logicExpr
            | NOT expression                                        #notExpr
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
MODULO: '%';
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

BOOLEANTYPE: 'boolean';
NUMBERTYPE: 'number';
DEVICETYPE: 'device';

IF: 'if';
ELSE: 'else';

ID : LETTER (LETTER | DIGIT)* ;
fragment DIGIT : '0'..'9';
fragment LETTER : 'A'..'Z' | 'a'..'z';

NUMBER : '-'? INT ('.' [0-9] +)?;
fragment INT : '0' | [1-9] [0-9]*;

WS : [ \t\n]+ -> skip;