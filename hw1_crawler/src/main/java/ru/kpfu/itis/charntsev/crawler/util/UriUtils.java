package ru.kpfu.itis.charntsev.crawler.util;

import java.net.IDN;
import java.net.URI;
import java.net.URL;

public final class UriUtils {
    private UriUtils() {}

    public static URI toSafeUri(String rawUrl) {
        try {
            URL u = new URL(rawUrl);
            String host = u.getHost();
            if (host != null && !host.isBlank()) {
                host = IDN.toASCII(host);
            }

            return new URI(
                    u.getProtocol(),
                    u.getUserInfo(),
                    host,
                    u.getPort(),
                    emptyToNull(u.getPath()),
                    emptyToNull(u.getQuery()),
                    null
            );
        } catch (Exception e) {
            return null;
        }
    }

    private static String emptyToNull(String s) {
        return (s == null || s.isEmpty()) ? null : s;
    }
}