package ru.kpfu.itis.charntsev.inverted.index.query;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Locale;

public class BooleanQueryParser {

    public List<QueryToken> toPostfix(String query) {
        List<QueryToken> tokens = tokenize(query);
        validate(tokens);

        List<QueryToken> output = new ArrayList<>();
        Deque<QueryToken> operators = new ArrayDeque<>();

        for (QueryToken token : tokens) {
            switch (token.type()) {
                case TERM -> output.add(token);
                case AND, OR, NOT -> {
                    while (!operators.isEmpty()
                            && operators.peek().type() != TokenType.LEFT_PAREN
                            && (precedence(operators.peek()) > precedence(token)
                            || (precedence(operators.peek()) == precedence(token)
                            && !isRightAssociative(token)))) {
                        output.add(operators.pop());
                    }
                    operators.push(token);
                }
                case LEFT_PAREN -> operators.push(token);
                case RIGHT_PAREN -> {
                    boolean foundLeftParen = false;
                    while (!operators.isEmpty()) {
                        QueryToken top = operators.pop();
                        if (top.type() == TokenType.LEFT_PAREN) {
                            foundLeftParen = true;
                            break;
                        }
                        output.add(top);
                    }
                    if (!foundLeftParen) {
                        throw new IllegalArgumentException("Inconsistent parentheses in the query");
                    }
                }
            }
        }

        while (!operators.isEmpty()) {
            QueryToken token = operators.pop();
            if (token.type() == TokenType.LEFT_PAREN || token.type() == TokenType.RIGHT_PAREN) {
                throw new IllegalArgumentException("Inconsistent parentheses in the query");
            }
            output.add(token);
        }

        return output;
    }

    private List<QueryToken> tokenize(String query) {
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("An empty search query");
        }

        List<QueryToken> tokens = new ArrayList<>();
        int i = 0;
        while (i < query.length()) {
            char ch = query.charAt(i);

            if (Character.isWhitespace(ch)) {
                i++;
                continue;
            }

            if (ch == '(') {
                tokens.add(new QueryToken(TokenType.LEFT_PAREN, "("));
                i++;
                continue;
            }
            if (ch == ')') {
                tokens.add(new QueryToken(TokenType.RIGHT_PAREN, ")"));
                i++;
                continue;
            }
            if (ch == '!') {
                tokens.add(new QueryToken(TokenType.NOT, "NOT"));
                i++;
                continue;
            }

            if (i + 1 < query.length()) {
                String two = query.substring(i, i + 2);
                if ("&&".equals(two)) {
                    tokens.add(new QueryToken(TokenType.AND, "AND"));
                    i += 2;
                    continue;
                }
                if ("||".equals(two)) {
                    tokens.add(new QueryToken(TokenType.OR, "OR"));
                    i += 2;
                    continue;
                }
            }

            int start = i;
            while (i < query.length()) {
                char current = query.charAt(i);
                if (Character.isWhitespace(current) || current == '(' || current == ')' || current == '!') {
                    break;
                }
                if (i + 1 < query.length()) {
                    String two = query.substring(i, i + 2);
                    if ("&&".equals(two) || "||".equals(two)) {
                        break;
                    }
                }
                i++;
            }

            String raw = query.substring(start, i);
            String upper = raw.toUpperCase(Locale.ROOT);
            if ("AND".equals(upper)) {
                tokens.add(new QueryToken(TokenType.AND, "AND"));
            } else if ("OR".equals(upper)) {
                tokens.add(new QueryToken(TokenType.OR, "OR"));
            } else if ("NOT".equals(upper)) {
                tokens.add(new QueryToken(TokenType.NOT, "NOT"));
            } else {
                tokens.add(new QueryToken(TokenType.TERM, raw));
            }
        }

        return tokens;
    }

    private void validate(List<QueryToken> tokens) {
        if (tokens.isEmpty()) {
            throw new IllegalArgumentException("An empty search query");
        }

        boolean expectingOperand = true;
        int balance = 0;

        for (QueryToken token : tokens) {
            switch (token.type()) {
                case TERM -> {
                    if (!expectingOperand) {
                        throw new IllegalArgumentException("There must be a logical operator between the terms.");
                    }
                    expectingOperand = false;
                }
                case NOT -> {
                    if (!expectingOperand) {
                        throw new IllegalArgumentException("An AND/OR operator or an opening parenthesis is needed before NOT.");
                    }
                }
                case AND, OR -> {
                    if (expectingOperand) {
                        throw new IllegalArgumentException("Incorrect placement of operators in the request");
                    }
                    expectingOperand = true;
                }
                case LEFT_PAREN -> {
                    if (!expectingOperand) {
                        throw new IllegalArgumentException("An operator is needed before the opening parenthesis.");
                    }
                    balance++;
                }
                case RIGHT_PAREN -> {
                    if (expectingOperand) {
                        throw new IllegalArgumentException("Empty or incorrect parentheses in the query");
                    }
                    balance--;
                    if (balance < 0) {
                        throw new IllegalArgumentException("Inconsistent parentheses in the query");
                    }
                }
            }
        }

        if (balance != 0) {
            throw new IllegalArgumentException("Inconsistent parentheses in the query");
        }

        if (expectingOperand) {
            throw new IllegalArgumentException("The request ends with the operator");
        }
    }

    private boolean isBinaryOperator(QueryToken token) {
        return token.type() == TokenType.AND || token.type() == TokenType.OR;
    }

    private boolean isTermLike(QueryToken token) {
        return token.type() == TokenType.TERM
                || token.type() == TokenType.LEFT_PAREN
                || token.type() == TokenType.NOT
                || token.type() == TokenType.RIGHT_PAREN;
    }

    private int precedence(QueryToken token) {
        return switch (token.type()) {
            case NOT -> 3;
            case AND -> 2;
            case OR -> 1;
            default -> 0;
        };
    }

    private boolean isRightAssociative(QueryToken token) {
        return token.type() == TokenType.NOT;
    }
}
