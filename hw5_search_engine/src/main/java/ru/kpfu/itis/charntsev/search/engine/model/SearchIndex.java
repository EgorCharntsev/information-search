package ru.kpfu.itis.charntsev.search.engine.model;

import java.util.Map;

public record SearchIndex(
        Map<String, DocumentVector> documents,
        Map<String, Double> termIdf
) {
}
