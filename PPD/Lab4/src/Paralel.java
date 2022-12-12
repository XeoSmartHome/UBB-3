import java.io.*;
import java.util.*;

public class Paralel {

    public static void main(String[] args) throws IOException {
        String folderPath = "data/caz2";
        int numberOfThreads = 2;

        long startTime = System.nanoTime();
        MonomialsList monomialsList = new MonomialsList();
        Queue<Monomial> monomialsQueue = new LinkedList<>();

        final int[] readingFinished = {0};

        Thread[] threads = new Thread[numberOfThreads];
        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        Monomial monomial = null;
                        synchronized (monomialsQueue) {
                            if (!monomialsQueue.isEmpty()) {
                                monomial = monomialsQueue.poll();
                            }
                        }
                        if (monomial != null) {
                            synchronized (monomialsList) {
                                monomialsList.add(monomial);
                            }
                        }
                        if (readingFinished[0] == 1 && monomialsQueue.isEmpty()) {
                            break;
                        }
                    }
                }
            });
            threads[i].start();
        }

        File folder = new File(folderPath);

        for (File file : Objects.requireNonNull(folder.listFiles())) {
            FileReader fileReader = new FileReader(file);
            Scanner scanner = new Scanner(fileReader);

            while (scanner.hasNext()) {
                int coefficient = scanner.nextInt();
                int exponent = scanner.nextInt();

                synchronized (monomialsQueue) {
                    monomialsQueue.add(new Monomial(coefficient, exponent));
                }
            }
        }
        readingFinished[0] = 1;

        for (int i = 0; i < numberOfThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        FileWriter fileWriter = new FileWriter("output/paralel_" + folderPath.replace("data/", "") + ".txt");
        for (Monomial monomial : monomialsList.getAsList()) {
            fileWriter.write(monomial.coefficient + " " + monomial.exponent + "\n");
        }
        fileWriter.close();
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        System.out.println(duration);
    }
}
