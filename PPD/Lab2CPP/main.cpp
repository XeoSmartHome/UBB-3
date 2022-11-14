#include <iostream>
#include <fstream>
#include <string>
#include <thread>
#include <chrono>
#include <regex>
#include <condition_variable>

#define MAX_THREADS 16

using namespace std;

int n, m, k;
int threadsCount;
double **matrix;
double **kernel;
thread threads[MAX_THREADS];

string resourceFolder = "../data";

void readMatrix(const string &filename) {
    ifstream inputFile(resourceFolder + "/input/" + filename);
    inputFile >> k >> n >> m;
    matrix = new double *[n];
    kernel = new double *[k];
    for (int i = 0; i < n; i++) {
        matrix[i] = new double[m];
    }
    kernel = new double *[k];
    for (int i = 0; i < k; i++) {
        kernel[i] = new double[k];
    }
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
    string path = resourceFolder + "/output/" + filename;
    ifstream inputFile(regex_replace(path, regex(".txt"), "_result.txt"));
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
            double expected;
            inputFile >> expected;
            if (matrix[i][j] - expected > 1e+6) {
                return false;
            }
        }
    }
    return true;
}

class MyBarrier {
private:
    std::mutex m;
    std::condition_variable cv;
    int counter;
    int waiting;
    int thread_count;
public:
    explicit MyBarrier(int count) : thread_count(count), counter(0), waiting(0) {}

    void wait() {
        std::unique_lock<std::mutex> lk(m);
        ++counter;
        ++waiting;
        cv.wait(lk, [&] { return counter >= thread_count; });
        cv.notify_one();
        --waiting;
        if (waiting == 0) {
            counter = 0;
        }
        lk.unlock();
    }
};


void ConvolutionThread(MyBarrier & barrier, int start, int stop) {
    int topBufferHeight = k / 2 + 1;
    int bottomBufferHeight = k / 2;
    auto **topBuffer = new double *[topBufferHeight];
    for (int i = 0; i < topBufferHeight; i++) {
        topBuffer[i] = new double[m];
    }
    auto **bottomBuffer = new double *[bottomBufferHeight];
    for (int i = 0; i < bottomBufferHeight; i++) {
        bottomBuffer[i] = new double[m];
    }

    for (int i = 0; i < topBufferHeight; i++) {
        for (int j = 0; j < m; j++) {
            topBuffer[i][j] = matrix[max(start + i - topBufferHeight + 1, 0)][j];
        }
    }

    for (int i = 0; i < bottomBufferHeight; i++) {
        for (int j = 0; j < m; j++) {
            bottomBuffer[i][j] = matrix[min(stop - bottomBufferHeight + i + 1, n - 1)][j];
        }
    }

    barrier.wait();

    for (int i = start; i < stop; i++) {
        for (int j = 0; j < m; j++) {
            double sum = 0;
            for (int ki = 0; ki < k; ki++) {
                for (int kj = 0; kj < k; kj++) {
                    int mi = min(max(i + ki - k / 2, 0), n - 1);
                    int mj = min(max(j + kj - k / 2, 0), m - 1);
                    if (ki < topBufferHeight) {
                        sum += topBuffer[ki][mj] * kernel[ki][kj];
                    } else {
                        if (i < stop - bottomBufferHeight) {
                            sum += matrix[mi][mj] * kernel[ki][kj];
                        } else {
                            sum += bottomBuffer[ki - bottomBufferHeight - 1][mj] * kernel[ki][kj];
                        }
                    }
                }
            }
            matrix[i][j] = sum;
        }
        for (int bi = 0; bi < topBufferHeight - 1; bi++) {
            if (i + bi < stop - bottomBufferHeight) {
                for (int j = 0; j < m; ++j) {
                    topBuffer[bi][j] = matrix[bi + 1][j];
                }
            } else {
                for(int j = 0; j < m; ++j) {
                    bottomBuffer[bi][j] = matrix[stop - bottomBufferHeight + bi][j];
                }
            }
        }
        for (int j = 0; j < m; ++j) {
            topBuffer[topBufferHeight - 1][j] = matrix[min(i + 1, n - 1)][j];
        }

    }

}

void parallelConvolution() {
    MyBarrier barrier(threadsCount);

    int rowsPerThread = n / threadsCount;
    int remainingRows = n % threadsCount;

    for (int i = 0; i < threadsCount; i++) {
        int startRow = i * rowsPerThread;
        int endRow = startRow + rowsPerThread;
        if (i == threadsCount - 1) {
            endRow += remainingRows;
        }
        threads[i] = thread(ConvolutionThread, std::ref(barrier), startRow, endRow);
    }

    for (int i = 0; i < threadsCount; i++) {
        threads[i].join();
    }
}

int main(int argc, const char *argv[]) {
    readMatrix(argv[1]);
    threadsCount = atoi(argv[2]);

    auto startTime = chrono::high_resolution_clock::now();
    parallelConvolution();
    auto endTime = chrono::high_resolution_clock::now();
    double executionTime = chrono::duration<double, milli>(endTime - startTime).count();

    checkResult(argv[1]);
    // print matrix
//    for (int i = 0; i < n; i++) {
//        for (int j = 0; j < m; j++) {
//            cout << matrix[i][j] << " ";
//        }
//        cout << endl;
//    }

    cout << "Execution time: " << executionTime << " ms" << endl;

    cout << executionTime << endl;
    return 0;
}