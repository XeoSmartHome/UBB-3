%{
	#include <stdio.h>
	#include <stdlib.h>
	#include <string.h>

	extern int yylex();
    	extern int ttparse();
    	extern FILE *yyin;
        extern char *yytext;

        void yyerror(char *s) {
	    fprintf(stderr, "Error: %s\n", s);
	};

	char dataSegment[1000];
	int dataSegmentIndex = 0;
	char codeSegment[1000];
	int codeSegmentIndex = 0;

	char buffer[1000];
	char variablesNames[100][64];

	void addVariable(char *name);
%}

%union {
	int value;
	char name[64];
}

%token <name> ID
%token <name> CONST
%token VAR
%token INT
%token PLUS
%token MINUS
%token TIMES
%token DIVIDE
%token EQUAL
%token OTHER

%type <name> term

%%

program: instruction_list;
instruction_list: instruction instruction_list
	| instruction;
instruction: assignment
	| declaration;

declaration: VAR ID INT {
		addVariable($2);
	};

assignment: ID EQUAL expression;
expression: expression PLUS term
	| expression MINUS term
	| term;
term: ID {

	}
	| CONST {

	};

%%

void addVariable(char *variableName) {
	sprintf(buffer, "%s dw 0\n", variableName);
	strcat(dataSegment, buffer);
}

void addCode(char *code) {
    strcat(codeSegment, code);
    codeSegmentIndex += strlen(code);
}

void writeAsmFile(char *fileName) {
	char asmFileName[100];
	sprintf(asmFileName, "%s.asm", fileName);
	FILE *asmFile = fopen(asmFileName, "w");

	fprintf(asmFile, "bits 64\nglobal start\nextern exit\nimport exit msvcrt.dll\n");
        fprintf(asmFile, "extern printf\nimport printf msvcrt.dll\nextern scanf\nimport scanf msvcrt.dll\n");
        fprintf(asmFile, "\nsegment data use32 class=data\n");
        fprintf(asmFile, "mesaj db \'%%d\', 10, 0\nformat db \'%%d\',0\n");
        fprintf(asmFile, "%s", dataSegment);
        fprintf(asmFile, "\nsegment code use32 class=code\nstart:\nmov eax, 0\n");
        fprintf(asmFile, "%s", codeSegment);
        fprintf(asmFile, "push dword 0\ncall [exit]\n");

        fclose(asmFile);
}

int main(int argc, char **argv) {
	if( argc < 2 ) {
		printf("Usage: %s filename", argv[0]);
		return 1;
	}

	yyin = fopen(argv[1], "r");

	if( !yyin ) {
		printf("Error: cannot open file %s", argv[1]);
		return 1;
	}

	while( !feof(yyin) ) {
		yyparse();
	}

	fclose(yyin);

	printf("Data segment: %s\n\n, code segment: %s\n\n", dataSegment, codeSegment);

	writeAsmFile(argv[1]);

	return 0;
}