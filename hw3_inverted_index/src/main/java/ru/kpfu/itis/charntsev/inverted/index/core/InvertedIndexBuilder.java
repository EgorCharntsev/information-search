package ru.kpfu.itis.charntsev.inverted.index.core;

import ru.kpfu.itis.charntsev.inverted.index.model.DocumentInfo;
import ru.kpfu.itis.charntsev.inverted.index.model.InvertedIndex;
import ru.kpfu.itis.charntsev.tokenization.nlp.LemmaInfo;
import ru.kpfu.itis.charntsev.tokenization.nlp.RussianTextProcessor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class InvertedIndexBuilder {

    private final RussianTextProcessor textProcessor;

    public InvertedIndexBuilder(RussianTextProcessor textProcessor) {
        this.textProcessor = textProcessor;
    }

    public InvertedIndex build(Path lemmasDir, Map<String, DocumentInfo> documents) throws IOException {
        Map<String, Set<String>> postings = new TreeMap<>();

        for (Map.Entry<String, DocumentInfo> entry : documents.entrySet()) {
            String documentId = entry.getKey();
            Path lemmasPath = lemmasDir.resolve(toLemmaFileName(documentId));
            if (!Files.exists(lemmasPath)) {
                continue;
            }

            Set<String> documentTerms = extractTerms(lemmasPath);
            for (String term : documentTerms) {
                postings.computeIfAbsent(term, key -> new TreeSet<>()).add(documentId);
            }
        }

        return new InvertedIndex(postings, documents);
    }

    private Set<String> extractTerms(Path lemmasPath) throws IOException {
        Set<String> terms = new TreeSet<>();

        List<String> lines = Files.readAllLines(lemmasPath, StandardCharsets.UTF_8);
        for (String line : lines) {
            if (line == null || line.isBlank()) {
                continue;
            }

            String[] parts = line.trim().split("\\s+");
            String term = normalizeToTerm(parts[0]);
            if (term != null) {
                terms.add(term);
            }
        }

        return terms;
    }

    private String toLemmaFileName(String documentId) {
        int dotIndex = documentId.lastIndexOf('.');
        if (dotIndex <= 0) {
            return documentId + ".txt";
        }
        return documentId.substring(0, dotIndex) + ".txt";
    }

    public String normalizeToTerm(String rawWord) throws IOException {
        String token = textProcessor.normalizeToken(rawWord);
        if (token == null) {
            return null;
        }

        LemmaInfo lemmaInfo = textProcessor.lemmatize(token);
        return lemmaInfo != null ? lemmaInfo.lemma() : token;
    }
}
