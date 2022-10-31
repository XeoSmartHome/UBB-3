public class MyThread extends Thread {
    private final int threadId;
    private final double[][] matrix;
    private final double[][] kernel;
    private final double[][] result;
    private final int n;
    private final int m;
    private final int k;
    private final int start;
    private final int end;

    MyThread(int threadId, double[][] matrix, double[][] kernel, double[][] result, int n, int m, int k, int start, int end) {
        this.threadId = threadId;
        this.matrix = matrix;
        this.kernel = kernel;
        this.result = result;
        this.n = n;
        this.m = m;
        this.k = k;
        this.start = start;
        this.end = end;
    }

    public void run() {
        for (int i = start; i < end; i++) {
            for (int j = 0; j < m; j++) {
                double sum = 0;
                for (int ki = 0; ki < k; ki++) {
                    for (int kj = 0; kj < k; kj++) {
                        int mi = Math.min(Math.max(i + ki - k / 2, 0), n - 1);
                        int mj = Math.min(Math.max(j + kj - k / 2, 0), m - 1);
                        sum += matrix[mi][mj] * kernel[ki][kj];
                    }
                }
                result[i][j] = sum;
            }
        }
    }
}
