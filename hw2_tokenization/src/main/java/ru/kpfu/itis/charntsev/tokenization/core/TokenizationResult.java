package ru.kpfu.itis.charntsev.tokenization.core;

import java.util.Map;
import java.util.Set;

public record TokenizationResult(
        Set<String> uniqueTokens,
        Map<String, Set<String>> lemmaToTokens
) {
}

