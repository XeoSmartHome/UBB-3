#include <mpi.h>
#include <random>
#include <iostream>
#include <string>
#include <fstream>
#include <chrono>
using namespace std;

const int MAX = 1000000;

int num1[MAX], num2[MAX], rez[MAX];
int n1, n2, nMax, noProcesses, nAfterProcessing;
string stringNum1, stringNum2;

void readAndStoreNumbers(char** argv) {
    // read n1 and n2 from argv[1] and argv[2]
    ifstream fin1(argv[1]);
    ifstream fin2(argv[2]);
    fin1 >> n1;
    fin1.get();
    fin2 >> n2;
    fin2.get();
    fin1 >> stringNum1;
    fin2 >> stringNum2;
    fin1.close();
    fin2.close();
    // save n1 and n2 in num1 and num2
    nMax = std::max(n1, n2);

    // now we have to make sure that nMax is divisible by noProcesses
    // if not, add 0s to the end of num1 and num2 (again, global variables, no need to do it explicitly)
    if (nMax % noProcesses != 0) {
        int noZeros = noProcesses - (nMax % noProcesses);
        nAfterProcessing = nMax + noZeros;
    }

    // we save the numbers reversed
    for (int i = 0; i < n1; i++) {
        // conversion char to int
        num1[i] = stringNum1[i] - '0';
    }
    for (int i = 0, j = n1 - 1; i < n1; i++, j--) {
        // conversion char to int
        num2[i] = stringNum2[i] - '0';
    }

// these are global variables, by default they are initialized with 0, this commented code is just a POC
//    // if n1 < n2, add 0s to the end of num1
//    for (int i = n1; i < nMax; i++) {
//        num1[i] = 0;
//    }
//
//    // if n2 < n1, add 0s to the end of num2
//    for (int i = n2; i < nMax; i++) {
//        num2[i] = 0;
//    }

}

int main(int argc, char** argv) {
    //argumentele din linia de comanda (incepand de la 1): fisier de input pentru numar 1, fisier de input pt numar 2

    MPI_Status status;
    int carry = 0, rank;
    auto startTime = std::chrono::high_resolution_clock::now();
    // Initialize the MPI environment
    MPI_Init(&argc, &argv);
    // Get the rank of the process
    MPI_Comm_size(MPI_COMM_WORLD, &noProcesses);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);

    // se citesc datele numerelor
    if (rank == 0) {
        startTime = chrono::high_resolution_clock::now();
        readAndStoreNumbers(argv);
//        for ()
//        MPI_Send(&nMax, 1, MPI_INT, 1, 0, MPI_COMM_WORLD);
//    } else {
        // the rest of the processes need to wait until the first one is reading for them lazy processes
//        MPI_Recv(&nMax, 1, MPI_INT, 0, 0, MPI_COMM_WORLD, &status);
    }

    MPI_Bcast(&nAfterProcessing, 1, MPI_INT, 0, MPI_COMM_WORLD);

    // declare arrays to receive the data in
    int maxRecvSize = nAfterProcessing / noProcesses;

    auto * aux1  = new int[maxRecvSize];
    auto * aux2  = new int[maxRecvSize];
    auto * auxRez  = new int[maxRecvSize];

    // all processes take care of a part of the numbers
    MPI_Scatter(num1, maxRecvSize, MPI_INT, aux1, maxRecvSize, MPI_INT, 0, MPI_COMM_WORLD);
    MPI_Scatter(num2, maxRecvSize, MPI_INT, aux2, maxRecvSize, MPI_INT, 0, MPI_COMM_WORLD);

    int c = 0;
    //Calcularea sumei numerelor
    for (int i = 0; i < maxRecvSize;i++) {
        auxRez[i] = (aux1[i] + aux2[i] + c) % 10;
        c = (aux1[i] + aux2[i] + c) / 10;
    }

    carry = c;

    // procesul 0 nu  primeste carry in etapa asta, doar mai tarziu
    if (rank >= 1) {
        //primirea carry-ului de la procesul anterior
        MPI_Recv(&carry, 1, MPI_INT, rank - 1, 0, MPI_COMM_WORLD, &status);
        if (carry != 0) {
            int c1 = carry;
            for (int i = 0; i < maxRecvSize; i++) {
                int newSum = auxRez[i] + c1;
                auxRez[i] = newSum % 10;
                c1 = newSum / 10;
            }
            carry = c1 + c;
        }
        else {
            carry = c;
        }
    }

    if (rank != noProcesses - 1) {
        // fiecare proces, cu exceptia ultimului, trimite carry-ul catre procesul urmator
        MPI_Send(&carry, 1, MPI_INT, rank + 1, 0, MPI_COMM_WORLD);
    }
    else {
        // ultimul proces trimite carry-ul catre procesul 0
        MPI_Send(&carry, 1, MPI_INT, 0, 0, MPI_COMM_WORLD);
    }

    // gatheruim tot ce se poate ca e aproape gata treaba
    MPI_Gather(auxRez, maxRecvSize, MPI_INT, rez, maxRecvSize, MPI_INT, 0, MPI_COMM_WORLD);

    if (rank == 0) {
        // time to do the magic

        //se primeste carry-ul de la ultimul proces (doar in acest moment procesul 0 il primeste)
        MPI_Recv(&carry, 1, MPI_INT, noProcesses - 1, 0, MPI_COMM_WORLD, &status);
        //se scrie suma in fisierul de output
        ofstream fout("/home/lorena/Desktop/facultate/an3/PPD/home/lab3/rez2");

        // folosim nMax pentru ca rez nu contine si 0-urile adaugate la final
        if (rez[nMax] != 0) {
            fout << rez[nMax];
        }
        if (carry != 0) {
            fout << carry;
        }
        for (int i = nMax - 1; i >= 0; i--) {
            fout << rez[i];
        }
        fout.close();

        auto endTime = chrono::high_resolution_clock::now();

        //se afisaza durata obtinerii numarului
        cout << chrono::duration<long, std::nano>(endTime - startTime).count();
    }

    delete[] aux1;
    delete[] aux2;
    delete[] auxRez;
    // Finalize the MPI environment.
    MPI_Finalize();
}