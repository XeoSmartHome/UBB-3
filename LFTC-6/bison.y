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
	int variablesCount = 0;
	char tempVar[64];

	void addCode(char *code);
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
%token SCAN
%token FMT_PRINTLN
%token LPAREN
%token RPAREN
%token AMP

%type <name> term
%type <name> expression

%%

program: instruction_list;
instruction_list: instruction instruction_list
	| instruction;
instruction: assignment
	| declaration
	| print
	| read;

declaration: VAR ID INT {
		addVariable($2);
	};

print: FMT_PRINTLN LPAREN expression RPAREN {
		printf("print %s\n", $3);
		sprintf(buffer, "push qword %s ", $3);
		addCode(buffer);
		sprintf(buffer, "push qword mesaj");
		addCode(buffer);
		sprintf(buffer, "call printf");
		addCode(buffer);
	};

read: SCAN LPAREN AMP ID RPAREN {
		printf("read %s\n", $3);
		sprintf(buffer, "push qword %s", $3);
		addCode(buffer);
		sprintf(buffer, "push qword mesaj");
		addCode(buffer);
		sprintf(buffer, "call scanf");
		addCode(buffer);
};

assignment: ID EQUAL expression {
		printf("assignment: %s = %s\n", $1, $3);
		sprintf(buffer, "mov word %s, %s", $1, $3);
		addCode(buffer);
	};

expression: term PLUS expression {
		printf("expression: %s + %s\n", $1, $3);

		// check if $1 is a variable
		int isVariable = 0;
		int i;
		for (i = 0; i < variablesCount; i++) {
			if (strcmp(variablesNames[i], $1) == 0) {
				isVariable = 1;
			}
		}

		if(!isVariable) {
			sprintf(tempVar, "temp_%d", variablesCount);
			addVariable(tempVar);

			sprintf(buffer, "mov ax, %s", $1);
			addCode(buffer);
			sprintf(buffer, "add ax, %s", $3);
			addCode(buffer);
			sprintf(buffer, "mov word %s, ax", tempVar);
			addCode(buffer);

			strcpy($$, tempVar);
		} else {
			sprintf(buffer, "mov ax, %s", $1);
                        addCode(buffer);
                        sprintf(buffer, "add ax, %s", $3);
                        addCode(buffer);
                        sprintf(buffer, "mov %s, ax", $1);
                        addCode(buffer);
		}

	}
	| term MINUS expression {
		printf("expression: %s - %s\n", $1, $3);

		// check if $1 is a variable
		int isVariable = 0;
		int i;
		for (i = 0; i < variablesCount; i++) {
			if (strcmp(variablesNames[i], $1) == 0) {
				isVariable = 1;
			}
		}

		if(!isVariable) {
			sprintf(tempVar, "temp_%d", variablesCount);
			addVariable(tempVar);

			sprintf(buffer, "mov ax, %s", $1);
			addCode(buffer);
			sprintf(buffer, "sub ax, %s", $3);
			addCode(buffer);
			sprintf(buffer, "mov word %s, ax", tempVar);
			addCode(buffer);

			strcpy($$, tempVar);
		} else {
			sprintf(buffer, "mov ax, %s", $1);
                        addCode(buffer);
                        sprintf(buffer, "sub ax, %s", $3);
                        addCode(buffer);
                        sprintf(buffer, "mov %s, ax", $1);
                        addCode(buffer);
		}
	}
	| term TIMES expression {
		printf("expression: %s * %s\n", $1, $3);

		// check if $1 is a variable
		int isVariable = 0;
		int i;
		for (i = 0; i < variablesCount; i++) {
			if (strcmp(variablesNames[i], $1) == 0) {
				isVariable = 1;
			}
		}

		if(!isVariable) {
			sprintf(tempVar, "temp_%d", variablesCount);
			addVariable(tempVar);

			sprintf(buffer, "mov ax, %s", $1);
			addCode(buffer);
			sprintf(buffer, "mov bx, %s", $3);
			addCode(buffer);
			sprintf(buffer, "mul bx");
			addCode(buffer);
			sprintf(buffer, "mov word %s, ax", tempVar);

			strcpy($$, tempVar);
		} else {
			sprintf(buffer, "mov ax, %s", $1);
                        addCode(buffer);
                        sprintf(buffer, "mov bx, %s", $3);
                        addCode(buffer);
                        sprintf(buffer, "mul bx");
                        addCode(buffer);
                        sprintf(buffer, "mov %s, ax", $1);
		}
	}
	| term DIVIDE expression {
		printf("expression: %s / %s\n", $1, $3);

		// check if $1 is a variable
		int isVariable = 0;
		int i;
		for (i = 0; i < variablesCount; i++) {
			if (strcmp(variablesNames[i], $1) == 0) {
				isVariable = 1;
			}
		}

		if(!isVariable) {
			sprintf(tempVar, "temp_%d", variablesCount);
			addVariable(tempVar);

			sprintf(buffer, "mov ax, %s", $1);
			addCode(buffer);
			sprintf(buffer, "mov bx, %s", $3);
			addCode(buffer);
			sprintf(buffer, "div bx");
			addCode(buffer);
			sprintf(buffer, "mov word [%s], ax", tempVar);

			strcpy($$, tempVar);
		} else {
			sprintf(buffer, "mov ax, %s", $1);
                        addCode(buffer);
                        sprintf(buffer, "mov bx, %s", $3);
                        addCode(buffer);
                        sprintf(buffer, "div bx");
                        addCode(buffer);
                        sprintf(buffer, "mov %s, ax", $1);
		}
	}
	| term;
term: ID {
		printf("term id %s\n", $1);
		strcpy($$, $1);
	}
	| CONST {
		printf("term const %s\n", $1);
		strcpy($$, $1);
	};

%%

void addVariable(char *variableName) {

	for (int i = 0; i < variablesCount; i++) {
		if (strcmp(variablesNames[i], variableName) == 0) {
			printf("Variable %s already exists\n", variableName);
			return;
		}
	}

	sprintf(buffer, "%s dw 0\n", variableName);
	strcat(dataSegment, buffer);

	// add variable name to variablesNames
	strcpy(variablesNames[variablesCount], variableName);
	variablesCount++;
}

void addCode(char *code) {
	strcat(code, "\n");
    	strcat(codeSegment, code);
    	codeSegmentIndex += strlen(code);
}

void writeAsmFile(char *fileName) {
	char asmFileName[100];
	sprintf(asmFileName, "%s.asm", fileName);
	FILE *asmFile = fopen(asmFileName, "w");

	fprintf(asmFile, "bits 64\ndefault rel\nglobal main\nextern exit\nextern printf\nextern scanf\n");
        fprintf(asmFile, "\nsegment data use32 class=data\n");
        fprintf(asmFile, "mesaj db \'%%d\', 10, 0\nformat db \'%%d\',0\n");
        fprintf(asmFile, "%s", dataSegment);
        fprintf(asmFile, "\nsegment code use32 class=code\nmain:\nmov eax, 0\n");
        fprintf(asmFile, "%s", codeSegment);
        fprintf(asmFile, "push dword 0\ncall exit\n");

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