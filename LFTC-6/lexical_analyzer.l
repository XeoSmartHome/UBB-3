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

"fmt.Println" {return FMT_PRINTLN;}
"fmt.Scanf" {return SCAN;}
"&" {return AMP;}
"(" {return LPAREN;}
")" {return RPAREN;}
"int" {return INT;}
"+" {return PLUS;}
"-" {return MINUS;}
"*" {return TIMES;}
"/" {return DIVIDE;}
"=" {return EQUAL;}
\n {rowCounter++;}
[ \t] { /* ignore */ }

var {
    return VAR;
}

{id} {
    strcpy(yylval.name, yytext);
    return ID;
}

{integer} {
    strcpy(yylval.name, yytext);
    return CONST;
}


. {
    printf("Error: invalid character %s at line %d, column %d\n", yytext, rowCounter, yyleng);
    return OTHER;
}

%%

