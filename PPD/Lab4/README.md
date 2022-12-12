## Laborator 4

### Neamtu Claudiu 235
### 12/12/2022

Obiectiv:

· Intelegerea/aprofundarea sablonului “producator-consumator”

· Intelegerea/aprofundarea sincronizarii conditionale

· Intelegerea/aprofundarea excluderii mutuale (granularitatea sectiunilor critice)

Se considera n polinoame reprezentate prin lista de monoame.

Se cere adunarea polinoamelor folosind o implementare multithreading (p threaduri).

Consideratii generale:

- reprezentarea unui polinom in memorie: lista inlantuita (1 nod=1monom) ordonata dupa exponentii monoamelor cu urmatorul INVARIANT (predicat adevarat la orice moment al executiei) de reprezentare:

-monoamele sunt ordonate dupa exponenti

-nu se pasteaza in lista monoame cu coeficient 0;

- nu exista doua noduri (monoame) cu acelasi exponent

- polinoamele se citesc din fisiere – cate un fisier pentru fiecare polinom;

- un fisier contine informatii de tip (coeficient, exponent) pentru fiecare monom al

unui polinom,

- fisierele input se creeaza prin generare de numere aleatoare.

(Conditie: fisierele nu contin monoame cu coeficient egal cu 0 dar nu sunt ordonate dupa exponent!)

Rezolvare:

Se porneste prin crearea unei liste inlantuita - L corespunzatoare unui polinom nul. In final aceasta lista va continue polinomul rezultat.

Metoda A) Implementare secventiala

· Se citeste pe rand din fiecare fisier cate un monom si se adauga in lista rezultat -L (atentie – invariantul trebuie sa ramana adevarat dupa fiecare adaugare de monom).

Metoda B) Implementare paralela – p threaduri

1. Primul thread citeste cate un monom si il adauga intr-o structura de date de tip coada.

(conditie – pentru structura de tip coada NU se admite folosirea unei structuri de date pentru care partea de sincronizare este deja implementata!!!)

2. Celelalte threaduri preiau cate un monom din coada si il aduna la polinomul reprezentat in lista L.

è Se continua operatiile 1., 2. pana cand toate monoamele, din toate fisierele, sunt adunate la lista L.

3. Primul thread scrie rezultatul obtinut in lista L intr-un fisier rezultat

(conditie: fisierul nu contine monoame cu coefficient egal cu 0)

Productor-consumator

Sincronizare la nivel de lista!!!

Limbaj: la alegere intre Java si C++

Analiza timpului de executie pentru urmatoarele cazuri:

1) 10 polinoame fiecare cu gradul maxim 1000 si cu maxim 50 monoame

a. p = 4, 6, 8

b. secvential

2) 5 polinoame fiecare cu gradul maxim 10000 si cu maxim 100 monoame

a. p = 4, 6, 8

b. secvential

Analiza: raport Tsecvential/Tparalel

### Implementare

#### Varianta seriala

#### Varianta secventiala

### Performanta