#include <iostream>
#include <fstream>

using namespace std;

#define MAX 100000

int num1[MAX], num2[MAX], rez[MAX + 1];

int n1, n2, nMax;

//Calcularea sumei numerelor
void sum() {
    int carry = 0;

    for (int i = 0; i < nMax; i++) {
        rez[i] = (num1[i] + num2[i] + carry) % 10;
        carry = (num1[i] + num2[i] + carry) / 10;
    }

    // daca la fina mai exista carry, se adauga in rez
    if (carry > 0) {
        rez[nMax] = carry;
        nMax++;
    }
}

int main(int argc, char* argv[]) {
    auto startTime = chrono::high_resolution_clock::now();

    // argumentele din linia de comanda (incepand de la 1): fisier de input pt numar1, fisier de input pt numar2
    string no1, no2;
    ifstream fin1(argv[1]);
    ifstream fin2(argv[2]);
    //se citesc datele numerelor
    fin1 >> n1;
    fin1.get();
    fin2 >> n2;
    fin2.get();
    fin1 >> no1;
    fin2 >> no2;
    fin1.close();
    fin2.close();

    nMax = std::max(n1, n2);

    for (int i = 0; i < n1; i++) {
        // conversie char to int
        num1[i] = no1[i] - '0';
    }
    for (int i = 0; i < n2; i++) {
        // conversie char to int
        num2[i] = no2[i] - '0';
    }
//    std::cout <<argv[2] << std::endl;

    //se obtine suma celor 2 numere
    sum();

    //se scrie suma in fisierul de output
    ofstream fout("/home/lorena/Desktop/facultate/an3/PPD/home/lab3/sec");
    for (int i = nMax - 1; i >= 0; i--) {
        fout << rez[i];
    }
    fout.close();

    auto endTime = chrono::high_resolution_clock::now();

    //se afisaza durata obtinerii numarului
    cout << chrono::duration<long, nano>(endTime - startTime).count();
    return 0;
}