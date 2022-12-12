import java.io.*;
import java.util.Objects;
import java.util.Scanner;

public class Secvential {
    public static void main(String[] args) throws IOException {
        String folderPath = "data/caz1";

        long startTime = System.nanoTime();
        MonomialsList monomialsList = new MonomialsList();
        File folder = new File(folderPath);

        for (File file : Objects.requireNonNull(folder.listFiles())) {
            FileReader fileReader = new FileReader(file);
            Scanner scanner = new Scanner(fileReader);

            while (scanner.hasNext()) {
                int coefficient = scanner.nextInt();
                int exponent = scanner.nextInt();
                monomialsList.add(new Monomial(coefficient, exponent));
            }
        }

        FileWriter fileWriter = new FileWriter("output/" + folderPath.replace("data/", "") + ".txt");
        for (Monomial monomial : monomialsList.getAsList()) {
            fileWriter.write(monomial.coefficient + " " + monomial.exponent + "\n");
        }
        fileWriter.close();
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        System.out.println(duration);
    }
}
