package ru.kpfu.itis.charntsev.tokenization.nlp;

import java.io.IOException;
import java.util.Locale;
import java.util.Set;

import static com.github.demidko.aot.WordformMeaning.lookupForMeanings;

public class RussianTextProcessor {

    private static final Set<String> BAD_POS = Set.of(
            "ПРЕДЛ",
            "СОЮЗ",
            "ЧАСТ",
            "МЕЖД",
            "ЧИСЛ"
    );

    public String normalizeToken(String raw) {
        if (raw == null) return null;

        String t = raw.toLowerCase(Locale.ROOT)
                .replace('\u0451', '\u0435');

        if (t.length() < 2) return null;

        for (int i = 0; i < t.length(); i++) {
            char c = t.charAt(i);
            if (c < '\u0430' || c > '\u044f') {
                return null;
            }
        }

        return t;
    }

    public LemmaInfo lemmatize(String token) throws IOException {
        var meanings = lookupForMeanings(token);
        if (meanings == null || meanings.isEmpty()) {
            return null;
        }

        for (var meaning : meanings) {
            String lemma = meaning.getLemma().toString();
            if (lemma == null || lemma.isBlank()) continue;
            lemma = lemma.toLowerCase(Locale.ROOT)
                    .replace('\u0451', '\u0435'); // ё -> е

            var morph = meaning.getMorphology(); // List<MorphologyTag>
            if (morph != null && morph.stream()
                    .map(Object::toString)
                    .anyMatch(BAD_POS::contains)) {
                continue;
            }

            if (normalizeToken(lemma) == null) continue;

            return new LemmaInfo(lemma);
        }

        return null;
    }
}

