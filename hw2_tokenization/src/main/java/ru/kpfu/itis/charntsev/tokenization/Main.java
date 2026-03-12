package ru.kpfu.itis.charntsev.tokenization;

import ru.kpfu.itis.charntsev.tokenization.core.TokenizationPipeline;
import ru.kpfu.itis.charntsev.tokenization.core.TokenizationResult;
import ru.kpfu.itis.charntsev.tokenization.html.HtmlTextExtractor;
import ru.kpfu.itis.charntsev.tokenization.io.ResultWriter;
import ru.kpfu.itis.charntsev.tokenization.nlp.RussianTextProcessor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    private static final Path INPUT_PAGES_DIR = Paths.get("hw1_crawler/output/pages");
    private static final Path OUTPUT_DIR = Paths.get("hw2_tokenization/output");

    public static void main(String[] args) throws Exception {
        if (!Files.isDirectory(INPUT_PAGES_DIR)) {
            throw new IllegalStateException("Не найдена директория с HTML-страницами: " +
                    INPUT_PAGES_DIR.toAbsolutePath());
        }

        Files.createDirectories(OUTPUT_DIR);

        HtmlTextExtractor extractor = new HtmlTextExtractor();
        RussianTextProcessor processor = new RussianTextProcessor();
        TokenizationPipeline pipeline = new TokenizationPipeline(extractor, processor);
        ResultWriter writer = new ResultWriter();

        TokenizationResult result = pipeline.run(INPUT_PAGES_DIR);

        Path tokensPath = OUTPUT_DIR.resolve("tokens.txt");
        Path lemmasPath = OUTPUT_DIR.resolve("lemmas.txt");

        writer.writeTokens(result.uniqueTokens(), tokensPath);
        writer.writeLemmas(result.lemmaToTokens(), lemmasPath);
    }
}