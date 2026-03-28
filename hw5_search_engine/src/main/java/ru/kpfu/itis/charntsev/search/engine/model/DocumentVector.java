package ru.kpfu.itis.charntsev.search.engine.model;

import java.util.Map;

public record DocumentVector(
        DocumentInfo document,
        Map<String, Double> weights,
        double norm
) {
}
