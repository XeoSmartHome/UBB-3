import java.io.*;
import java.util.Scanner;

public class SequentialMain extends GenericMain {

    private static void writeResult(String filename) throws IOException {
        FileWriter fileWriter = new FileWriter(String.format("%s\\%s", resourceFolder, filename.replace(".txt", "_result.txt")));
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                fileWriter.write(String.format("%f ", result[i][j]));
            }
            fileWriter.write("\n");
        }
        fileWriter.close();
    }

    public static void main(String[] args) {
        String fileName = args[0];
        try {
            readMatrix(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        result = new double[n][m];

        //
        long startTime = System.nanoTime();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                double sum = 0;
                for (int ki = 0; ki < k; ki++) {
                    for (int kj = 0; kj < k; kj++) {
                        int mi = Math.min(Math.max(i + ki - k / 2, 0), n - 1);
                        int mj = Math.min(Math.max(j + kj - k / 2, 0), m - 1);
                        sum += matrix[mi][mj] * kernel[ki][kj];
                    }
                }
                result[i][j] = sum;
            }
        }
        long endTime = System.nanoTime();
        double executionTime = (endTime - startTime) / 1e6;
        System.out.println("Sequential time: " + executionTime + " ms");

        try {
            writeResult(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(executionTime);
    }
}