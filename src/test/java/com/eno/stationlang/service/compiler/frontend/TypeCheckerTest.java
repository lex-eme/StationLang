package com.eno.stationlang.service.compiler.frontend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.eno.stationlang.parser.StatParser;
import com.eno.stationlang.service.compiler.error.SemanticErrorListener;
import com.eno.stationlang.service.compiler.frontend.symboltable.Function;
import com.eno.stationlang.service.compiler.frontend.symboltable.Scope;
import com.eno.stationlang.service.compiler.frontend.symboltable.Variable;
import java.util.List;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class TypeCheckerTest {

  ParseTreeProperty<StatType> types = new ParseTreeProperty<>();
  SemanticErrorListener listener = mock(SemanticErrorListener.class);
  Scope scope = mock(Scope.class);

  @ParameterizedTest
  @EnumSource(StatType.class)
  void exitParenExprTest(StatType type) {
    StatParser.ExpressionContext subExpr = mockSubExpr(type);
    StatParser.ParenExprContext expr = mock(StatParser.ParenExprContext.class);
    when(expr.expression()).thenReturn(subExpr);

    new TypeChecker(types, listener, scope).exitParenExpr(expr);

    assertEquals(type, types.get(expr));
  }

  @Test
  void exitVarExprTest() {
    StatParser.VarExprContext expr = mock(StatParser.VarExprContext.class);
    TerminalNode node = mock(TerminalNode.class);
    when(node.getText()).thenReturn("variable");
    when(expr.ID()).thenReturn(node);
    when(scope.resolve("variable")).thenReturn(new Variable("variable", StatType.NUMBER));

    new TypeChecker(types, listener, scope).exitVarExpr(expr);

    assertEquals(StatType.NUMBER, types.get(expr));
  }

  @Test
  void exitVarExprVariableNotDefinedTest() {
    StatParser.VarExprContext expr = mockExpr(StatParser.VarExprContext.class);
    TerminalNode node = mock(TerminalNode.class);
    when(node.getText()).thenReturn("variable");
    when(expr.ID()).thenReturn(node);

    new TypeChecker(types, listener, scope).exitVarExpr(expr);

    verify(listener).semanticError(1, 1, "variable 'variable' is not defined.");
    assertEquals(StatType.VOID, types.get(expr));
  }

  @Test
  void exitCallExprFunctionNotDefinedTest() {
    StatParser.CallExprContext expr = mockExpr(StatParser.CallExprContext.class);
    TerminalNode node = mock(TerminalNode.class);
    when(node.getText()).thenReturn("function");
    when(expr.ID()).thenReturn(node);

    new TypeChecker(types, listener, scope).exitCallExpr(expr);

    verify(listener).semanticError(1, 1, "function 'function' is not defined.");
    assertEquals(StatType.VOID, types.get(expr));
  }

  @Test
  void exitCallExprWrongNumberOfArguments() {
    StatParser.ExpressionContext subExpr = mockSubExpr(StatType.NUMBER);
    List<StatParser.ExpressionContext> list = List.of(subExpr);
    StatParser.CallExprContext expr = mockExpr(StatParser.CallExprContext.class, 10, 5);
    when(expr.expression()).thenReturn(list);

    TerminalNode node = mock(TerminalNode.class);
    when(node.getText()).thenReturn("function");
    when(expr.ID()).thenReturn(node);
    Function function = new Function("function", StatType.NUMBER, scope);
    when(scope.resolve("function")).thenReturn(function);

    new TypeChecker(types, listener, scope).exitCallExpr(expr);

    verify(listener).semanticError(10, 5, "function 'function' expects 0 arguments but found 1.");
    assertEquals(StatType.NUMBER, types.get(expr));
  }

  @Test
  void exitCallExprArgumentTypesDoNotMatch() {
    StatParser.ExpressionContext subExpr1 = mockSubExpr(StatType.BOOLEAN, 1, 10);
    StatParser.ExpressionContext subExpr2 = mockSubExpr(StatType.NUMBER, 1, 15);
    List<StatParser.ExpressionContext> list = List.of(subExpr1, subExpr2);
    StatParser.CallExprContext expr = mockExpr(StatParser.CallExprContext.class, 10, 1);
    when(expr.expression()).thenReturn(list);
    TerminalNode node = mock(TerminalNode.class);
    when(node.getText()).thenReturn("function");
    when(expr.ID()).thenReturn(node);

    Function function = new Function("function", StatType.NUMBER, scope);
    function.addParameter(StatType.NUMBER);
    function.addParameter(StatType.BOOLEAN);
    when(scope.resolve("function")).thenReturn(function);

    new TypeChecker(types, listener, scope).exitCallExpr(expr);

    verify(listener).semanticError(1, 10, "argument must be of type NUMBER but found BOOLEAN.");
    verify(listener).semanticError(1, 15, "argument must be of type BOOLEAN but found NUMBER.");
    assertEquals(StatType.NUMBER, types.get(expr));
  }

  @ParameterizedTest
  @EnumSource(
      value = StatType.class,
      names = {"BOOLEAN", "VOID", "NUMBER"})
  void exitCallExprValidArguments(StatType type) {
    StatParser.ExpressionContext subExpr1 = mockSubExpr(StatType.NUMBER, 1, 10);
    StatParser.ExpressionContext subExpr2 = mockSubExpr(StatType.BOOLEAN, 1, 15);
    List<StatParser.ExpressionContext> list = List.of(subExpr1, subExpr2);
    StatParser.CallExprContext expr = mockExpr(StatParser.CallExprContext.class, 10, 1);
    when(expr.expression()).thenReturn(list);
    TerminalNode node = mock(TerminalNode.class);
    when(node.getText()).thenReturn("function");
    when(expr.ID()).thenReturn(node);

    Function function = new Function("function", type, scope);
    function.addParameter(StatType.NUMBER);
    function.addParameter(StatType.BOOLEAN);
    when(scope.resolve("function")).thenReturn(function);

    new TypeChecker(types, listener, scope).exitCallExpr(expr);

    verify(listener, times(0)).semanticError(anyInt(), anyInt(), anyString());
    assertEquals(type, types.get(expr));
  }

  @ParameterizedTest
  @EnumSource(
      value = StatType.class,
      names = {"BOOLEAN", "NUMBER"})
  void exitCallExprSymbolIsNotAFunction(StatType type) {
    StatParser.CallExprContext expr = mockExpr(StatParser.CallExprContext.class, 10, 1);
    TerminalNode node = mock(TerminalNode.class);
    when(node.getText()).thenReturn("name");
    when(expr.ID()).thenReturn(node);

    Variable variable = new Variable("name", type);
    when(scope.resolve("name")).thenReturn(variable);

    new TypeChecker(types, listener, scope).exitCallExpr(expr);

    verify(listener).semanticError(10, 1, "can only call functions.");
    assertEquals(type, types.get(expr));
  }

  @ParameterizedTest
  @EnumSource(
      value = StatType.class,
      names = {"BOOLEAN", "VOID", "PROPERTY"})
  void exitUnaryMinusExprSubExprInvalidTypeTest(StatType type) {
    StatParser.ExpressionContext subExpr = mockSubExpr(type);
    StatParser.UnaryMinusExprContext expr = mock(StatParser.UnaryMinusExprContext.class);
    when(expr.expression()).thenReturn(subExpr);

    new TypeChecker(types, listener, scope).exitUnaryMinusExpr(expr);

    verify(listener).semanticError(1, 1, "expression following '-' must be of type NUMBER.");
  }

  @ParameterizedTest
  @EnumSource(
      value = StatType.class,
      names = {"NUMBER"})
  void exitUnaryMinusExprSubExprValidTypeTest(StatType type) {
    StatParser.ExpressionContext subExpr = mockSubExpr(type);
    StatParser.UnaryMinusExprContext expr = mock(StatParser.UnaryMinusExprContext.class);
    when(expr.expression()).thenReturn(subExpr);

    new TypeChecker(types, listener, scope).exitUnaryMinusExpr(expr);

    verify(listener, times(0)).semanticError(anyInt(), anyInt(), anyString());
    assertEquals(StatType.NUMBER, types.get(expr));
  }

  @ParameterizedTest
  @EnumSource(
      value = StatType.class,
      names = {"NUMBER"})
  void exitMultDivideExprValidTypeTest(StatType type) {
    StatParser.ExpressionContext leftSubExpr = mockSubExpr(type);
    StatParser.ExpressionContext rightSubExpr = mockSubExpr(type);
    StatParser.MultDivideExprContext expr = mock(StatParser.MultDivideExprContext.class);
    expr.leftOperand = leftSubExpr;
    expr.rightOperand = rightSubExpr;

    new TypeChecker(types, listener, scope).exitMultDivideExpr(expr);

    verify(listener, times(0)).semanticError(anyInt(), anyInt(), anyString());
    assertEquals(StatType.NUMBER, types.get(expr));
  }

  @ParameterizedTest
  @EnumSource(
      value = StatType.class,
      names = {"BOOLEAN", "VOID", "PROPERTY"})
  void exitMultDivideExprInvalidTypeTest(StatType type) {
    Token operator = mock(Token.class);
    when(operator.getText()).thenReturn("op");
    StatParser.ExpressionContext leftSubExpr = mockSubExpr(type, 1, 1);
    StatParser.ExpressionContext rightSubExpr = mockSubExpr(type, 2, 10);
    StatParser.MultDivideExprContext expr = mock(StatParser.MultDivideExprContext.class);
    expr.leftOperand = leftSubExpr;
    expr.rightOperand = rightSubExpr;
    expr.operator = operator;

    new TypeChecker(types, listener, scope).exitMultDivideExpr(expr);

    verify(listener).semanticError(1, 1, "left operand of 'op' expression must be of type NUMBER.");
    verify(listener)
        .semanticError(2, 10, "right operand of 'op' expression must be of type NUMBER.");
    assertEquals(StatType.NUMBER, types.get(expr));
  }

  @ParameterizedTest
  @EnumSource(
      value = StatType.class,
      names = {"NUMBER"})
  void exitAddSubExprValidTypeTest(StatType type) {
    StatParser.ExpressionContext leftSubExpr = mockSubExpr(type);
    StatParser.ExpressionContext rightSubExpr = mockSubExpr(type);
    StatParser.AddSubExprContext expr = mock(StatParser.AddSubExprContext.class);
    expr.leftOperand = leftSubExpr;
    expr.rightOperand = rightSubExpr;

    new TypeChecker(types, listener, scope).exitAddSubExpr(expr);

    verify(listener, times(0)).semanticError(anyInt(), anyInt(), anyString());
    assertEquals(StatType.NUMBER, types.get(expr));
  }

  @ParameterizedTest
  @EnumSource(
      value = StatType.class,
      names = {"BOOLEAN", "VOID", "PROPERTY"})
  void exitAddSubExprInvalidTypeTest(StatType type) {
    Token operator = mock(Token.class);
    when(operator.getText()).thenReturn("op");
    StatParser.ExpressionContext leftSubExpr = mockSubExpr(type, 1, 1);
    StatParser.ExpressionContext rightSubExpr = mockSubExpr(type, 1, 10);
    StatParser.AddSubExprContext expr = mock(StatParser.AddSubExprContext.class);
    expr.leftOperand = leftSubExpr;
    expr.rightOperand = rightSubExpr;
    expr.operator = operator;

    new TypeChecker(types, listener, scope).exitAddSubExpr(expr);

    verify(listener).semanticError(1, 1, "left operand of 'op' expression must be of type NUMBER.");
    verify(listener)
        .semanticError(1, 10, "right operand of 'op' expression must be of type NUMBER.");
    assertEquals(StatType.NUMBER, types.get(expr));
  }

  @ParameterizedTest
  @EnumSource(
      value = StatType.class,
      names = {"VOID", "PROPERTY"})
  void exitEqualExprInvalidTypesTest(StatType type) {
    Token operator = mock(Token.class);
    when(operator.getText()).thenReturn("op");
    StatParser.ExpressionContext leftSubExpr = mockSubExpr(type, 1, 1);
    StatParser.ExpressionContext rightSubExpr = mockSubExpr(type, 1, 10);
    StatParser.EqualExprContext expr = mock(StatParser.EqualExprContext.class);
    expr.leftOperand = leftSubExpr;
    expr.rightOperand = rightSubExpr;
    expr.operator = operator;

    new TypeChecker(types, listener, scope).exitEqualExpr(expr);

    verify(listener)
        .semanticError(1, 1, "left operand of 'op' expression must be of type NUMBER or BOOLEAN.");
    verify(listener)
        .semanticError(
            1, 10, "right operand of 'op' expression must be of type NUMBER or BOOLEAN.");
    assertEquals(StatType.BOOLEAN, types.get(expr));
  }

  @Test
  void exitEqualExprValidTypesButDifferentTypesTest() {
    Token operator = mock(Token.class);
    when(operator.getText()).thenReturn("op");
    StatParser.ExpressionContext leftSubExpr = mockSubExpr(StatType.NUMBER, 1, 1);
    StatParser.ExpressionContext rightSubExpr = mockSubExpr(StatType.BOOLEAN, 1, 10);
    StatParser.EqualExprContext expr = mock(StatParser.EqualExprContext.class);
    expr.leftOperand = leftSubExpr;
    expr.rightOperand = rightSubExpr;
    expr.operator = operator;

    new TypeChecker(types, listener, scope).exitEqualExpr(expr);

    verify(listener)
        .semanticError(1, 1, "both operands of 'op' expression must be of the same type.");
    assertEquals(StatType.BOOLEAN, types.get(expr));
  }

  @ParameterizedTest
  @EnumSource(
      value = StatType.class,
      names = {"NUMBER", "BOOLEAN"})
  void exitEqualExprValidTypesTest(StatType type) {
    StatParser.ExpressionContext leftSubExpr = mockSubExpr(type, 1, 1);
    StatParser.ExpressionContext rightSubExpr = mockSubExpr(type, 1, 10);
    StatParser.EqualExprContext expr = mock(StatParser.EqualExprContext.class);
    expr.leftOperand = leftSubExpr;
    expr.rightOperand = rightSubExpr;

    new TypeChecker(types, listener, scope).exitEqualExpr(expr);

    verify(listener, times(0)).semanticError(anyInt(), anyInt(), anyString());
    assertEquals(StatType.BOOLEAN, types.get(expr));
  }

  @ParameterizedTest
  @EnumSource(
      value = StatType.class,
      names = {"VOID", "PROPERTY", "BOOLEAN"})
  void exitCompExprInvalidTypesTest(StatType type) {
    Token operator = mock(Token.class);
    when(operator.getText()).thenReturn("op");
    StatParser.ExpressionContext leftSubExpr = mockSubExpr(type, 1, 1);
    StatParser.ExpressionContext rightSubExpr = mockSubExpr(type, 1, 10);
    StatParser.CompExprContext expr = mock(StatParser.CompExprContext.class);
    expr.leftOperand = leftSubExpr;
    expr.rightOperand = rightSubExpr;
    expr.operator = operator;

    new TypeChecker(types, listener, scope).exitCompExpr(expr);

    verify(listener).semanticError(1, 1, "left operand of 'op' expression must be of type NUMBER.");
    verify(listener)
        .semanticError(1, 10, "right operand of 'op' expression must be of type NUMBER.");
    assertEquals(StatType.BOOLEAN, types.get(expr));
  }

  @ParameterizedTest
  @EnumSource(
      value = StatType.class,
      names = {"NUMBER"})
  void exitCompExprValidTypesTest(StatType type) {
    StatParser.ExpressionContext leftSubExpr = mockSubExpr(type, 1, 1);
    StatParser.ExpressionContext rightSubExpr = mockSubExpr(type, 1, 10);
    StatParser.CompExprContext expr = mock(StatParser.CompExprContext.class);
    expr.leftOperand = leftSubExpr;
    expr.rightOperand = rightSubExpr;

    new TypeChecker(types, listener, scope).exitCompExpr(expr);

    verify(listener, times(0)).semanticError(anyInt(), anyInt(), anyString());
    assertEquals(StatType.BOOLEAN, types.get(expr));
  }

  @ParameterizedTest
  @EnumSource(
      value = StatType.class,
      names = {"VOID", "PROPERTY", "NUMBER"})
  void exitLogicExprInvalidTypesTest(StatType type) {
    Token operator = mock(Token.class);
    when(operator.getText()).thenReturn("op");
    StatParser.ExpressionContext leftSubExpr = mockSubExpr(type, 1, 1);
    StatParser.ExpressionContext rightSubExpr = mockSubExpr(type, 1, 10);
    StatParser.LogicExprContext expr = mock(StatParser.LogicExprContext.class);
    expr.leftOperand = leftSubExpr;
    expr.rightOperand = rightSubExpr;
    expr.operator = operator;

    new TypeChecker(types, listener, scope).exitLogicExpr(expr);

    verify(listener)
        .semanticError(1, 1, "left operand of 'op' expression must be of type BOOLEAN.");
    verify(listener)
        .semanticError(1, 10, "right operand of 'op' expression must be of type BOOLEAN.");
    assertEquals(StatType.BOOLEAN, types.get(expr));
  }

  @ParameterizedTest
  @EnumSource(
      value = StatType.class,
      names = {"BOOLEAN"})
  void exitLogicExprValidTypesTest(StatType type) {
    StatParser.ExpressionContext leftSubExpr = mockSubExpr(type, 1, 1);
    StatParser.ExpressionContext rightSubExpr = mockSubExpr(type, 1, 10);
    StatParser.LogicExprContext expr = mock(StatParser.LogicExprContext.class);
    expr.leftOperand = leftSubExpr;
    expr.rightOperand = rightSubExpr;

    new TypeChecker(types, listener, scope).exitLogicExpr(expr);

    verify(listener, times(0)).semanticError(anyInt(), anyInt(), anyString());
    assertEquals(StatType.BOOLEAN, types.get(expr));
  }

  @ParameterizedTest
  @EnumSource(
      value = StatType.class,
      names = {"VOID", "PROPERTY", "NUMBER"})
  void exitNotExprInvalidTypesTest(StatType type) {
    Token operator = mock(Token.class);
    when(operator.getText()).thenReturn("op");
    StatParser.ExpressionContext subExpr = mockSubExpr(type);
    StatParser.NotExprContext expr = mock(StatParser.NotExprContext.class);
    when(expr.expression()).thenReturn(subExpr);

    new TypeChecker(types, listener, scope).exitNotExpr(expr);

    verify(listener).semanticError(1, 1, "expression following '!' must be of type BOOLEAN.");
    assertEquals(StatType.BOOLEAN, types.get(expr));
  }

  @ParameterizedTest
  @EnumSource(
      value = StatType.class,
      names = {"BOOLEAN"})
  void exitNotExprValidTypesTest(StatType type) {
    StatParser.ExpressionContext subExpr = mockSubExpr(type, 1, 1);
    StatParser.NotExprContext expr = mock(StatParser.NotExprContext.class);
    when(expr.expression()).thenReturn(subExpr);

    new TypeChecker(types, listener, scope).exitNotExpr(expr);

    verify(listener, times(0)).semanticError(anyInt(), anyInt(), anyString());
    assertEquals(StatType.BOOLEAN, types.get(expr));
  }

  StatParser.ExpressionContext mockSubExpr(StatType type) {
    return mockSubExpr(type, 1, 1);
  }

  private StatParser.ExpressionContext mockSubExpr(StatType type, int line, int charPosInLine) {
    StatParser.ExpressionContext subExpr = mock(StatParser.ExpressionContext.class);
    Token start = mock(Token.class);
    when(start.getLine()).thenReturn(line);
    when(start.getCharPositionInLine()).thenReturn(charPosInLine);
    subExpr.start = start;
    types.put(subExpr, type);
    return subExpr;
  }

  private <T extends StatParser.ExpressionContext> T mockExpr(Class<T> exprClass) {
    return mockExpr(exprClass, 1, 1);
  }

  private <T extends StatParser.ExpressionContext> T mockExpr(
      Class<T> exprClass, int line, int charPosInLine) {
    T expr = mock(exprClass);
    Token start = mock(Token.class);
    when(start.getLine()).thenReturn(line);
    when(start.getCharPositionInLine()).thenReturn(charPosInLine);

    expr.start = start;
    return expr;
  }
}
