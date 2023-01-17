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
temp_3 dw 0
temp_4 dw 0
temp_5 dw 0

segment code use32 class=code
main:
mov eax, 0
mov word a, 3
mov word b, 4
push qword c
push qword format
call scanf
mov ax, 5
add ax, 6
mov word temp_3, ax
mov ax, b
add ax, temp_3
mov b, ax
mov ax, a
add ax, b
mov a, ax
mov word c, a
mov ax, 9
mov bx, a
div bx
mov ax, 5
mov bx, temp_4
mul bx
mov word b, temp_5
push qword a 
push qword mesaj
call printf
push qword b 
push qword mesaj
call printf
push dword 0
call exit
