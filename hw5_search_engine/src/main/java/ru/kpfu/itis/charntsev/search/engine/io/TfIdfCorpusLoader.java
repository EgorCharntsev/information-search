package ru.kpfu.itis.charntsev.search.engine.io;

import ru.kpfu.itis.charntsev.search.engine.model.DocumentVector;
import ru.kpfu.itis.charntsev.search.engine.model.DocumentInfo;
import ru.kpfu.itis.charntsev.search.engine.model.SearchIndex;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;

public class TfIdfCorpusLoader {

    public SearchIndex load(Path tfIdfDir, Map<String, DocumentInfo> documents) throws IOException {
        Map<String, DocumentVector> vectors = new TreeMap<>();
        Map<String, Double> termIdf = new TreeMap<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(tfIdfDir, "*.txt")) {
            for (Path file : stream) {
                String documentId = toDocumentId(file.getFileName().toString());
                DocumentInfo documentInfo = documents.get(documentId);
                if (documentInfo == null) {
                    continue;
                }

                Map<String, Double> weights = new TreeMap<>();
                for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                    if (line == null || line.isBlank()) {
                        continue;
                    }

                    String[] parts = line.trim().split("\\s+");
                    if (parts.length < 3) {
                        continue;
                    }

                    String term = parts[0];
                    double idf = parseDouble(parts[1]);
                    double tfIdf = parseDouble(parts[2]);

                    weights.put(term, tfIdf);
                    termIdf.putIfAbsent(term, idf);
                }

                vectors.put(documentId, new DocumentVector(documentInfo, weights, vectorNorm(weights)));
            }
        }

        return new SearchIndex(vectors, termIdf);
    }

    private String toDocumentId(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex <= 0) {
            return fileName + ".html";
        }
        return fileName.substring(0, dotIndex) + ".html";
    }

    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return 0.0;
        }
    }

    private double vectorNorm(Map<String, Double> weights) {
        double sum = 0.0;
        for (double value : weights.values()) {
            sum += value * value;
        }
        return Math.sqrt(sum);
    }
}
