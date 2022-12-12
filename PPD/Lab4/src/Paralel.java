import java.io.*;
import java.util.*;

public class Paralel {
    static class MyThread extends Thread {

        private Queue<Monomial> monomialsQueue;
        private MonomialsList monomialsList;
        private int totalMonomials;
        private int addedMonomials;
        private boolean readingFinished;

        MyThread(Queue<Monomial> monomialsQueue, MonomialsList monomialsList, int totalMonomials, int addedMonomials, boolean readingFinished) {
            this.monomialsQueue = monomialsQueue;
            this.monomialsList = monomialsList;
            this.totalMonomials = totalMonomials;
            this.addedMonomials = addedMonomials;
            this.readingFinished = readingFinished;
        }

        private synchronized Monomial getNextMonomial() {
            return monomialsQueue.poll();
        }

        private synchronized void addMonomial(Monomial monomial) {
            monomialsList.add(monomial);
        }

        @Override
        public void run() {
            System.out.println("running...");
            while (true) {
                Monomial monomial = getNextMonomial();
                if (monomial == null) {
                    break;
                }
                addMonomial(monomial);
            }

        }
    }

    public static void main(String[] args) throws IOException {
        String folderPath = "data/caz1";
        int numberOfThreads = 4;

        long startTime = System.nanoTime();
        MonomialsList monomialsList = new MonomialsList();
        Queue<Monomial> monomialsQueue = new LinkedList<>();

        int totalMonomials = 0;
        int addedMonomials = 0;
        boolean readingFinished = false;

        Thread[] threads = new Thread[numberOfThreads];
        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new MyThread(monomialsQueue, monomialsList, totalMonomials, addedMonomials, readingFinished);
            threads[i].start();
        }

        File folder = new File(folderPath);

        for (File file : Objects.requireNonNull(folder.listFiles())) {
            FileReader fileReader = new FileReader(file);
            Scanner scanner = new Scanner(fileReader);

            while (scanner.hasNext()) {
                int coefficient = scanner.nextInt();
                int exponent = scanner.nextInt();

                totalMonomials++;
                monomialsQueue.add(new Monomial(coefficient, exponent));
            }
        }

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
