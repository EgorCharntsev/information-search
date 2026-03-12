package ru.kpfu.itis.charntsev.tokenization.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class HtmlTextExtractor {

    public String extractMainText(String html) {
        Document doc = Jsoup.parse(html);

        Element wiki = doc.selectFirst("#mw-content-text");
        if (wiki != null) {
            return wiki.text();
        }

        Element main = doc.selectFirst("main");
        if (main != null) {
            return main.text();
        }

        Element body = doc.body();
        return body != null ? body.text() : doc.text();
    }
}

