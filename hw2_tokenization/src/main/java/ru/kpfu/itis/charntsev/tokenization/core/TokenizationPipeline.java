package ru.kpfu.itis.charntsev.tokenization.core;

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
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenizationPipeline {

    private static final Pattern WORD_PATTERN = Pattern.compile("[А-Яа-яЁё]+");

    private final HtmlTextExtractor textExtractor;
    private final RussianTextProcessor textProcessor;

    public TokenizationPipeline(HtmlTextExtractor textExtractor, RussianTextProcessor textProcessor) {
        this.textExtractor = textExtractor;
        this.textProcessor = textProcessor;
    }

    public TokenizationResult run(Path inputPagesDir) throws IOException {
        Map<String, Set<String>> documentTokens = new TreeMap<>();
        Map<String, Map<String, Set<String>>> documentLemmas = new TreeMap<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(inputPagesDir, "*.html")) {
            for (Path page : stream) {
                String html = Files.readString(page, StandardCharsets.UTF_8);
                String text = textExtractor.extractMainText(html);
                Set<String> pageTokens = new TreeSet<>();
                Map<String, Set<String>> pageLemmas = new TreeMap<>();

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

                    pageTokens.add(token);
                    pageLemmas
                            .computeIfAbsent(lemmaInfo.lemma(), k -> new TreeSet<>())
                            .add(token);
                }

                String fileName = page.getFileName().toString();
                documentTokens.put(fileName, pageTokens);
                documentLemmas.put(fileName, pageLemmas);
            }
        }

        return new TokenizationResult(documentTokens, documentLemmas);
    }
}

