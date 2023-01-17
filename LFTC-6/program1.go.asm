bits 32
global start
extern exit
import exit msvcrt.dll
extern printf
import printf msvcrt.dll
extern scanf
import scanf msvcrt.dll

segment data use32 class=data
mesaj db '%d', 10, 0
format db '%d',0
a dw 0

segment code use32 class=code
start:
mov eax, 0
push dword 0
call [exit]
