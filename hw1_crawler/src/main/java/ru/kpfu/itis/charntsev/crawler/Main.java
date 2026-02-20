package ru.kpfu.itis.charntsev.crawler;

import ru.kpfu.itis.charntsev.crawler.config.ConfigLoader;
import ru.kpfu.itis.charntsev.crawler.config.CrawlConfig;
import ru.kpfu.itis.charntsev.crawler.crawl.CrawlEngine;
import ru.kpfu.itis.charntsev.crawler.crawl.CrawlResult;

public class Main {
    public static void main(String[] args) throws Exception {
        CrawlConfig cfg = ConfigLoader.load(System.getenv(), args);

        CrawlEngine engine = new CrawlEngine(cfg);
        CrawlResult res = engine.run();

        System.out.println("Готово. Скачано страниц: " + res.saved());
        System.out.println("Вывод: " + res.outDir().toAbsolutePath());
        System.out.println("index.txt: " + res.indexPath().toAbsolutePath());
    }
}