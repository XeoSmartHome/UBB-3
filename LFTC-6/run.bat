nasm -g -f win64 program1.go.asm -l program1.lst -o program1.obj
gcc -g -m64 program1.obj -o program1.exe -lmsvcrt -lkernel32
