bits 64
default rel
global main
extern exit
extern printf
extern scanf

segment data use32 class=data
mesaj db '%d', 10, 0
format db '%d',0
a dw 0
b dw 0
c dw 0

segment code use32 class=code
main:
mov eax, 0
mov word a, 3
mov word b, 4
push dword 0
call exit
