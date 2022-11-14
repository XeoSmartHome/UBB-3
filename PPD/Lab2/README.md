# Laborator 2
## Neamtu Claudiu - 235

---

Se considera problema de la laboratorul 1 cu urmatoarea modificare:

Se cere un program care sa asigure urmatoarea postconditie:

Postconditie: Matricea initiala contine imaginea filtrata.

Constrangere: NU se aloca o matrice rezultat (V) temporara!

Obiectiv: optimizarea complexitatii-spatiu in conditiile obtinerii unei performante ridicate.

Datele de intrare se citesc dintr-un fisier de intrare “date.txt”.

(Fisierul trebuie creat anterior prin adaugare de numere generate aleator.)

Implementare

a) Java

b) C++ ( cel putin C++11 )

Testare: masurati timpul de executie pentru

1) N=M=10 si n=m=3; p=2;

2) N=M=1000 si n=m=5; p=1,2,4,8,16

3) N=10 M=10000 si n=m=5; p=1,2,4,8,16

4) N=10000 M=10 si n=m=5; p=1,2,4,8,16

ObservatII (valabile pentru fiecare caz de testare 1-4):

- Fiecare test trebuie repetat de 10 ori si evaluarea timpul de executie se considera media aritmetica a celor 10 rulari.

- Pentru fiecare varianta de testare (dintre cele 10) folositi acelasi fisier “date.txt”;

- Pentru fiecare varianta de testare (dintre cele 10) verificati corectitudinea prin comparatia rezultatului cu fisierul rezultat prin executia secventiala.

Analiza:

Verificati corectitudinea prin comparatie cu rezultatul de la laboratorul 1.

Comparati performanta pentru fiecare caz !

Comparati timpii obtinuti cu implementarea Java versus implementarea C++.

Evaluati complexitatea-spatiu.

### Varianta utilizata: 1B


### Detalii implementare
- se foloseste un fisier de intrare pentru a citi matricea si kernelul
- se imparte matrice in bucati pe linii
- fiecare bucata este procesata de un thread
- threadurile isi copiaza in primul buffer primele [dimensiuneKernel / 2 + 1] linii din ale bucatii anterioare si prima linie din bucata curenta
- threadurile isi copiaza in al doilea buffer primele [dimensiuneKernel / 2] linii din ale bucatii urmatoare
- se foloseste o bariera pentru a sincroniza threadurile
- se parcurge fiecare linie din bucata curenta si se aplica kernelul
- rezultatul unei linii se scrie in matricea initiala
- datele din beffer sunt shiftate cu o linie
- se copiaza urmatoarea linie din matricea initiala in buffer
- cand ajung la utimele linii din bucata curenta datele de iput pt calcul for fi luate din al doilea buffer si nu din matricea initiala
- main-ul asteapta ca toate threadurile sa se termine
- se verifica corectitudinea rezultatului
- re afiseaza timpul de executie

### Complexitate spatiu:
- pe langa matricea initiala de dimenziune n x m, pentru fiecare thread se aloca 2 bufere de dimensiune [dimendiuneKernel / 2 + 1], respoectiv [dimensiuneKernel / 2] pentru a retine elementele de pe marginea matricei 

`O(nrTheaduri * m * dimensiuneKernel)`


## Grafice

### Matrice 1000x1000 Kernel 5x5
![](docs/Screenshot_0.png)

### Matrice 10000x10 Kernel 5x5
![](docs/Screenshot_1.png)

### Matrice 10x10000 Kernel 5x5
![](docs/Screenshot_2.png)

### Observatii
- Pentru matrici mari timpul de executie scade cu cat adaugam mai multe threaduri
- In cazul Java numarul optim fiind 4 threaduri
- In cazul C++ daca adaugam mai multe theaduri nu imbunatateste timpul de executie, dar nuci nu il creste prea mult
- Pentru matrici mici observam ca adaugarea de theaduri suplimentare creste timpul de executie
- Aceasta crestere este mult mai vizibila in Java decat in C++
- Toate testele au aratat ca C++ este mai rapid decat Java
- 
