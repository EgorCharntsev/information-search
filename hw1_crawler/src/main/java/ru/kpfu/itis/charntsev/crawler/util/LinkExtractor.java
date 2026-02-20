package ru.kpfu.itis.charntsev.crawler.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

public final class LinkExtractor {
    private LinkExtractor() {}

    public static List<String> extract(String html, String baseUrl) {
        try {
            Document doc = Jsoup.parse(html, baseUrl);
            List<String> out = new ArrayList<>();
            for (Element a : doc.select("a[href]")) {
                String abs = a.absUrl("href");
                if (abs != null && !abs.isBlank()) out.add(abs);
            }
            return out;
        } catch (Exception e) {
            return List.of();
        }
    }
}