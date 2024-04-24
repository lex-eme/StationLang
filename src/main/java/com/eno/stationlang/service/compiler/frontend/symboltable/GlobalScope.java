package com.eno.stationlang.service.compiler.frontend.symboltable;

import com.eno.stationlang.service.compiler.frontend.StatType;

public class GlobalScope extends BaseScope {

  public GlobalScope() {
    super();
    addBuiltInFunctions();
  }

  private void addBuiltInFunctions() {
    define(createFunction("readNumber", StatType.NUMBER, this, StatType.NUMBER, StatType.PROPERTY));
    define(
        createFunction("readBoolean", StatType.BOOLEAN, this, StatType.NUMBER, StatType.PROPERTY));
    define(
        createFunction(
            "writeNumber",
            StatType.VOID,
            this,
            StatType.NUMBER,
            StatType.PROPERTY,
            StatType.NUMBER));
    define(
        createFunction(
            "writeBoolean",
            StatType.VOID,
            this,
            StatType.NUMBER,
            StatType.PROPERTY,
            StatType.BOOLEAN));
  }

  private Function createFunction(
      String name, StatType returnType, Scope scope, StatType... argTypes) {
    Function func = new Function(name, returnType, scope);
    for (var type : argTypes) {
      func.addParameter(type);
    }
    return func;
  }
}
