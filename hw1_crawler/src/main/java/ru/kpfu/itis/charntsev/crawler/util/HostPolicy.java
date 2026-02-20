package ru.kpfu.itis.charntsev.crawler.util;

import java.net.URI;
import java.util.*;

public final class HostPolicy {
    private final Set<String> allowedHosts;

    private HostPolicy(Set<String> allowedHosts) {
        this.allowedHosts = allowedHosts;
    }

    public static HostPolicy fromSeeds(List<String> seeds) {
        Set<String> hosts = new HashSet<>();

        for (String s : seeds) {
            try {
                String norm = UrlNormalizer.normalize(s);
                if (norm == null) continue;

                URI u = URI.create(norm);
                if (u.getHost() != null) {
                    hosts.add(u.getHost().toLowerCase(Locale.ROOT));
                }
            } catch (Exception ignored) {}
        }

        return new HostPolicy(hosts);
    }

    public boolean isAllowed(String url) {
        try {
            URI u = URI.create(url);
            String h = u.getHost();
            if (h == null) return false;
            h = h.toLowerCase(Locale.ROOT);

            if (allowedHosts.contains(h)) return true;
            for (String ah : allowedHosts) {
                if (h.endsWith("." + ah)) return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public Set<String> allowedHosts() {
        return Collections.unmodifiableSet(allowedHosts);
    }
}
