package ru.kpfu.itis.charntsev.crawler.util;

import java.net.URI;
import java.util.Locale;
import java.util.regex.Pattern;

public final class UrlNormalizer {
    private UrlNormalizer() {}

    private static final Pattern BAD_EXT = Pattern.compile(
            ".*\\.(css|js|png|jpg|jpeg|gif|svg|ico|pdf|zip|rar|7z|mp3|mp4|avi|webm)(\\?.*)?$",
            Pattern.CASE_INSENSITIVE
    );

    public static String normalize(String url) {
        if (url == null) return null;
        url = url.trim();
        if (url.isEmpty()) return null;

        int hash = url.indexOf('#');
        if (hash >= 0) url = url.substring(0, hash);

        String lower = url.toLowerCase(Locale.ROOT);
        if (lower.startsWith("mailto:") || lower.startsWith("javascript:")) return null;
        if (!(lower.startsWith("http://") || lower.startsWith("https://"))) return null;

        URI safe = UriUtils.toSafeUri(url);
        if (safe == null) return null;

        return safe.toASCIIString();
    }

    public static boolean isLikelyHtmlPage(String url) {
        if (url == null) return false;
        return !BAD_EXT.matcher(url).matches();
    }
}
