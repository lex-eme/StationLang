package com.eno.stationlang.service.compiler.frontend.symboltable;

import java.util.HashMap;
import java.util.Map;

public class BaseScope implements Scope {

    private final Scope parent;
    private final Map<String, Symbol> symbols;

    public BaseScope(Scope parent) {
        this.parent = parent;
        this.symbols = new HashMap<>();
    }

    @Override
    public void define(Symbol symbol) {
        if (symbols.get(symbol.getName()) != null) {
            throw new RuntimeException("Symbol '" + symbol.getName() + "' already exists in scope.");
        }

        symbols.put(symbol.getName(), symbol);
    }

    @Override
    public Symbol resolve(String name) {
        if (symbols.containsKey(name)) {
            return symbols.get(name);
        }

        if (parent != null) {
            return parent.resolve(name);
        }

        return null;
    }

    @Override
    public Scope getParentScope() {
        return parent;
    }
}
