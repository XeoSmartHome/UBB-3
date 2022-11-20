import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import java.util.concurrent.CyclicBarrier;

import static java.lang.System.nanoTime;

public class Main {
    private static final String resourceFolder = "data";
    private static int k;
    private static int n;
    private static int m;
    private static double[][] kernel;
    private static double[][] matrix;
    private static int threadCount;

    public static void main(String[] args) {
        String fileName = args[0];
        threadCount = Integer.parseInt(args[1]);

        try {
            readInputMatrix(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        long start = nanoTime();
        parallelConvolution();
        long end = nanoTime();
        double executionTime = (end - start) / 1e6;

//        printMatrix(matrix);
        if (!checkOutputMatrix(fileName)) {
            System.out.println("Wrong result");
            return;
        }

        System.out.println("Execution time: " + executionTime + "ms");
        System.out.println(executionTime);
    }

    private static void parallelConvolution() {
        int rowsPerThread = n / threadCount;
        int remainingRows = n % threadCount;

        Thread[] threads = new Thread[threadCount];
        CyclicBarrier barrier = new CyclicBarrier(threadCount);

        for (int i = 0; i < threadCount; i++) {
            int startRow = i * rowsPerThread;
            int endRow = startRow + rowsPerThread;
            if (i == threadCount - 1) {
                endRow += remainingRows;
            }
            threads[i] = new ConvolutionThread(i, barrier, matrix, kernel, n, m, k, startRow, endRow);
            threads[i].start();
        }

        for (int i = 0; i < threadCount; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected static void readInputMatrix(String filename) throws FileNotFoundException {
        FileReader fileReader = new FileReader(String.format("%s\\input\\%s", resourceFolder, filename));
        Scanner scanner = new Scanner(fileReader);
        k = scanner.nextInt();
        n = scanner.nextInt();
        m = scanner.nextInt();

        kernel = new double[k][k];
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < k; j++) {
                kernel[i][j] = scanner.nextDouble();
            }
        }

        matrix = new double[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                matrix[i][j] = scanner.nextDouble();
            }
        }
    }

    private static boolean checkOutputMatrix(String filename) {
        try {
            FileReader fileReader = new FileReader(String.format("%s\\output\\%s", resourceFolder, filename.replace(".txt", "_result.txt")));
            Scanner scanner = new Scanner(fileReader);
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    if (Math.abs(scanner.nextDouble() - matrix[i][j]) > 1e-6) {
                        return false;
                    }
                }
            }
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void printMatrix(double[][] matrix) {
        for (double[] row : matrix) {
            for (double element : row) {
                System.out.print(element + " ");
            }
            System.out.println();
        }
    }

}