package ru.kpfu.itis.charntsev.search.engine.web;

import ru.kpfu.itis.charntsev.search.engine.model.SearchResult;

import java.util.List;
import java.util.Locale;

public final class SearchPageRenderer {

    private SearchPageRenderer() {
    }

    public static String render(String query, List<SearchResult> results, int totalResults, int topLimit) {
        StringBuilder html = new StringBuilder();
        html.append("""
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Vector Search Engine</title>
                    <style>
                        :root {
                            --bg: #ffffff;
                            --panel: #ffffff;
                            --ink: #212121;
                            --muted: #757575;
                            --accent: #448aff;
                            --accent-soft: #b3e5fc;
                            --line: #bdbdbd;
                            --shadow: rgba(2, 136, 209, 0.14);
                            --primary: #03a9f4;
                            --primary-dark: #0288d1;
                            --on-primary: #ffffff;
                        }
                        * { box-sizing: border-box; }
                        body {
                            margin: 0;
                            min-height: 100vh;
                            color: var(--ink);
                            background:
                                radial-gradient(circle at top left, rgba(179, 229, 252, 0.55) 0, transparent 24%%),
                                radial-gradient(circle at bottom right, rgba(68, 138, 255, 0.10) 0, transparent 20%%),
                                linear-gradient(180deg, #ffffff 0%%, #fdfefe 100%%);
                            font-family: "Segoe UI", "Helvetica Neue", Arial, sans-serif;
                        }
                        .shell {
                            width: min(920px, calc(100vw - 32px));
                            margin: 48px auto;
                            padding: 32px;
                            border: 1px solid var(--line);
                            border-radius: 24px;
                            background: var(--panel);
                            box-shadow: 0 20px 48px var(--shadow);
                        }
                        h1 {
                            margin: 0;
                            font-size: clamp(2rem, 4vw, 3rem);
                            line-height: 1;
                            letter-spacing: -0.03em;
                            font-weight: 700;
                        }
                        form {
                            display: grid;
                            grid-template-columns: 1fr auto;
                            gap: 12px;
                            margin: 28px 0 22px;
                        }
                        input[type="text"] {
                            width: 100%%;
                            padding: 16px 18px;
                            border-radius: 16px;
                            border: 1px solid var(--line);
                            background: #fdfefe;
                            color: var(--ink);
                            font: inherit;
                            font-size: 1.05rem;
                            transition: border-color 0.2s ease, box-shadow 0.2s ease;
                        }
                        input[type="text"]:focus {
                            outline: none;
                            border-color: var(--primary);
                            box-shadow: 0 0 0 4px rgba(3, 169, 244, 0.18);
                        }
                        button {
                            border: none;
                            border-radius: 16px;
                            padding: 16px 24px;
                            background: linear-gradient(135deg, var(--primary), var(--primary-dark));
                            color: var(--on-primary);
                            font: inherit;
                            font-weight: 700;
                            cursor: pointer;
                            transition: transform 0.15s ease, box-shadow 0.15s ease;
                        }
                        button:hover {
                            transform: translateY(-1px);
                            box-shadow: 0 10px 24px rgba(2, 136, 209, 0.22);
                        }
                        .meta {
                            display: flex;
                            flex-wrap: wrap;
                            gap: 14px;
                            margin: 8px 0 24px;
                            color: var(--muted);
                            font-size: 0.98rem;
                        }
                        .pill {
                            padding: 8px 12px;
                            border-radius: 999px;
                            background: var(--accent-soft);
                            border: 1px solid rgba(2, 136, 209, 0.18);
                        }
                        .results {
                            display: grid;
                            gap: 14px;
                        }
                        .card {
                            padding: 18px 18px 16px;
                            border-radius: 18px;
                            border: 1px solid var(--line);
                            background: #fcfeff;
                        }
                        .rank {
                            margin-bottom: 8px;
                            color: var(--primary-dark);
                            font-size: 0.85rem;
                            text-transform: uppercase;
                            letter-spacing: 0.12em;
                        }
                        .title {
                            margin: 0 0 8px;
                            font-size: 1.15rem;
                            word-break: break-word;
                        }
                        .title a {
                            color: var(--ink);
                            text-decoration: none;
                        }
                        .title a:hover {
                            text-decoration: underline;
                        }
                        .url {
                            margin: 0 0 8px;
                            color: var(--muted);
                            font-size: 0.95rem;
                            word-break: break-word;
                        }
                        .score {
                            color: var(--accent);
                            font-weight: 700;
                        }
                        .empty {
                            padding: 18px;
                            border-radius: 20px;
                            background: #fbfdff;
                            border: 1px dashed var(--line);
                            color: var(--muted);
                        }
                        @media (max-width: 720px) {
                            .shell {
                                margin: 16px auto;
                                padding: 20px;
                                border-radius: 20px;
                            }
                            form {
                                grid-template-columns: 1fr;
                            }
                            button {
                                width: 100%%;
                            }
                        }
                    </style>
                </head>
                <body>
                    <main class="shell">
                        <h1>Vector Search Engine</h1>
                        <form action="search" method="get">
                            <input type="text" name="q" placeholder="Enter a search query" value="%s">
                            <button type="submit">Search</button>
                        </form>
                """.formatted(escapeHtml(query)));

        if (query.isBlank()) {
            html.append("""
                        <div class="empty">Enter a query to see ranked search results.</div>
                    </main>
                </body>
                </html>
                """);
            return html.toString();
        }

        html.append("""
                    <div class="meta">
                        <div class="pill">Query: %s</div>
                        <div class="pill">Found: %d</div>
                        <div class="pill">Shown: %d of %d</div>
                    </div>
                """.formatted(escapeHtml(query), totalResults, Math.min(results.size(), topLimit), totalResults));

        if (results.isEmpty()) {
            html.append("""
                        <div class="empty">No matches found for this query.</div>
                    </main>
                </body>
                </html>
                """);
            return html.toString();
        }

        html.append("<section class=\"results\">");
        for (int i = 0; i < results.size(); i++) {
            SearchResult result = results.get(i);
            html.append("""
                        <article class="card">
                            <div class="rank">Result %d</div>
                            <h2 class="title"><a href="%s" target="_blank" rel="noreferrer">%s</a></h2>
                            <p class="url">%s</p>
                            <div class="score">Score: %s</div>
                        </article>
                    """.formatted(
                    i + 1,
                    escapeHtml(result.document().url()),
                    escapeHtml(result.document().fileName()),
                    escapeHtml(result.document().url()),
                    formatScore(result.score())
            ));
        }
        html.append("""
                    </section>
                </main>
                </body>
                </html>
                """);

        return html.toString();
    }

    public static String renderError(String query, String errorMessage) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Search Error</title>
                    <style>
                        body {
                            margin: 0;
                            min-height: 100vh;
                            display: grid;
                            place-items: center;
                            background: linear-gradient(135deg, #f8f3e8 0%%, #efe5d1 100%%);
                            color: #2f2a24;
                            font-family: Georgia, "Book Antiqua", "Times New Roman", serif;
                        }
                        .card {
                            width: min(760px, calc(100vw - 32px));
                            padding: 28px;
                            border-radius: 24px;
                            background: rgba(255,255,255,0.9);
                            border: 1px solid rgba(85, 59, 43, 0.16);
                            box-shadow: 0 16px 48px rgba(84, 55, 36, 0.12);
                        }
                        h1 { margin-top: 0; }
                        .muted { color: #6a6258; }
                        a { color: #9f452c; }
                        code {
                            display: block;
                            margin-top: 12px;
                            padding: 12px 14px;
                            border-radius: 14px;
                            background: #f7efe4;
                            white-space: pre-wrap;
                        }
                    </style>
                </head>
                <body>
                    <main class="card">
                        <h1>Failed to process request</h1>
                        <p class="muted">Query: %s</p>
                        <p>The server returned an internal error while processing the search.</p>
                        <code>%s</code>
                        <p><a href="./">Back to search</a></p>
                    </main>
                </body>
                </html>
                """.formatted(
                escapeHtml(query),
                escapeHtml(errorMessage == null || errorMessage.isBlank() ? "Unknown error" : errorMessage)
        );
    }

    private static String formatScore(double score) {
        return String.format(Locale.ROOT, "%.6f", score);
    }

    private static String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
