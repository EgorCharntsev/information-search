package ru.kpfu.itis.charntsev.crawler.config;

import lombok.Builder;

import java.nio.file.Path;
import java.nio.file.Paths;

@Builder(toBuilder = true)
public record CrawlConfig(
        Path seedsPath,
        Path outDir,
        int limit,
        String lang,
        int minTextChars,
        int delayMs,
        int timeoutSeconds,
        int maxQueue,
        boolean overwrite,
        boolean verbose,
        String userAgent
) {
    public static final String DEFAULT_USER_AGENT =
            "Mozilla/5.0 (compatible; ITIS-Crawler/1.0; +https://example.com)";

    public static CrawlConfigBuilder builderWithDefaults() {
        return CrawlConfig.builder()
                .seedsPath(Paths.get(".\\seeds.txt"))
                .outDir(Paths.get("out"))
                .limit(100)
                .lang("ru")
                .minTextChars(200)
                .delayMs(250)
                .timeoutSeconds(20)
                .maxQueue(20000)
                .overwrite(true)
                .verbose(true)
                .userAgent(DEFAULT_USER_AGENT);
    }
}