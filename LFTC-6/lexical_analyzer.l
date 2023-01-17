%{
    #include <stdio.h>
    #include "bison.tab.h"

    void showError(char *s, int line, int col) {
    	printf("Error: %s at line %d, column %d\n", s, line, col);
    }
%}

integer [0-9]+
id      [a-zA-Z_][a-zA-Z0-9_]*

%option noyywrap

%%

{integer} {
    sscanf(yytext, "%d", &yylval.value);
    return CONST;
}

{id} {
    yylval.name = yytext;
    return ID;
}

"int" {return INT;}
"+" {return PLUS;}
"-" {return MINUS;}
"*" {return TIMES;}
"/" {return DIVIDE;}
"=" {return EQUAL;}

. {return OTHER;}

%%

