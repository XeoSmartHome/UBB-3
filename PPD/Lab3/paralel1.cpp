#include <mpi.h>
#include <random>
#include <iostream>
#include <string>
#include <fstream>
#include <chrono>
#include <algorithm>

const int MAX = 100000;

int num1[MAX], num2[MAX], rez[MAX];
int n1, n2, nMax, carry, noProcesses;

void master(char **argv) {
    MPI_Status status;
    // only the master process is writing to the standard output
    auto startTime = std::chrono::high_resolution_clock::now();

    std::ifstream fin1(argv[1]);
    std::ifstream fin2(argv[2]);
    //se citesc datele numerelor
    fin1 >> n1;
    fin2 >> n2;
    fin1.get();
    fin2.get();

    nMax = std::max(n1, n2);

    // first process is not a worker, so we split the work between the rest of the processes
    int digitsPerProcess = nMax / (noProcesses - 1);
    int remainingDigits = nMax % (noProcesses - 1);

    int start = 0, end;
    int k1 = 0, k2 = 0;

    //declare some aux for reading
    int aux1[digitsPerProcess + 1];
    int aux2[digitsPerProcess + 1];

    for (int i = 1; i < noProcesses; i++) {
        end = start + digitsPerProcess;
        if (remainingDigits > 0) {
            remainingDigits--;
            end++;
        }
        // citesc cifrele (k/p) pentru procesul actual si le pun intr un vector, apoi le salvez invers in memorie
        char ch;
        for (int j = start; j < end; j++) {
            fin1.get(ch);
            num1[k1++] = ch - '0';
            fin2.get(ch);
            num2[k2++] = ch - '0';
        }

        // se trimite fiecarui proces startul, endul si cifrele pe care le au de procesat
        MPI_Send(num1 + start, end - start, MPI_INT, i, 0, MPI_COMM_WORLD);
        MPI_Send(num2 + start, end - start, MPI_INT, i, 0, MPI_COMM_WORLD);
        start = end;
    }

    // se inchid fisierele dupa ce s-a terminat de citit din ele
    fin1.close();
    fin2.close();

    int amountReceived = 0, statRes;
    for (int i = 1; i < noProcesses; i++) {
        // se asteapta rezultatele de la fiecare proces
        MPI_Recv(rez + amountReceived, MAX, MPI_INT, i, 0, MPI_COMM_WORLD, &status);

        MPI_Get_count(&status, MPI_INT, &statRes);
        amountReceived += statRes;
    }
    //se primeste carry-ul de la ultimul proces
    MPI_Recv(&carry, 1, MPI_INT, noProcesses - 1, 0, MPI_COMM_WORLD, &status);

    //se scrie suma in fisierul de output
    std::ofstream fout("/home/lorena/Desktop/facultate/an3/PPD/home/lab3/rez1");
    if (carry != 0) {
        fout << carry;
    }
    for (int i = nMax - 1; i >= 0; i--) {
        fout << rez[i];
    }
    fout.close();

    auto endTime = std::chrono::high_resolution_clock::now();

    //se afisaza durata obtinerii numarului
    std::cout << std::chrono::duration<long, std::nano>(endTime - startTime).count() << std::endl;

}

void slave(int rank) {
    int start = 0, end, receivedNum1, receivedNum2;
    MPI_Status status, status1, status2;

    // workerii primesc startul, endul si cifrele pe care le au de procesat
    MPI_Recv(num1, MAX, MPI_INT, 0, 0, MPI_COMM_WORLD, &status1);
    MPI_Get_count(&status1, MPI_INT, &receivedNum1);
    MPI_Recv(num2, MAX, MPI_INT, 0, 0, MPI_COMM_WORLD, &status2);
    MPI_Get_count(&status2, MPI_INT, &receivedNum2);

    int maxRecv = std::max(receivedNum1, receivedNum2);

    int c = 0;

    // fiecare worker calculeaza suma si modifica carry ul
    for (int i = 0; i < maxRecv; i++) {
        rez[i] = (num1[i] + num2[i] + c) % 10;
        c = (num1[i] + num2[i] + c) / 10;
    }
    carry = c;

    // primirea carry-ului de la procesul anterior
    if (rank != 1) {
        MPI_Recv(&carry, 1, MPI_INT, rank - 1, 0, MPI_COMM_WORLD, &status);

        // daca carry ul e diferit de 0, se actualizeaza suma calculata
        if (carry != 0) {
            int c1 = carry;
            for (int i = 0; i < maxRecv; i++) {
                int newSum = rez[i] + c1;
                rez[i] = newSum % 10;
                c1 = newSum / 10;
            }
            carry = c1 + c;
        } else {
            // carry trebuie actualizat pt ca a fost suprascris de recv
            carry = c;
        }
    }

    // trimitera rezultatului la procesul master
    MPI_Send(rez, maxRecv, MPI_INT, 0, 0, MPI_COMM_WORLD);

    //trimiterea carry-ului la procesul urmator
    if (rank != noProcesses - 1) {
        MPI_Send(&carry, 1, MPI_INT, rank + 1, 0, MPI_COMM_WORLD);
    } else {
        MPI_Send(&carry, 1, MPI_INT, 0, 0, MPI_COMM_WORLD);
    }
}

int main(int argc, char **argv) {
    // argumentele din linia de comanda (incepand de la 1): fisier de input pentru numarul 1, fisier de input pentru numarul 2

    int rank;
    MPI_Status status;
    // Initialize the MPI environment
    MPI_Init(&argc, &argv);

    MPI_Comm_size(MPI_COMM_WORLD, &noProcesses);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);


    if (rank == 0) {
        master(argv);
    } else {
        slave(rank);
    }
    // Finalize the MPI environment.
    MPI_Finalize();
}