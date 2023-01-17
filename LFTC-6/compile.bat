bison -d bison.y

flex .\lexical_analyzer.l

gcc bison.tab.c lex.yy.c -o compiler.exe

compiler.exe program1.go