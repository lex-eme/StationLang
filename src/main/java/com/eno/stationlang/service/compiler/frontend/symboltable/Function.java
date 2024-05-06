package com.eno.stationlang.service.compiler.frontend.symboltable;

import com.eno.stationlang.service.compiler.frontend.StatType;
import java.util.ArrayList;
import java.util.List;

public class Function extends BaseScope implements Symbol {

  private final String name;
  private final StatType type;
  private final List<StatType> paramTypes;
  private final boolean isBuiltIn;

  public Function(String name, StatType type, Scope parent) {
    this(name, type, parent, false);
  }

  public Function(String name, StatType type, Scope parent, boolean isBuiltIn) {
    super(parent);
    this.name = name;
    this.type = type;
    this.paramTypes = new ArrayList<>();
    this.isBuiltIn = isBuiltIn;
  }

  public void addParameter(StatType type) {
    paramTypes.add(type);
  }

  public int arity() {
    return paramTypes.size();
  }

  public StatType getParamType(int index) {
    return paramTypes.get(index);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public StatType getType() {
    return type;
  }

  public boolean isBuiltIn() {
    return isBuiltIn;
  }
}
