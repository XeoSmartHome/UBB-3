import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class Secvential {
    public static void main(String[] args) throws FileNotFoundException {
        MonomialsList monomialsList = new MonomialsList();

        FileReader fileReader = new FileReader("data/caz1/polynomial0.txt");
        Scanner scanner = new Scanner(fileReader);

        while (scanner.hasNext()) {
            int coefficient = scanner.nextInt();
            int exponent = scanner.nextInt();
            monomialsList.add(new Monomial(coefficient, exponent));
            monomialsList.print();
        }

    }
}
