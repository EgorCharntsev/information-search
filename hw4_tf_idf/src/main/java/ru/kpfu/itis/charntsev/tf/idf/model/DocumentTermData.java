package ru.kpfu.itis.charntsev.tf.idf.model;

import java.util.Map;

public record DocumentTermData(
        Map<String, Integer> termCounts,
        Map<String, Integer> lemmaCounts,
        int totalTerms
) {
}
