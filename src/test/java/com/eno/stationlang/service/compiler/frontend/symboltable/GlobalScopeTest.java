package com.eno.stationlang.service.compiler.frontend.symboltable;

import static org.junit.jupiter.api.Assertions.*;

import com.eno.stationlang.service.compiler.frontend.StatType;
import org.junit.jupiter.api.Test;

class GlobalScopeTest {

  @Test
  void readNumberIsDefinedTest() {
    GlobalScope scope = new GlobalScope();

    Symbol symbol = scope.resolve("readNumber");
    assertNotNull(symbol);
    Function function = assertInstanceOf(Function.class, symbol);
    assertEquals(StatType.NUMBER, function.getType());
    assertEquals(2, function.arity());
    assertEquals(StatType.NUMBER, function.getParamType(0));
    assertEquals(StatType.PROPERTY, function.getParamType(1));
  }

  @Test
  void readBooleanIsDefinedTest() {
    GlobalScope scope = new GlobalScope();

    Symbol symbol = scope.resolve("readBoolean");
    assertNotNull(symbol);
    Function function = assertInstanceOf(Function.class, symbol);
    assertEquals(StatType.BOOLEAN, function.getType());
    assertEquals(2, function.arity());
    assertEquals(StatType.NUMBER, function.getParamType(0));
    assertEquals(StatType.PROPERTY, function.getParamType(1));
  }

  @Test
  void writeNumberIsDefinedTest() {
    GlobalScope scope = new GlobalScope();

    Symbol symbol = scope.resolve("writeNumber");
    assertNotNull(symbol);
    Function function = assertInstanceOf(Function.class, symbol);
    assertEquals(StatType.VOID, function.getType());
    assertEquals(3, function.arity());
    assertEquals(StatType.NUMBER, function.getParamType(0));
    assertEquals(StatType.PROPERTY, function.getParamType(1));
    assertEquals(StatType.NUMBER, function.getParamType(2));
  }

  @Test
  void writeBooleanIsDefinedTest() {
    GlobalScope scope = new GlobalScope();

    Symbol symbol = scope.resolve("writeBoolean");
    assertNotNull(symbol);
    Function function = assertInstanceOf(Function.class, symbol);
    assertEquals(StatType.VOID, function.getType());
    assertEquals(3, function.arity());
    assertEquals(StatType.NUMBER, function.getParamType(0));
    assertEquals(StatType.PROPERTY, function.getParamType(1));
    assertEquals(StatType.BOOLEAN, function.getParamType(2));
  }
}
