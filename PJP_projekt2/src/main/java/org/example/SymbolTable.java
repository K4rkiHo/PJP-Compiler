package org.example;

import org.antlr.v4.runtime.Token;

import java.util.HashMap;

public class SymbolTable {
    private final HashMap<String, Entry> memory = new HashMap<>();

    public void add(Token variable, Type type) {
        var name = variable.getText().trim();
        if (memory.containsKey(name)) {
            Errors.reportError(variable, "Variable " + name + " was already declared.");
        } else {
            switch (type) {
                case Int -> memory.put(name, new Entry(Type.Int));
                case Boolean -> memory.put(name, new Entry(Type.Boolean));
                case String -> memory.put(name, new Entry(Type.String));
                case Float -> memory.put(name, new Entry(Type.Float));
            }
        }
    }

    public Entry get(Token variable) {
        var name = variable.getText().trim();

        if (memory.containsKey(name)) {
            return memory.get(name);
        } else {
            Errors.reportError(variable, "Variable " + name + " was NOT declared.");
            return new Entry(Type.Error);
        }
    }

    public void set(Token symbol, Entry value) {
        var name = symbol.getText().trim();
        memory.put(name, value);
    }

    public static class Entry {
        Type type;
        public Entry(Type type) {
            this.type = type;
        }
    }
}
