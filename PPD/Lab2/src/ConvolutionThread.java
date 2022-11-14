import java.util.Arrays;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class ConvolutionThread extends Thread {
    private final int threadId;
    private final double[][] matrix;
    private final double[][] kernel;
    private final int n;
    private final int m;
    private final int k;
    private final int start;
    private final int stop;
    final CyclicBarrier barrier;
    public ConvolutionThread(int threadId, CyclicBarrier barrier, double[][]matrix, double [][]kernel, int n, int m, int k, int start, int stop) {
        this.threadId = threadId;
        this.barrier = barrier;
        this.matrix = matrix;
        this.kernel = kernel;
        this.n = n;
        this.m = m;
        this.k = k;
        this.start = start;
        this.stop = stop;
    }

    @Override
    public void run() {
        int topBufferHeight = k / 2 + 1;
        int bottomBufferHeight = k / 2;
        double[][] topBuffer = new double[topBufferHeight][m];
        double[][] bottomBuffer = new double[bottomBufferHeight][m];

        for (int i = 0; i < topBufferHeight; i++) {
            System.arraycopy(matrix[Math.max(start + i - topBufferHeight + 1, 0)], 0, topBuffer[i], 0, m);
        }

//         copy the bottom rows to the buffer
        for (int i = 0; i < bottomBufferHeight; i++) {
            System.arraycopy(matrix[Math.min(stop - bottomBufferHeight + i + 1, n - 1)], 0, bottomBuffer[i], 0, m);
        }

//        System.out.println("Thread " + threadId  + "before barrier");
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
//        System.out.println("Thread " + threadId  + "after barrier");

        for (int i = start; i < stop; i++) {
            for (int j = 0; j < m; j++) {
                double sum = 0;
                for (int ki = 0; ki < k; ki++) {
                    for (int kj = 0; kj < k; kj++) {
                        int mi = Math.min(Math.max(i + ki - k / 2, 0), n - 1);
                        int mj = Math.min(Math.max(j + kj - k / 2, 0), m - 1);
                        if(ki < topBufferHeight) {
                            sum += topBuffer[ki][mj] * kernel[ki][kj];
                        } else {
                            if (i < stop - bottomBufferHeight) {
                                sum += matrix[mi][mj] * kernel[ki][kj];
                            } else {
                                sum += bottomBuffer[ki - bottomBufferHeight - 1][mj] * kernel[ki][kj];
                            }
                        }
                    }
                }
                matrix[i][j] = sum;
            }
            for (int bi = 0; bi < topBufferHeight - 1; bi++) {
                if (i + bi < stop - bottomBufferHeight) {
                    System.arraycopy(topBuffer[bi + 1], 0, topBuffer[bi], 0, m);
                } else {
                    System.arraycopy(bottomBuffer[bi], 0, topBuffer[bi], 0, m);
                }
            }
            System.arraycopy(matrix[Math.min(i + 1, n - 1)], 0, topBuffer[topBufferHeight - 1], 0, m);
        }
    }
}
