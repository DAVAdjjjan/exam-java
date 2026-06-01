package com.exam;

public class RowAverageTask implements Runnable {

    private final int[][] matrix;
    private final double[] results;
    private final int start;
    private final int end;

    public RowAverageTask(int[][] matrix, double[] results, int start, int end) {
        this.matrix = matrix;
        this.results = results;
        this.start = start;
        this.end = end;
    }

    @Override
    public void run() {
        for (int r = start; r < end; r++) {
            double sum = 0;
            for (int val : matrix[r]) sum += val;
            results[r] = sum / matrix[r].length;
        }
    }
}
