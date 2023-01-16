%{
    #include <stdio.h>
    #include "bison.tab.h"

    showError(char *s, int line, int col) {
    	printf("Error: %s at line %d, column %d\n", s, line, col);
    }
%}

integer [0-9]+
string  ".*"
id      [a-zA-Z_][a-zA-Z0-9_]*

%%

{integer} {
    sscanf(yytext, "%d", &yylval.name);
    return INTEGER;
}

{string} {
    yylval.name = yytext;
    return STRING;
}

{id} {
    yylval.name = yytext;
    return ID;
}

"+" {return PLUS;}
"-" {return MINUS;}
"*" {return TIMES;}
"/" {return DIVIDE;}
"=" {return EQUAL;}

. {return OTHER;}

%%

