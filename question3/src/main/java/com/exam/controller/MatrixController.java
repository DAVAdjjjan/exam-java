package com.exam.controller;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exam.RowAverageTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class MatrixController {

    @Value("${matrix.file}")
    private Resource matrixFile;

    private int[][] matrix;

    @PostConstruct
    void loadMatrix() throws Exception {
        List<int[]> rows = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(matrixFile.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] tokens = line.trim().split("\\s+");
                int[] row = new int[tokens.length];
                for (int i = 0; i < tokens.length; i++) {
                    row[i] = Integer.parseInt(tokens[i]);
                }
                rows.add(row);
            }
        }
        matrix = rows.toArray(new int[0][]);
    }

    @GetMapping("/api/matrix/averages")
    public List<Double> averages() {
        int cores = Runtime.getRuntime().availableProcessors();
        double[] results = new double[matrix.length];

        try (ExecutorService pool = Executors.newFixedThreadPool(cores)) {
            int chunk = (matrix.length + cores - 1) / cores;
            for (int t = 0; t < cores; t++) {
                int start = t * chunk;
                int end = Math.min(start + chunk, matrix.length);
                if (start >= end) break;
                pool.submit(new RowAverageTask(matrix, results, start, end));
            }
        }

        List<Double> list = new ArrayList<>(results.length);
        for (double avg : results) list.add(avg);
        return list;
    }
}
