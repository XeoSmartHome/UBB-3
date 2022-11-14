import java.io.FileWriter;
import java.io.IOException;

public class MatrixGenerator {
    private static final String resourceFolder = "data";

    public static void main(String[] args) {
        int k = 5;//Integer.parseInt(args[0]);
        int n = 10;//Integer.parseInt(args[1]);
        int m = 10000;//Integer.parseInt(args[2]);

        double kernel[][] = new double[k][k];
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < k; j++) {
                kernel[i][j] = Math.random();
            }
        }

        double[][] matrix = new double[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                matrix[i][j] = Math.random();
            }
        }

        try (FileWriter fileWriter = new FileWriter(String.format("%s\\input_%d_%d_%d.txt", resourceFolder, k, n, m))) {
            fileWriter.write(String.format("%d %d %d\n", k, n, m));
            for (int i = 0; i < k; i++) {
                for (int j = 0; j < k; j++) {
                    fileWriter.write(String.format("%f ", kernel[i][j]));
                }
                fileWriter.write("\n");
            }

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    fileWriter.write(matrix[i][j] + " ");
                }
                fileWriter.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
