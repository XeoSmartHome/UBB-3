import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class SequentialMain {
    private static final String resourceFolder = "data";

    private static double[][] readMatrix(String filename) throws FileNotFoundException {
        FileReader fileReader = new FileReader(String.format("%s\\%s", resourceFolder, filename));
        Scanner scanner = new Scanner(fileReader);
        int n = scanner.nextInt();
        int m = scanner.nextInt();
        double[][] matrix = new double[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                matrix[i][j] = scanner.nextDouble();
            }
        }
        return matrix;
    }

    public static void main(String[] args) {
        try {
            double[][] matrix = readMatrix("input_10_10.txt");
            for (double[] doubles : matrix) {
                for (double aDouble : doubles) {
                    System.out.print(aDouble + " ");
                }
                System.out.println();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}