package ru.kpfu.itis.charntsev.inverted.index.search;

import ru.kpfu.itis.charntsev.inverted.index.core.InvertedIndexBuilder;
import ru.kpfu.itis.charntsev.inverted.index.model.InvertedIndex;
import ru.kpfu.itis.charntsev.inverted.index.query.BooleanQueryParser;
import ru.kpfu.itis.charntsev.inverted.index.query.QueryToken;
import ru.kpfu.itis.charntsev.inverted.index.query.TokenType;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class BooleanSearchEngine {

    private final InvertedIndex index;
    private final InvertedIndexBuilder indexBuilder;
    private final BooleanQueryParser parser;

    public BooleanSearchEngine(InvertedIndex index,
                               InvertedIndexBuilder indexBuilder,
                               BooleanQueryParser parser) {
        this.index = index;
        this.indexBuilder = indexBuilder;
        this.parser = parser;
    }

    public Set<String> search(String query) throws IOException {
        List<QueryToken> postfix = parser.toPostfix(query);
        Deque<Set<String>> stack = new ArrayDeque<>();
        Set<String> allDocuments = index.allDocumentIds();

        for (QueryToken token : postfix) {
            switch (token.type()) {
                case TERM -> stack.push(resolveTerm(token.value()));
                case NOT -> {
                    ensureStackSize(stack, 1, token.type());
                    Set<String> operand = stack.pop();
                    stack.push(difference(allDocuments, operand));
                }
                case AND -> {
                    ensureStackSize(stack, 2, token.type());
                    Set<String> right = stack.pop();
                    Set<String> left = stack.pop();
                    stack.push(intersection(left, right));
                }
                case OR -> {
                    ensureStackSize(stack, 2, token.type());
                    Set<String> right = stack.pop();
                    Set<String> left = stack.pop();
                    stack.push(union(left, right));
                }
                default -> throw new IllegalStateException("Unsupported token: " + token.type());
            }
        }

        if (stack.size() != 1) {
            throw new IllegalArgumentException("Couldn't calculate the search query");
        }

        return new TreeSet<>(stack.pop());
    }

    private Set<String> resolveTerm(String rawTerm) throws IOException {
        Set<String> result = new TreeSet<>();

        if (rawTerm == null || rawTerm.isBlank()) {
            return result;
        }

        String rawNormalized = rawTerm.trim().toLowerCase().replace('ё', 'е');
        result.addAll(index.getPostings(rawNormalized));

        String lemma = indexBuilder.normalizeToTerm(rawTerm);
        if (lemma != null) {
            result.addAll(index.getPostings(lemma));
        }

        return result;
    }

    private void ensureStackSize(Deque<Set<String>> stack, int expected, TokenType operator) {
        if (stack.size() < expected) {
            throw new IllegalArgumentException("There are not enough operands for the operator " + operator);
        }
    }

    private Set<String> intersection(Set<String> left, Set<String> right) {
        Set<String> result = new TreeSet<>(left);
        result.retainAll(right);
        return result;
    }

    private Set<String> union(Set<String> left, Set<String> right) {
        Set<String> result = new TreeSet<>(left);
        result.addAll(right);
        return result;
    }

    private Set<String> difference(Set<String> left, Set<String> right) {
        Set<String> result = new TreeSet<>(left);
        result.removeAll(right);
        return result;
    }
}
