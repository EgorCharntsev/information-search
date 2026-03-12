package ru.kpfu.itis.charntsev.inverted.index.io;

import ru.kpfu.itis.charntsev.inverted.index.model.DocumentInfo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;

public class DocumentCatalogLoader {

    public Map<String, DocumentInfo> load(Path indexPath) throws IOException {
        Map<String, DocumentInfo> documents = new TreeMap<>();

        for (String line : Files.readAllLines(indexPath, StandardCharsets.UTF_8)) {
            if (line == null || line.isBlank()) {
                continue;
            }

            String[] parts = line.split("\\t", 2);
            if (parts.length != 2) {
                continue;
            }

            String fileName = parts[0].trim();
            String url = parts[1].trim();
            if (fileName.isEmpty() || url.isEmpty()) {
                continue;
            }

            documents.put(fileName, new DocumentInfo(fileName, url));
        }

        return documents;
    }
}
