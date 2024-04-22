package com.eno.stationlang.service.compiler.frontend.symboltable;

import com.eno.stationlang.service.compiler.frontend.StatType;

public class Variable implements Symbol {

  private final String name;
  private final StatType type;

  public Variable(String name, StatType type) {
    this.name = name;
    this.type = type;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public StatType getType() {
    return type;
  }
}
