package ru.kpfu.itis.charntsev.crawler.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public final class TextHeuristics {
    private TextHeuristics() {}

    public static boolean hasEnoughText(String html, int minChars) {
        try {
            Document doc = Jsoup.parse(html);
            String text = (doc.body() == null) ? "" : doc.body().text();
            return text != null && text.trim().length() >= minChars;
        } catch (Exception e) {
            return false;
        }
    }
}