package ru.kpfu.itis.charntsev.search.engine.model;

public record SearchResult(
        DocumentInfo document,
        double score
) {
}
