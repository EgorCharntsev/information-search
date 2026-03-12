package ru.kpfu.itis.charntsev.crawler.config;

import java.nio.file.Paths;
import java.util.Locale;
import java.util.Map;

public final class ConfigLoader {
    private ConfigLoader() {}

    public static CrawlConfig load(Map<String, String> env, String[] args) {
        CrawlConfig.CrawlConfigBuilder configBuilder = CrawlConfig.builderWithDefaults();

        configBuilder.limit(envInt(env, "CRAWL_LIMIT", 100));
        configBuilder.lang(envStr(env, "CRAWL_LANG", "ru"));
        configBuilder.seedsPath(Paths.get(envStr(env, "CRAWL_SEEDS", "seeds.txt")));
        configBuilder.outDir(Paths.get(envStr(env, "CRAWL_OUT", "hw1_crawler/output")));
        configBuilder.delayMs(envInt(env, "CRAWL_DELAY_MS", 250));
        configBuilder.timeoutSeconds(envInt(env, "CRAWL_TIMEOUT_S", 20));
        configBuilder.minTextChars(envInt(env, "CRAWL_MIN_TEXT_CHARS", 200));
        configBuilder.maxQueue(envInt(env, "CRAWL_MAX_QUEUE", 20000));
        configBuilder.verbose(envBool(env, "CRAWL_VERBOSE", true));
        configBuilder.overwrite(envBool(env, "CRAWL_OVERWRITE", true));
        configBuilder.userAgent(envStr(env, "CRAWL_USER_AGENT", CrawlConfig.DEFAULT_USER_AGENT));

        for (int i = 0; i < args.length; i++) {
            String k = args[i];
            String v = (i + 1 < args.length) ? args[i + 1] : null;

            switch (k) {
                case "--seeds" -> { configBuilder.seedsPath(Paths.get(requireValue(k, v))); i++; }
                case "--out" -> { configBuilder.outDir(Paths.get(requireValue(k, v))); i++; }
                case "--limit" -> { configBuilder.limit(Integer.parseInt(requireValue(k, v))); i++; }
                case "--lang" -> { configBuilder.lang(requireValue(k, v)); i++; }
                case "--minTextChars" -> { configBuilder.minTextChars(Integer.parseInt(requireValue(k, v))); i++; }
                case "--delayMs" -> { configBuilder.delayMs(Integer.parseInt(requireValue(k, v))); i++; }
                case "--timeoutSeconds" -> { configBuilder.timeoutSeconds(Integer.parseInt(requireValue(k, v))); i++; }
                case "--maxQueue" -> { configBuilder.maxQueue(Integer.parseInt(requireValue(k, v))); i++; }
                case "--overwrite" -> configBuilder.overwrite(true);
                case "--no-overwrite" -> configBuilder.overwrite(false);
                case "--verbose" -> configBuilder.verbose(true);
                case "--no-verbose" -> configBuilder.verbose(false);
                case "--userAgent" -> { configBuilder.userAgent(requireValue(k, v)); i++; }
                default -> {}
            }
        }

        return configBuilder.build();
    }

    private static String requireValue(String k, String v) {
        if (v == null) throw new IllegalArgumentException("Нет значения для " + k);
        return v;
    }

    private static int envInt(Map<String, String> env, String key, int def) {
        String v = env.get(key);
        if (v == null || v.isBlank()) return def;
        try {
            return Integer.parseInt(v.trim());
        } catch (Exception e) {
            return def;
        }
    }

    private static String envStr(Map<String, String> env, String key, String def) {
        String v = env.get(key);
        return (v == null || v.isBlank()) ? def : v.trim();
    }

    private static boolean envBool(Map<String, String> env, String key, boolean def) {
        String v = env.get(key);
        if (v == null || v.isBlank()) return def;
        String t = v.trim().toLowerCase(Locale.ROOT);
        return t.equals("true") || t.equals("1") || t.equals("yes") || t.equals("y");
    }
}
