import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class ParallelMain extends GenericMain {
    private static boolean checkResult(String filename) {
        try {
            FileReader fileReader = new FileReader(String.format("%s\\%s", resourceFolder, filename.replace(".txt", "_result.txt")));
            Scanner scanner = new Scanner(fileReader);
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    if (Math.abs(scanner.nextDouble() - result[i][j]) > 1e-6) {
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

    public static void main(String[] args) {
        String fileName = args[0];
        int numberOfThreads = Integer.parseInt(args[1]);
        try {
            readMatrix(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        result = new double[n][m];
        ArrayList<Thread> threads = new ArrayList<>();
        for (int threadId = 0; threadId < numberOfThreads; threadId++) {
            int start = threadId * n / numberOfThreads;
            int end = (threadId + 1) * n / numberOfThreads;
            threads.add(new MyThread(threadId, matrix, kernel, result, n, m, k, start, end));
        }

        long startTime = System.nanoTime();
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long endTime = System.nanoTime();
        double executionTime = (endTime - startTime) / 1e6;

        if (checkResult(fileName)) {
            System.out.println("Execution time: " + executionTime / 1e6 + "ms");
        } else {
            System.out.println("Wrong result");
        }

        System.out.println(executionTime);
    }
}
