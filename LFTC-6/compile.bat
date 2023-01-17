bison -d bison.y
flex .\lexical_analyzer.l
gcc -o bison.tab.c lex.yy.c -o compiler.exe
