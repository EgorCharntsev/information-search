package ru.kpfu.itis.charntsev.crawler.crawl;

import java.nio.file.Path;

public record CrawlResult(int saved, Path outDir, Path indexPath) {}
