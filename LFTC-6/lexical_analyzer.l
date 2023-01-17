%{
    #include <stdio.h>
    #include <string.h>
    #include "bison.tab.h"

    int rowCounter = 1;

    void showError(char *s, int line, int col) {
    	printf("Error: %s at line %d, column %d\n", s, line, col);
    }
%}

integer [0-9]+
id      [a-zA-Z_][a-zA-Z0-9_]*

%option noyywrap

%%

"int" {return INT;}
"+" {return PLUS;}
"-" {return MINUS;}
"*" {return TIMES;}
"/" {return DIVIDE;}
"=" {return EQUAL;}
\n {rowCounter++;}
[ \t] { /* ignore */ }

var {
    printf("var\n");
    return VAR;
}

{integer} {
    return CONST;
}

{id} {
    strcpy(yylval.name, yytext);
    return ID;
}


. {return OTHER;}

%%

