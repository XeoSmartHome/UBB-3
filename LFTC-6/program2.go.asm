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
push qword a
push qword format
call scanf
push qword b
push qword format
call scanf
mov ax, a
add ax, b
mov a, ax
mov word c, a
push qword c 
push qword mesaj
call printf
push dword 0
call exit
