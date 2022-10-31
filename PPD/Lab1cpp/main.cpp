#include <iostream>
#include <fstream>
#include <string>
#include <thread>
#include <chrono>
#include <regex>

#define MAX_MATRIX_SIZE 1000
#define MAX_KERNEL_SIZE 5
#define MAX_THREADS 16

using namespace std;

int n, m, k;
double matrix[MAX_MATRIX_SIZE][MAX_MATRIX_SIZE];
double result[MAX_MATRIX_SIZE][MAX_MATRIX_SIZE];
double kernel[MAX_KERNEL_SIZE][MAX_KERNEL_SIZE];
thread threads[MAX_THREADS];

string resourceFolder = "../data";

void readMatrix(const string &filename) {
    cout << "Reading matrix from " << filename << endl;
    ifstream inputFile(resourceFolder + "/" + filename);
    inputFile >> k >> n >> m;
    cout << k << " " << n << " " << m << endl;
    for (int i = 0; i < k; i++) {
        for (int j = 0; j < k; j++) {
            inputFile >> kernel[i][j];
        }
    }
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
            inputFile >> matrix[i][j];
        }
    }
    inputFile.close();
}

bool checkResult(const string &filename) {
    string path = resourceFolder + "/" + filename;
    cout << "Checking result" << endl;
    ifstream inputFile(regex_replace(path, regex(".txt"), "_result.txt"));
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
            double expected;
            inputFile >> expected;
            if (result[i][j] - expected > 1e-6) {
                cout << "Result is incorrect" << endl;
                return false;
            }
        }
    }
    cout << "Result is correct" << endl;
    return true;
}

void myThread(int start, int stop) {
    for (int i = start; i < stop; i++) {
        for (int j = 0; j < m; j++) {
            double sum = 0;
            for (int ki = 0; ki < k; ki++) {
                for (int kj = 0; kj < k; kj++) {
                    int mi = min(max(i + ki - k / 2, 0), n - 1);
                    int mj = min(max(j + kj - k / 2, 0), m - 1);
                    sum += matrix[mi][mj] * kernel[ki][kj];
                }
            }
            result[i][j] = sum;
        }
    }
}

int main(int argc, const char *argv[]) {
    readMatrix(argv[1]);
    int threadsCount = atoi(argv[2]);

    auto startTime = chrono::high_resolution_clock::now();
    for(int i = 0; i < threadsCount; i++) {
        threads[i] = thread(myThread, i * n / threadsCount, (i + 1) * n / threadsCount);
    }
    for(int i = 0; i < threadsCount; i++) {
        threads[i].join();
    }
    auto endTime = chrono::high_resolution_clock::now();
    double executionTime = chrono::duration<double, milli>(endTime - startTime).count();

    checkResult(argv[1]);

    cout << "Execution time: " << executionTime << " ms" << endl;

    cout << executionTime << endl;
    return 0;
}
