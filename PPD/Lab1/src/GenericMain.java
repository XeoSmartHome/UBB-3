import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class GenericMain {
    protected static final String resourceFolder = "data";
    protected static int k;
    protected static int n;
    protected static int m;
    protected static double[][] kernel;
    protected static double[][] matrix;
    protected static double[][] result;

    protected static void readMatrix(String filename) throws FileNotFoundException {
        FileReader fileReader = new FileReader(String.format("%s\\%s", resourceFolder, filename));
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
}
