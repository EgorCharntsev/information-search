package ru.kpfu.itis.charntsev.search.engine.web;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.kpfu.itis.charntsev.search.engine.io.TfIdfCorpusLoader;
import ru.kpfu.itis.charntsev.search.engine.io.DocumentCatalogLoader;
import ru.kpfu.itis.charntsev.search.engine.model.SearchIndex;
import ru.kpfu.itis.charntsev.search.engine.model.DocumentInfo;
import ru.kpfu.itis.charntsev.search.engine.model.SearchResult;
import ru.kpfu.itis.charntsev.search.engine.search.VectorSearchEngine;
import ru.kpfu.itis.charntsev.search.engine.nlp.RussianTextProcessor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/", "/search"}, loadOnStartup = 1)
public class SearchServlet extends HttpServlet {

    private static final String PROJECT_ROOT_PROPERTY = "search.projectRoot";
    private static final String TFIDF_DIR_PROPERTY = "search.tfidf.dir";
    private static final String INDEX_PATH_PROPERTY = "search.index.path";
    private static final String TOP_LIMIT_PROPERTY = "search.topLimit";

    private volatile VectorSearchEngine searchEngine;
    private volatile int topLimit;

    @Override
    public void init() throws ServletException {
        try {
            ServletContext context = getServletContext();

            Path projectRoot = resolveProjectRoot();
            Path tfIdfDir = resolvePath(TFIDF_DIR_PROPERTY, projectRoot.resolve("hw4_tf_idf/output/lemmas"));
            Path crawlerIndexPath = resolvePath(INDEX_PATH_PROPERTY, projectRoot.resolve("hw1_crawler/output/index.txt"));

            validateInputs(tfIdfDir, crawlerIndexPath);

            Map<String, DocumentInfo> documents = new DocumentCatalogLoader().load(crawlerIndexPath);
            if (documents.isEmpty()) {
                throw new IllegalStateException("The file hw1_crawler/output/index.txt is empty or has an incorrect format!");
            }

            SearchIndex index = new TfIdfCorpusLoader().load(tfIdfDir, documents);
            this.searchEngine = new VectorSearchEngine(index, new RussianTextProcessor());
            this.topLimit = resolveTopLimit(context);

            context.setAttribute("search.projectRoot", projectRoot.toString());
            context.setAttribute("search.tfidf.dir", tfIdfDir.toString());
            context.setAttribute("search.index.path", crawlerIndexPath.toString());
        } catch (Exception e) {
            throw new UnavailableException("Failed to initialize search engine: " + e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handleSearch(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handleSearch(req, resp);
    }

    private void handleSearch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");

        String query = normalize(req.getParameter("q"));

        try {
            List<SearchResult> allResults = query.isBlank() ? List.of() : searchEngine.search(query);
            List<SearchResult> shownResults = allResults.size() <= topLimit
                    ? allResults
                    : List.copyOf(allResults.subList(0, topLimit));

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(SearchPageRenderer.render(query, shownResults, allResults.size(), topLimit));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(SearchPageRenderer.renderError(query, e.getMessage()));
        }
    }

    private Path resolveProjectRoot() {
        String configured = System.getProperty(PROJECT_ROOT_PROPERTY);
        if (configured != null && !configured.isBlank()) {
            return Paths.get(configured.trim()).toAbsolutePath().normalize();
        }
        return Paths.get("").toAbsolutePath().normalize();
    }

    private Path resolvePath(String propertyName, Path defaultPath) {
        String configured = System.getProperty(propertyName);
        if (configured != null && !configured.isBlank()) {
            return Paths.get(configured.trim()).toAbsolutePath().normalize();
        }
        return defaultPath.toAbsolutePath().normalize();
    }

    private void validateInputs(Path tfIdfDir, Path crawlerIndexPath) {
        if (!Files.isDirectory(tfIdfDir)) {
            throw new IllegalStateException("The directory with TF-IDF vectors was not found: " + tfIdfDir);
        }
        if (!Files.exists(crawlerIndexPath)) {
            throw new IllegalStateException("The page list file was not found: " + crawlerIndexPath);
        }
    }

    private int resolveTopLimit(ServletContext context) {
        String configured = System.getProperty(TOP_LIMIT_PROPERTY);
        if (configured == null || configured.isBlank()) {
            configured = context.getInitParameter(TOP_LIMIT_PROPERTY);
        }
        if (configured == null || configured.isBlank()) {
            return 10;
        }

        try {
            int value = Integer.parseInt(configured.trim());
            return value > 0 ? value : 10;
        } catch (NumberFormatException e) {
            return 10;
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
