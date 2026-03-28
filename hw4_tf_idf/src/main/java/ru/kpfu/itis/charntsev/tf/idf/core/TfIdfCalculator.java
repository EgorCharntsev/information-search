package ru.kpfu.itis.charntsev.tf.idf.core;

import ru.kpfu.itis.charntsev.tf.idf.model.CorpusTfIdfResult;
import ru.kpfu.itis.charntsev.tf.idf.model.DocumentTermData;
import ru.kpfu.itis.charntsev.tf.idf.model.TfIdfEntry;
import ru.kpfu.itis.charntsev.tokenization.html.HtmlTextExtractor;
import ru.kpfu.itis.charntsev.tokenization.nlp.LemmaInfo;
import ru.kpfu.itis.charntsev.tokenization.nlp.RussianTextProcessor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TfIdfCalculator {

    private static final Pattern WORD_PATTERN = Pattern.compile("[А-Яа-яЁё]+");

    private final HtmlTextExtractor textExtractor;
    private final RussianTextProcessor textProcessor;

    public TfIdfCalculator(HtmlTextExtractor textExtractor, RussianTextProcessor textProcessor) {
        this.textExtractor = textExtractor;
        this.textProcessor = textProcessor;
    }

    public CorpusTfIdfResult calculate(Path inputPagesDir) throws IOException {
        Map<String, DocumentTermData> documentStats = new TreeMap<>();
        Map<String, Integer> termDocumentFrequency = new TreeMap<>();
        Map<String, Integer> lemmaDocumentFrequency = new TreeMap<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(inputPagesDir, "*.html")) {
            for (Path page : stream) {
                DocumentTermData documentData = analyzeDocument(page);
                String fileName = page.getFileName().toString();
                documentStats.put(fileName, documentData);

                incrementDocumentFrequency(termDocumentFrequency, documentData.termCounts().keySet());
                incrementDocumentFrequency(lemmaDocumentFrequency, documentData.lemmaCounts().keySet());
            }
        }

        int documentCount = documentStats.size();
        return new CorpusTfIdfResult(
                buildTfIdfMap(documentStats, termDocumentFrequency, documentCount, true),
                buildTfIdfMap(documentStats, lemmaDocumentFrequency, documentCount, false),
                documentCount
        );
    }

    private DocumentTermData analyzeDocument(Path page) throws IOException {
        String html = Files.readString(page, StandardCharsets.UTF_8);
        String text = textExtractor.extractMainText(html);

        Map<String, Integer> termCounts = new TreeMap<>();
        Map<String, Integer> lemmaCounts = new TreeMap<>();
        int totalTerms = 0;

        Matcher matcher = WORD_PATTERN.matcher(text);
        while (matcher.find()) {
            String token = textProcessor.normalizeToken(matcher.group());
            if (token == null) {
                continue;
            }

            LemmaInfo lemmaInfo = textProcessor.lemmatize(token);
            if (lemmaInfo == null) {
                continue;
            }

            totalTerms++;
            termCounts.merge(token, 1, Integer::sum);
            lemmaCounts.merge(lemmaInfo.lemma(), 1, Integer::sum);
        }

        return new DocumentTermData(termCounts, lemmaCounts, totalTerms);
    }

    private void incrementDocumentFrequency(Map<String, Integer> documentFrequency, Set<String> values) {
        for (String value : values) {
            documentFrequency.merge(value, 1, Integer::sum);
        }
    }

    private Map<String, Map<String, TfIdfEntry>> buildTfIdfMap(
            Map<String, DocumentTermData> documentStats,
            Map<String, Integer> documentFrequency,
            int documentCount,
            boolean useTerms
    ) {
        Map<String, Map<String, TfIdfEntry>> result = new TreeMap<>();

        for (Map.Entry<String, DocumentTermData> entry : documentStats.entrySet()) {
            DocumentTermData documentData = entry.getValue();
            Map<String, Integer> counts = useTerms ? documentData.termCounts() : documentData.lemmaCounts();
            Map<String, TfIdfEntry> values = new TreeMap<>();

            for (Map.Entry<String, Integer> countEntry : counts.entrySet()) {
                String value = countEntry.getKey();
                double tf = documentData.totalTerms() == 0
                        ? 0.0
                        : (double) countEntry.getValue() / documentData.totalTerms();
                int df = documentFrequency.getOrDefault(value, 0);
                double idf = calculateIdf(documentCount, df);
                values.put(value, new TfIdfEntry(idf, tf * idf));
            }

            result.put(entry.getKey(), values);
        }

        return result;
    }

    private double calculateIdf(int documentCount, int documentFrequency) {
        if (documentCount == 0 || documentFrequency == 0) {
            return 0.0;
        }
        return Math.log((double) documentCount / documentFrequency);
    }
}
