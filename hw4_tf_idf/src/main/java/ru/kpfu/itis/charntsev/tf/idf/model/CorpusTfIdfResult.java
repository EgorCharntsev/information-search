package ru.kpfu.itis.charntsev.tf.idf.model;

import java.util.Map;

public record CorpusTfIdfResult(
        Map<String, Map<String, TfIdfEntry>> documentTerms,
        Map<String, Map<String, TfIdfEntry>> documentLemmas,
        int documentCount
) {
}
