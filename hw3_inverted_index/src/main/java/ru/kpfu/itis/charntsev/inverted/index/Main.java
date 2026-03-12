package ru.kpfu.itis.charntsev.inverted.index;

import ru.kpfu.itis.charntsev.inverted.index.core.InvertedIndexBuilder;
import ru.kpfu.itis.charntsev.inverted.index.io.DocumentCatalogLoader;
import ru.kpfu.itis.charntsev.inverted.index.io.InvertedIndexWriter;
import ru.kpfu.itis.charntsev.inverted.index.model.DocumentInfo;
import ru.kpfu.itis.charntsev.inverted.index.model.InvertedIndex;
import ru.kpfu.itis.charntsev.inverted.index.query.BooleanQueryParser;
import ru.kpfu.itis.charntsev.inverted.index.search.BooleanSearchEngine;
import ru.kpfu.itis.charntsev.tokenization.html.HtmlTextExtractor;
import ru.kpfu.itis.charntsev.tokenization.nlp.RussianTextProcessor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class Main {

    private static final Path INPUT_PAGES_DIR = Paths.get("hw1_crawler/output/pages");
    private static final Path INPUT_CRAWLER_INDEX = Paths.get("hw1_crawler/output/index.txt");
    private static final Path OUTPUT_DIR = Paths.get("hw3_inverted_index/output");
    private static final Path OUTPUT_INDEX = OUTPUT_DIR.resolve("inverted_index.txt");

    public static void main(String[] args) throws Exception {
        validateInputs();
        Files.createDirectories(OUTPUT_DIR);

        DocumentCatalogLoader catalogLoader = new DocumentCatalogLoader();
        Map<String, DocumentInfo> documents = catalogLoader.load(INPUT_CRAWLER_INDEX);
        if (documents.isEmpty()) {
            throw new IllegalStateException("The file hw1_crawler/output/index.txt empty or has an incorrect format!");
        }

        HtmlTextExtractor extractor = new HtmlTextExtractor();
        RussianTextProcessor processor = new RussianTextProcessor();
        InvertedIndexBuilder builder = new InvertedIndexBuilder(extractor, processor);
        InvertedIndex index = builder.build(INPUT_PAGES_DIR, documents);

        InvertedIndexWriter writer = new InvertedIndexWriter();
        writer.write(index, OUTPUT_INDEX);

        System.out.println("Inverted index has been built.");
        System.out.println("Terms in the index: " + index.postings().size());
        System.out.println("Documents in the index: " + index.documents().size());
        System.out.println("Index file: " + OUTPUT_INDEX.toAbsolutePath());

        String query = readQuery(args);
        if (query == null || query.isBlank()) {
            System.out.println("The search query has not been entered. The work is completed after the index is built.");
            return;
        }

        BooleanSearchEngine searchEngine = new BooleanSearchEngine(index, builder, new BooleanQueryParser());
        Set<String> result = searchEngine.search(query);

        System.out.println();
        System.out.println("Query: " + query);
        System.out.println("Documents found: " + result.size());

        if (result.isEmpty()) {
            System.out.println("No matches found.");
            return;
        }

        for (String documentId : result) {
            DocumentInfo document = index.getDocument(documentId);
            if (document == null) {
                System.out.println(documentId);
            } else {
                System.out.println(document.fileName() + "\t" + document.url());
            }
        }
    }

    private static void validateInputs() {
        if (!Files.isDirectory(INPUT_PAGES_DIR)) {
            throw new IllegalStateException("The directory with the pages was not found: " + INPUT_PAGES_DIR.toAbsolutePath());
        }
        if (!Files.exists(INPUT_CRAWLER_INDEX)) {
            throw new IllegalStateException("The page list file was not found: " + INPUT_CRAWLER_INDEX.toAbsolutePath());
        }
    }

    private static String readQuery(String[] args) throws Exception {
        if (args != null && args.length > 0) {
            return String.join(" ", args).trim();
        }

        System.out.println();
        System.out.println("Enter a Boolean query (AND, OR, NOT, parentheses). To finish the search, just press Enter:");

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8)
        );

        String line = reader.readLine();
        return line == null ? "" : line.trim();
    }

}
