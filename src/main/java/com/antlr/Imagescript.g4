//grammar Imagescript;
//@header {
//    package com.parsingfiles;
//}
//script: command* EOF;
//
//command:
//      loadCmd
//    | resizeCmd
//    | grayscaleCmd
//    | rotateCmd
//    | flipCmd
//    | saveCmd
//    ;
//
//loadCmd         : LOAD filePath=STRING_LITERAL AS varName=ID SEMI?;
//resizeCmd       : RESIZE inputVar=ID WIDTH width=NUMBER HEIGHT height=NUMBER AS outputVar=ID SEMI?;
//grayscaleCmd    : GRAYSCALE inputVar=ID AS outputVar=ID SEMI?;
//rotateCmd       : ROTATE inputVar=ID ANGLE angle=NUMBER AS outputVar=ID SEMI?;
//flipCmd         : FLIP inputVar=ID direction AS outputVar=ID SEMI?;
//direction       : HORIZONTAL | VERTICAL | BOTH ;
//saveCmd         : SAVE varName=ID TO filePath=STRING_LITERAL FORMAT formatName=STRING_LITERAL SEMI?;
//
//
//LOAD            : 'LOAD';
//AS              : 'AS';
//RESIZE          : 'RESIZE';
//WIDTH           : 'WIDTH';
//HEIGHT          : 'HEIGHT';
//GRAYSCALE       : 'GRAYSCALE';
//ROTATE          : 'ROTATE';
//ANGLE           : 'ANGLE';
//FLIP            : 'FLIP';
//HORIZONTAL      : 'HORIZONTAL';
//VERTICAL        : 'VERTICAL';
//BOTH            : 'BOTH';
//SAVE            : 'SAVE';
//TO              : 'TO';
//FORMAT          : 'FORMAT';
//
//ID              : [a-zA-Z_][a-zA-Z_0-9]* ;
//NUMBER          : [0-9]+ ('.' [0-9]+)? ;
//STRING_LITERAL  : '"' ( ~["\r\n] | '""' )*? '"' ;
//
//SEMI            : ';' ;
//
//WS              : [ \t\r\n]+ -> skip ;
//COMMENT         : '//' .*? '\r'? '\n' -> skip ;

grammar Imagescript;
@header {
    package com.parsingfiles;
}
// Parser Rules
script: statement* EOF;

statement: loadStatement
         | resizeStatement
         | grayscaleStatement
         | rotateStatement
         | flipStatement
         | saveStatement
         ;

loadStatement: 'LOAD' STRING 'AS' IDENTIFIER ';';

resizeStatement: 'RESIZE' IDENTIFIER 'WIDTH' NUMBER 'HEIGHT' NUMBER 'AS' IDENTIFIER ';';

grayscaleStatement: 'GRAYSCALE' IDENTIFIER 'AS' IDENTIFIER ';';

rotateStatement: 'ROTATE' IDENTIFIER 'ANGLE' NUMBER 'AS' IDENTIFIER ';';

flipStatement: 'FLIP' IDENTIFIER flipDirection 'AS' IDENTIFIER ';';

flipDirection: 'HORIZONTAL' | 'VERTICAL' | 'BOTH';

saveStatement: 'SAVE' IDENTIFIER 'TO' STRING 'FORMAT' STRING ';';

// Lexer Rules
IDENTIFIER: [a-zA-Z_][a-zA-Z0-9_]*;
NUMBER: '-'? [0-9]+ ('.' [0-9]+)?;
STRING: '"' (~["\r\n] | '""')* '"';

// Whitespace and Comments
WS: [ \t\r\n]+ -> skip;
COMMENT: '//' ~[\r\n]* -> skip;