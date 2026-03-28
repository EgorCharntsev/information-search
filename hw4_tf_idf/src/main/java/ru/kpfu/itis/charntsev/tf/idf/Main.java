package ru.kpfu.itis.charntsev.tf.idf;

import ru.kpfu.itis.charntsev.tf.idf.core.TfIdfCalculator;
import ru.kpfu.itis.charntsev.tf.idf.io.ResultWriter;
import ru.kpfu.itis.charntsev.tf.idf.model.CorpusTfIdfResult;
import ru.kpfu.itis.charntsev.tokenization.html.HtmlTextExtractor;
import ru.kpfu.itis.charntsev.tokenization.nlp.RussianTextProcessor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    private static final Path INPUT_PAGES_DIR = Paths.get("hw1_crawler/output/pages");
    private static final Path OUTPUT_DIR = Paths.get("hw4_tf_idf/output");
    private static final Path TERMS_DIR = OUTPUT_DIR.resolve("terms");
    private static final Path LEMMAS_DIR = OUTPUT_DIR.resolve("lemmas");

    public static void main(String[] args) throws Exception {
        validateInputs();

        Files.createDirectories(TERMS_DIR);
        Files.createDirectories(LEMMAS_DIR);

        HtmlTextExtractor extractor = new HtmlTextExtractor();
        RussianTextProcessor processor = new RussianTextProcessor();
        TfIdfCalculator calculator = new TfIdfCalculator(extractor, processor);
        ResultWriter writer = new ResultWriter();

        CorpusTfIdfResult result = calculator.calculate(INPUT_PAGES_DIR);
        writer.writeAll(result.documentTerms(), TERMS_DIR);
        writer.writeAll(result.documentLemmas(), LEMMAS_DIR);

        System.out.println("TF-IDF calculation completed.");
        System.out.println("Documents processed: " + result.documentCount());
        System.out.println("Terms output: " + TERMS_DIR.toAbsolutePath());
        System.out.println("Lemmas output: " + LEMMAS_DIR.toAbsolutePath());
    }

    private static void validateInputs() {
        if (!Files.isDirectory(INPUT_PAGES_DIR)) {
            throw new IllegalStateException("The directory with the pages was not found: " + INPUT_PAGES_DIR.toAbsolutePath());
        }
    }
}
