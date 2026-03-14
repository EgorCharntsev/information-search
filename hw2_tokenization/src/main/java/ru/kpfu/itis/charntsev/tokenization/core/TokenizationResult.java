package ru.kpfu.itis.charntsev.tokenization.core;

import java.util.Map;
import java.util.Set;

public record TokenizationResult(
        Map<String, Set<String>> documentTokens,
        Map<String, Map<String, Set<String>>> documentLemmas
) {
}

