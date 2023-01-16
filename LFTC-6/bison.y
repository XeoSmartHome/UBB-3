%{
	#include <stdio.h>

%}

%token <name> ID
%token <name> CONST
%token PLUS
%token MINUS
%token TIMES
%token DIVIDE
%token EQUALS
%token OTHER

%%

program: instruction_list;
instruction_list: instruction instruction_list
	| instruction;
assignment: ID EQUALS expression;
expression: expression PLUS term
	| expression MINUS term
	| expression TIMES term
	| expression DIVIDE term
	| term;
