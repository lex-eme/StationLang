package com.eno.stationlang.service.compiler.frontend.symboltable;

public interface Scope {

  void define(Symbol symbol);

  Symbol resolve(String name);

  Scope getParentScope();
}
