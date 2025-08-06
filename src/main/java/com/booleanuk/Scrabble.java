package com.booleanuk;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Scrabble {
    private final String word;

    private static final Map<Character, Integer> letterScores = new HashMap<>();

    static {
        for (char c : "AEIOULNRST".toCharArray()) letterScores.put(c, 1);
        for (char c : "DG".toCharArray()) letterScores.put(c, 2);
        for (char c : "BCMP".toCharArray()) letterScores.put(c, 3);
        for (char c : "FHVWY".toCharArray()) letterScores.put(c, 4);
        for (char c : "K".toCharArray()) letterScores.put(c, 5);
        for (char c : "JX".toCharArray()) letterScores.put(c, 8);
        for (char c : "QZ".toCharArray()) letterScores.put(c, 10);
    }

    public Scrabble(String word) {
        this.word = word;
    }

    public int score() {
        if (word == null || word.trim().isEmpty()) {
            return 0;
        }

        String input = word.toUpperCase();
        return scoreRecursive(input, 0, input.length()).score;
    }

    private Result scoreRecursive(String input, int start, int end) {
        int totalScore = 0;
        int i = start;
        Stack<Integer> wordMultipliers = new Stack<>();
        wordMultipliers.push(1);

        while (i < end) {
            char c = input.charAt(i);

            if (c == '{' || c == '[') {

                char closing = (c == '{') ? '}' : ']';
                int closeIndex = findClosing(input, i, end, c, closing);
                if (closeIndex == -1) return new Result(0, end);

                String inside = input.substring(i + 1, closeIndex);

                if (inside.isEmpty()) return new Result(0, end);

                int multiplier = (c == '{') ? 2 : 3;


                if (inside.length() == 1) {
                    char letter = inside.charAt(0);
                    if (!letterScores.containsKey(letter)) return new Result(0, end);
                    totalScore += letterScores.get(letter) * multiplier;
                } else {

                    Result innerResult = scoreRecursive(inside, 0, inside.length());
                    if (innerResult.score == 0) return new Result(0, end);
                    totalScore += innerResult.score * multiplier;
                }

                i = closeIndex + 1;
            } else if (c == '(' || c == '<') {

                char closing = (c == '(') ? ')' : '>';
                int closeIndex = findClosing(input, i, end, c, closing);
                if (closeIndex == -1) return new Result(0, end);

                String inside = input.substring(i + 1, closeIndex);
                if (inside.isEmpty()) return new Result(0, end);

                int multiplier = (c == '(') ? 2 : 3;

                Result innerResult = scoreRecursive(inside, 0, inside.length());
                if (innerResult.score == 0) return new Result(0, end);
                totalScore += innerResult.score * multiplier;

                i = closeIndex + 1;
            } else {

                if (!letterScores.containsKey(c)) return new Result(0, end);
                totalScore += letterScores.get(c);
                i++;
            }
        }

        return new Result(totalScore, i);
    }

    private int findClosing(String input, int start, int end, char openChar, char closeChar) {
        int depth = 0;
        for (int i = start; i < end; i++) {
            char c = input.charAt(i);
            if (c == openChar) depth++;
            else if (c == closeChar) {
                depth--;
                if (depth == 0) return i;
            }
        }
        return -1;
    }

    private static class Result {
        int score;
        int nextIndex;

        Result(int score, int nextIndex) {
            this.score = score;
            this.nextIndex = nextIndex;
        }
    }
}
