import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MatrixGenerator {
    private static final String resourceFolder = "data";

    public static void main(String[] args) {
        int n = 10;
        int m = 10;
        double[][] matrix = new double[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                matrix[i][j] = Math.random();
            }
        }
        try (FileWriter fileWriter = new FileWriter(String.format("%s\\input_%d_%d.txt", resourceFolder, n, m))) {
            fileWriter.write(n + " " + m + "\n");
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
