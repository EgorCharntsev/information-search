package ru.kpfu.itis.charntsev.crawler.net;

import ru.kpfu.itis.charntsev.crawler.config.CrawlConfig;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Locale;

public class HtmlFetcher {

    private final CrawlConfig cfg;
    private final HttpClient client;

    public HtmlFetcher(CrawlConfig cfg) {
        this.cfg = cfg;
        this.client = HttpClient.newBuilder()
                .followRedirects(Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(cfg.timeoutSeconds()))
                .build();
    }

    public FetchResult fetch(String url) {
        try {
            URI uri = URI.create(url);

            HttpRequest req = HttpRequest.newBuilder(uri)
                    .timeout(Duration.ofSeconds(cfg.timeoutSeconds()))
                    .header("User-Agent", cfg.userAgent())
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .GET()
                    .build();

            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            int code = resp.statusCode();
            if (code < 200 || code >= 300) return FetchResult.fail();

            String ctype = resp.headers().firstValue("content-type").orElse("").toLowerCase(Locale.ROOT);
            if (!ctype.contains("text/html")) return FetchResult.fail();

            String body = resp.body();
            if (body == null || body.isBlank()) return FetchResult.fail();

            return FetchResult.ok(resp.uri().toString(), body, resp.headers().map());
        } catch (Exception e) {
            return FetchResult.fail();
        }
    }
}
