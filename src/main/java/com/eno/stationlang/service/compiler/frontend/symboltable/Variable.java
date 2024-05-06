package com.eno.stationlang.service.compiler.frontend.symboltable;

import com.eno.stationlang.service.compiler.frontend.StatType;

public class Variable implements Symbol {

  private final String name;
  private final StatType type;
  private int indexInScope;

  public Variable(String name, StatType type) {
    this(name, type, 0);
  }

  public Variable(String name, StatType type, int indexInScope) {
    this.name = name;
    this.type = type;
    this.indexInScope = indexInScope;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public StatType getType() {
    return type;
  }

  void setIndexInScope(int index) {
    indexInScope = index;
  }

  public int getIndexInScope() {
    return indexInScope;
  }
}
