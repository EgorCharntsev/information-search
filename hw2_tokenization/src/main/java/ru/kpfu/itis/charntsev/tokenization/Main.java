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
    private static final Path TOKENS_DIR = OUTPUT_DIR.resolve("tokens");
    private static final Path LEMMAS_DIR = OUTPUT_DIR.resolve("lemmas");

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
        for (var entry : result.documentTokens().entrySet()) {
            writer.writeTokens(entry.getValue(), TOKENS_DIR.resolve(toOutputFileName(entry.getKey())));
        }

        for (var entry : result.documentLemmas().entrySet()) {
            writer.writeLemmas(entry.getValue(), LEMMAS_DIR.resolve(toOutputFileName(entry.getKey())));
        }
    }

    private static String toOutputFileName(String htmlFileName) {
        int dotIndex = htmlFileName.lastIndexOf('.');
        if (dotIndex <= 0) {
            return htmlFileName + ".txt";
        }
        return htmlFileName.substring(0, dotIndex) + ".txt";
    }
}
