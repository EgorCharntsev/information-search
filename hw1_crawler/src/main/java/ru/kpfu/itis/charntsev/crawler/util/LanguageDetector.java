package ru.kpfu.itis.charntsev.crawler.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class LanguageDetector {
    private LanguageDetector() {}

    public static String detect(String html, Map<String, List<String>> headers) {
        try {
            Document doc = Jsoup.parse(html);
            Element htmlTag = doc.selectFirst("html[lang]");
            if (htmlTag != null) {
                String lang = htmlTag.attr("lang").trim();
                if (!lang.isEmpty()) return lang;
            }
            for (Map.Entry<String, List<String>> e : headers.entrySet()) {
                if ("content-language".equalsIgnoreCase(e.getKey())) {
                    if (e.getValue() != null && !e.getValue().isEmpty()) {
                        String v = e.getValue().get(0);
                        if (v != null && !v.isBlank()) return v.trim();
                    }
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    public static boolean langMatches(String detected, String required) {
        if (detected == null) return false;
        String req = required.toLowerCase(Locale.ROOT);

        String det = detected.toLowerCase(Locale.ROOT);
        String first = det.split("[,;\\s]")[0].trim();

        return first.equals(req) || first.startsWith(req + "-") || first.startsWith(req + "_");
    }
}
