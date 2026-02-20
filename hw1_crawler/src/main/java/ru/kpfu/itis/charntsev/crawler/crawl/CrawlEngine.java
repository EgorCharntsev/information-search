package ru.kpfu.itis.charntsev.crawler.crawl;

import ru.kpfu.itis.charntsev.crawler.config.CrawlConfig;
import ru.kpfu.itis.charntsev.crawler.net.FetchResult;
import ru.kpfu.itis.charntsev.crawler.net.HtmlFetcher;
import ru.kpfu.itis.charntsev.crawler.util.LanguageDetector;
import ru.kpfu.itis.charntsev.crawler.util.LinkExtractor;
import ru.kpfu.itis.charntsev.crawler.util.TextHeuristics;
import ru.kpfu.itis.charntsev.crawler.util.HostPolicy;
import ru.kpfu.itis.charntsev.crawler.util.UrlNormalizer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class CrawlEngine {

    private final CrawlConfig cfg;
    private final HtmlFetcher fetcher;

    public CrawlEngine(CrawlConfig cfg) {
        this.cfg = cfg;
        this.fetcher = new HtmlFetcher(cfg);
    }

    public CrawlResult run() throws Exception {
        List<String> seeds = readSeeds(cfg.seedsPath().toString());

        if (seeds.isEmpty()) {
            throw new IllegalStateException("seeds файл пустой: " + cfg.seedsPath().toAbsolutePath());
        }

        HostPolicy hostPolicy = HostPolicy.fromSeeds(seeds);
        if (hostPolicy.allowedHosts().isEmpty()) {
            throw new IllegalStateException("Не удалось извлечь домены из seeds.");
        }

        Files.createDirectories(cfg.outDir());
        Path pagesDir = cfg.outDir().resolve("pages");
        Files.createDirectories(pagesDir);

        Path indexPath = cfg.outDir().resolve("index.txt");
        if (Files.exists(indexPath) && !cfg.overwrite()) {
            throw new IllegalStateException("index.txt уже существует. Установи CRAWL_OVERWRITE=true или --overwrite.");
        }

        Deque<String> queue = new ArrayDeque<>(seeds);
        Set<String> visited = new HashSet<>();
        int saved = 0;

        try (BufferedWriter index = Files.newBufferedWriter(
                indexPath,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        )) {

            while (!queue.isEmpty() && saved < cfg.limit()) {
                String raw = queue.pollFirst();
                String url = UrlNormalizer.normalize(raw);
                if (url == null) continue;

                if (!visited.add(url)) continue;
                if (!hostPolicy.isAllowed(url)) continue;
                if (!UrlNormalizer.isLikelyHtmlPage(url)) continue;

                if (cfg.delayMs() > 0) Thread.sleep(cfg.delayMs());

                FetchResult fr = fetcher.fetch(url);
                if (!fr.ok()) continue;

                String detectedLang = LanguageDetector.detect(fr.html(), fr.headers());
                if (detectedLang == null) continue;
                if (!LanguageDetector.langMatches(detectedLang, cfg.lang())) continue;

                if (!TextHeuristics.hasEnoughText(fr.html(), cfg.minTextChars())) continue;

                saved++;
                String fileName = String.format("%05d.html", saved);
                Path outFile = pagesDir.resolve(fileName);

                Files.writeString(outFile, fr.html(), StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

                index.write(fileName + "\t" + fr.finalUrl());
                index.newLine();

                if (cfg.verbose()) {
                    System.out.printf("Saved %d/%d (%s): %s%n",
                            saved, cfg.limit(), cfg.lang(), fr.finalUrl());
                }

                for (String link : LinkExtractor.extract(fr.html(), fr.finalUrl())) {
                    if (queue.size() >= cfg.maxQueue()) break;

                    String norm = UrlNormalizer.normalize(link);
                    if (norm == null) continue;
                    if (!UrlNormalizer.isLikelyHtmlPage(norm)) continue;
                    if (!hostPolicy.isAllowed(norm)) continue;

                    if (!visited.contains(norm)) queue.addLast(norm);
                }
            }
        }

        return new CrawlResult(saved, cfg.outDir(), indexPath);
    }

    private List<String> readSeeds(String path) throws IOException {
        Path p = Paths.get(path);
        List<String> lines;

        if (Files.exists(p)) {
            lines = Files.readAllLines(p, StandardCharsets.UTF_8);
        } else {
            try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
                if (is == null) throw new NoSuchFileException(path);
                lines = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                        .lines().toList();
            }
        }

        return lines.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty() && !s.startsWith("#"))
                .toList();
    }
}