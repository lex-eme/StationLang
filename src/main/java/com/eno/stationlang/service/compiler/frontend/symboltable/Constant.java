package com.eno.stationlang.service.compiler.frontend.symboltable;

import com.eno.stationlang.service.compiler.frontend.StatType;

public class Constant implements Symbol {
  private final String name;
  private final StatType type;
  private final String value;

  public Constant(String name, StatType type, String value) {
    this.name = name;
    this.type = type;
    this.value = value;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public StatType getType() {
    return type;
  }

  public String getValue() {
    return value;
  }
}
