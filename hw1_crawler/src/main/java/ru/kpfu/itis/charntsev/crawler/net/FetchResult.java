package ru.kpfu.itis.charntsev.crawler.net;

import java.util.List;
import java.util.Map;

public record FetchResult(boolean ok, String finalUrl, String html, Map<String, List<String>> headers) {

    public static FetchResult fail() {
        return new FetchResult(false, null, null, Map.of());
    }

    public static FetchResult ok(String finalUrl, String html, Map<String, List<String>> headers) {
        return new FetchResult(true, finalUrl, html, headers);
    }
}
