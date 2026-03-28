package ru.kpfu.itis.charntsev.tf.idf.io;

import ru.kpfu.itis.charntsev.tf.idf.model.TfIdfEntry;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Locale;
import java.util.Map;

public class ResultWriter {

    public void writeAll(Map<String, Map<String, TfIdfEntry>> valuesByDocument, Path outputDir) throws IOException {
        Files.createDirectories(outputDir);

        for (Map.Entry<String, Map<String, TfIdfEntry>> entry : valuesByDocument.entrySet()) {
            writeDocument(entry.getValue(), outputDir.resolve(toOutputFileName(entry.getKey())));
        }
    }

    private void writeDocument(Map<String, TfIdfEntry> values, Path outputFile) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(
                outputFile,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        )) {
            for (Map.Entry<String, TfIdfEntry> entry : values.entrySet()) {
                writer.write(entry.getKey());
                writer.write(' ');
                writer.write(format(entry.getValue().idf()));
                writer.write(' ');
                writer.write(format(entry.getValue().tfIdf()));
                writer.newLine();
            }
        }
    }

    private String toOutputFileName(String htmlFileName) {
        int dotIndex = htmlFileName.lastIndexOf('.');
        if (dotIndex <= 0) {
            return htmlFileName + ".txt";
        }
        return htmlFileName.substring(0, dotIndex) + ".txt";
    }

    private String format(double value) {
        return String.format(Locale.ROOT, "%.6f", value);
    }
}
