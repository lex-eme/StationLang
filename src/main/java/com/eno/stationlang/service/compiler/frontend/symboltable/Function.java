package com.eno.stationlang.service.compiler.frontend.symboltable;

import com.eno.stationlang.service.compiler.frontend.StatType;

public class Function extends BaseScope implements Symbol {

  private final String name;
  private final StatType type;

  public Function(String name, StatType type, Scope parent) {
    super(parent);
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
