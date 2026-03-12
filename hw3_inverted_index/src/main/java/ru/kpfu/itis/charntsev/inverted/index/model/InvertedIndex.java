package ru.kpfu.itis.charntsev.inverted.index.model;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class InvertedIndex {

    private final SortedMap<String, SortedSet<String>> postings;
    private final SortedMap<String, DocumentInfo> documents;

    public InvertedIndex(Map<String, ? extends Set<String>> postings,
                         Map<String, DocumentInfo> documents) {
        this.postings = new TreeMap<>();
        for (Map.Entry<String, ? extends Set<String>> entry : postings.entrySet()) {
            this.postings.put(entry.getKey(), new TreeSet<>(entry.getValue()));
        }

        this.documents = new TreeMap<>(documents);
    }

    public SortedSet<String> getPostings(String term) {
        SortedSet<String> docs = postings.get(term);
        return docs == null ? new TreeSet<>() : new TreeSet<>(docs);
    }

    public SortedSet<String> allDocumentIds() {
        return new TreeSet<>(documents.keySet());
    }

    public DocumentInfo getDocument(String documentId) {
        return documents.get(documentId);
    }

    public SortedMap<String, SortedSet<String>> postings() {
        SortedMap<String, SortedSet<String>> copy = new TreeMap<>();
        for (Map.Entry<String, SortedSet<String>> entry : postings.entrySet()) {
            copy.put(entry.getKey(), new TreeSet<>(entry.getValue()));
        }
        return Collections.unmodifiableSortedMap(copy);
    }

    public SortedMap<String, DocumentInfo> documents() {
        return Collections.unmodifiableSortedMap(documents);
    }
}
