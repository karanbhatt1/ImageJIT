grammar Imagescript;

// Parser Rules
script: statement* EOF;

statement: loadStatement
         | resizeStatement
         | grayscaleStatement
         | rotateStatement
         | flipStatement
         | saveStatement
         ;

loadStatement: 'LOAD' filePath=STRING 'AS' varName=IDENTIFIER ';';

resizeStatement: 'RESIZE' inputVar=IDENTIFIER 'WIDTH' width=INT 'HEIGHT' height=INT 'AS' outputVar=IDENTIFIER ';';

grayscaleStatement: 'GRAYSCALE' inputVar=IDENTIFIER 'AS' outputVar=IDENTIFIER ';';

rotateStatement: 'ROTATE' inputVar=IDENTIFIER 'ANGLE' angle=NUMBER 'AS' outputVar=IDENTIFIER ';';

flipStatement: 'FLIP' inputVar=IDENTIFIER flipDirection 'AS' outputVar=IDENTIFIER ';';

flipDirection: 'HORIZONTAL' | 'VERTICAL' | 'BOTH';

// CORRECTED: Added labels (filePath, formatName) to STRING tokens
saveStatement: 'SAVE' varName=IDENTIFIER 'TO' filePath=STRING 'FORMAT' formatName=STRING ';';

// Lexer Rules
IDENTIFIER: [a-zA-Z_][a-zA-Z0-9_]*;
INT         : [0-9]+ ; // For integer dimensions (width, height)
NUMBER      : '-'? [0-9]+ ('.' [0-9]+)? ; // For numbers that might be decimal (like angles)
STRING: '"' (~["\r\n] | '""')* '"';

// Whitespace and Comments
WS: [ \t\r\n]+ -> skip;
COMMENT: '//' ~[\r\n]* -> skip;