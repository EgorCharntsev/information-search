package ru.kpfu.itis.charntsev.inverted.index.io;

import ru.kpfu.itis.charntsev.inverted.index.model.InvertedIndex;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Set;

public class InvertedIndexWriter {

    public void write(InvertedIndex index, Path outFile) throws IOException {
        Files.createDirectories(outFile.getParent());

        try (BufferedWriter writer = Files.newBufferedWriter(
                outFile,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        )) {
            for (Map.Entry<String, ? extends Set<String>> entry : index.postings().entrySet()) {
                writer.write(entry.getKey());
                writer.write('\t');
                writer.write(String.join(", ", entry.getValue()));
                writer.newLine();
            }
        }
    }
}
