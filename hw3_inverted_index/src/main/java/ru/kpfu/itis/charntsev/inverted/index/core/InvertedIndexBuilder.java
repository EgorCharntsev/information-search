package ru.kpfu.itis.charntsev.inverted.index.core;

import ru.kpfu.itis.charntsev.inverted.index.model.DocumentInfo;
import ru.kpfu.itis.charntsev.inverted.index.model.InvertedIndex;
import ru.kpfu.itis.charntsev.tokenization.html.HtmlTextExtractor;
import ru.kpfu.itis.charntsev.tokenization.nlp.LemmaInfo;
import ru.kpfu.itis.charntsev.tokenization.nlp.RussianTextProcessor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InvertedIndexBuilder {

    private static final Pattern WORD_PATTERN = Pattern.compile("[А-Яа-яЁё]+");

    private final HtmlTextExtractor textExtractor;
    private final RussianTextProcessor textProcessor;

    public InvertedIndexBuilder(HtmlTextExtractor textExtractor, RussianTextProcessor textProcessor) {
        this.textExtractor = textExtractor;
        this.textProcessor = textProcessor;
    }

    public InvertedIndex build(Path pagesDir, Map<String, DocumentInfo> documents) throws IOException {
        Map<String, Set<String>> postings = new TreeMap<>();

        for (Map.Entry<String, DocumentInfo> entry : documents.entrySet()) {
            String documentId = entry.getKey();
            Path pagePath = pagesDir.resolve(documentId);
            if (!Files.exists(pagePath)) {
                continue;
            }

            Set<String> documentTerms = extractTerms(pagePath);
            for (String term : documentTerms) {
                postings.computeIfAbsent(term, key -> new TreeSet<>()).add(documentId);
            }
        }

        return new InvertedIndex(postings, documents);
    }

    private Set<String> extractTerms(Path pagePath) throws IOException {
        String html = Files.readString(pagePath, StandardCharsets.UTF_8);
        String text = textExtractor.extractMainText(html);

        Set<String> terms = new TreeSet<>();
        Matcher matcher = WORD_PATTERN.matcher(text);
        while (matcher.find()) {
            String token = textProcessor.normalizeToken(matcher.group());
            if (token == null) {
                continue;
            }

            String term = normalizeToTerm(token);
            if (term != null) {
                terms.add(term);
            }
        }

        return terms;
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
