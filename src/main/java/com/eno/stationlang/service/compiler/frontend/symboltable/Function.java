package com.eno.stationlang.service.compiler.frontend.symboltable;

import com.eno.stationlang.service.compiler.frontend.StatType;
import java.util.ArrayList;
import java.util.List;

public class Function extends BaseScope implements Symbol {

  private final String name;
  private final StatType type;
  private final List<StatType> paramTypes;

  public Function(String name, StatType type, Scope parent) {
    super(parent);
    this.name = name;
    this.type = type;
    paramTypes = new ArrayList<>();
  }

  public void addParameter(StatType type) {
    paramTypes.add(type);
  }

  public int arity() {
    return paramTypes.size();
  }

  public List<StatType> getParamTypes() {
    return paramTypes;
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
