package org.example;

import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;

public class Errors {
    private static final List<String> ErrorsData = new ArrayList<>();
    public static void reportError(Token token, String message) {
        ErrorsData.add(token.getLine() + ":" + token.getCharPositionInLine() + " - " + message);
    }
    public static int getNumberOfErrors() {
        return ErrorsData.size();
    }
    public static void printAndClearErrors() {
        for (var error : ErrorsData) {
            System.err.println(error);
        }
        ErrorsData.clear();
    }
}
