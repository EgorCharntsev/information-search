package ru.kpfu.itis.charntsev.tokenization.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Set;

public class ResultWriter {

    public void writeTokens(Set<String> tokens, Path outFile) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(
                outFile,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        )) {
            for (String token : tokens) {
                writer.write(token);
                writer.newLine();
            }
        }
    }

    public void writeLemmas(Map<String, Set<String>> lemmaToTokens, Path outFile) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(
                outFile,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        )) {
            for (Map.Entry<String, Set<String>> entry : lemmaToTokens.entrySet()) {
                writer.write(entry.getKey());
                for (String token : entry.getValue()) {
                    writer.write(' ');
                    writer.write(token);
                }
                writer.newLine();
            }
        }
    }
}

