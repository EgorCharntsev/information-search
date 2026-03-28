package ru.kpfu.itis.charntsev.search.engine.search;

import ru.kpfu.itis.charntsev.search.engine.model.DocumentVector;
import ru.kpfu.itis.charntsev.search.engine.model.SearchIndex;
import ru.kpfu.itis.charntsev.search.engine.model.SearchResult;
import ru.kpfu.itis.charntsev.search.engine.nlp.LemmaInfo;
import ru.kpfu.itis.charntsev.search.engine.nlp.RussianTextProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VectorSearchEngine {

    private static final Pattern WORD_PATTERN = Pattern.compile("[\\u0410-\\u042F\\u0430-\\u044F\\u0401\\u0451]+");

    private final SearchIndex index;
    private final RussianTextProcessor textProcessor;

    public VectorSearchEngine(SearchIndex index, RussianTextProcessor textProcessor) {
        this.index = index;
        this.textProcessor = textProcessor;
    }

    public List<SearchResult> search(String query) throws IOException {
        return search(query, Integer.MAX_VALUE);
    }

    public List<SearchResult> search(String query, int limit) throws IOException {
        Map<String, Double> queryVector = buildQueryVector(query);
        double queryNorm = vectorNorm(queryVector);
        if (queryVector.isEmpty() || queryNorm == 0.0) {
            return List.of();
        }

        List<SearchResult> results = new ArrayList<>();
        for (DocumentVector documentVector : index.documents().values()) {
            double score = cosineSimilarity(queryVector, queryNorm, documentVector);
            if (score > 0.0) {
                results.add(new SearchResult(documentVector.document(), score));
            }
        }

        results.sort(
                Comparator.comparingDouble(SearchResult::score).reversed()
                        .thenComparing(result -> result.document().fileName())
        );
        if (limit <= 0 || results.size() <= limit) {
            return results;
        }
        return new ArrayList<>(results.subList(0, limit));
    }

    private Map<String, Double> buildQueryVector(String query) throws IOException {
        Map<String, Integer> counts = new TreeMap<>();
        int totalTerms = 0;

        Matcher matcher = WORD_PATTERN.matcher(query);
        while (matcher.find()) {
            String token = textProcessor.normalizeToken(matcher.group());
            if (token == null) {
                continue;
            }

            LemmaInfo lemmaInfo = textProcessor.lemmatize(token);
            if (lemmaInfo == null) {
                continue;
            }

            String lemma = lemmaInfo.lemma();
            if (!index.termIdf().containsKey(lemma)) {
                continue;
            }

            counts.merge(lemma, 1, Integer::sum);
            totalTerms++;
        }

        Map<String, Double> queryVector = new TreeMap<>();
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            double tf = totalTerms == 0 ? 0.0 : (double) entry.getValue() / totalTerms;
            double idf = index.termIdf().getOrDefault(entry.getKey(), 0.0);
            queryVector.put(entry.getKey(), tf * idf);
        }

        return queryVector;
    }

    private double cosineSimilarity(Map<String, Double> queryVector, double queryNorm, DocumentVector documentVector) {
        if (documentVector.norm() == 0.0) {
            return 0.0;
        }

        double dotProduct = 0.0;
        for (Map.Entry<String, Double> entry : queryVector.entrySet()) {
            double documentWeight = documentVector.weights().getOrDefault(entry.getKey(), 0.0);
            dotProduct += entry.getValue() * documentWeight;
        }

        if (dotProduct == 0.0) {
            return 0.0;
        }

        return dotProduct / (queryNorm * documentVector.norm());
    }

    private double vectorNorm(Map<String, Double> vector) {
        double sum = 0.0;
        for (double value : vector.values()) {
            sum += value * value;
        }
        return Math.sqrt(sum);
    }
}
