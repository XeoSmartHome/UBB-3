import java.io.FileWriter;
import java.io.IOException;

public class PolynomialsGenerator {
    public static void main(String[] args) throws IOException {
        int numberOfPolynomials = 5;
        int maxExponent = 10000;
        int maxCoefficient = 100;
        int numberOfMonomials = 100;
        String outputDirectory = "data/caz2/";

        for (int i = 0; i < numberOfPolynomials; i++) {
            FileWriter outputFile = new FileWriter(outputDirectory + "polynomial" + i + ".txt");

            int numberOfTerms = (int) (Math.random() * numberOfMonomials) + 1;
            int[] coefficients = new int[numberOfTerms];
            int[] exponents = new int[numberOfTerms];

            for (int j = 0; j < numberOfTerms; j++) {
                coefficients[j] = (int) (Math.random() * maxCoefficient) + 1;
                exponents[j] = (int) (Math.random() * maxExponent) + 1;
            }

            for (int j = 0; j < numberOfTerms; j++) {
                outputFile.write(coefficients[j] + " " + exponents[j] + "\n");
            }

            outputFile.close();
        }
    }
}
