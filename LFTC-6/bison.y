%{
	#include <stdio.h>

	extern int yylex();
    	extern int ttparse();
    	extern FILE *yyin;
        extern char *yytext;

        void yyerror(char *s) {
	    fprintf(stderr, "Error: %s\n", s);
	}
%}

%union {
	int value;
	char *name;
}

%token <name> ID
%token <name> CONST
%token INT
%token PLUS
%token MINUS
%token TIMES
%token DIVIDE
%token EQUAL
%token OTHER

%%

program: instruction_list;
instruction_list: instruction instruction_list
	| instruction;
instruction: assignment
assignment: ID EQUAL expression;
expression: expression PLUS term
	| expression MINUS term
	| term;
term: ID | CONST;

%%

int main(int argc, char **argv)

}