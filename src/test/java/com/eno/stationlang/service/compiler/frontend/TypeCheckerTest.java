package com.eno.stationlang.service.compiler.frontend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.eno.stationlang.parser.StatParser;
import com.eno.stationlang.service.compiler.error.SemanticErrorListener;
import com.eno.stationlang.service.compiler.frontend.symboltable.Scope;
import com.eno.stationlang.service.compiler.frontend.symboltable.Variable;
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

  StatParser.ExpressionContext createSubExpr() {
    return createSubExpr(1, 1);
  }

  StatParser.ExpressionContext createSubExpr(int line, int charPosInLine) {
    StatParser.VarExprContext subExpr = mock(StatParser.VarExprContext.class);
    Token start = mock(Token.class);
    when(start.getLine()).thenReturn(line);
    when(start.getCharPositionInLine()).thenReturn(charPosInLine);
    subExpr.start = start;
    return subExpr;
  }

  @ParameterizedTest
  @EnumSource(StatType.class)
  void exitParenExprTest(StatType type) {
    StatParser.ExpressionContext subExpr = createSubExpr();
    StatParser.ParenExprContext expr = mock(StatParser.ParenExprContext.class);
    when(expr.expression()).thenReturn(subExpr);
    types.put(subExpr, type);

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
    StatParser.VarExprContext expr = mock(StatParser.VarExprContext.class);
    TerminalNode node = mock(TerminalNode.class);
    when(node.getText()).thenReturn("variable");
    when(expr.ID()).thenReturn(node);
    Token id = mock(Token.class);
    when(id.getLine()).thenReturn(1);
    when(id.getCharPositionInLine()).thenReturn(1);
    expr.start = id;

    new TypeChecker(types, listener, scope).exitVarExpr(expr);

    verify(listener).semanticError(1, 1, "variable 'variable' is not defined.");
    assertEquals(StatType.VOID, types.get(expr));
  }

  @ParameterizedTest
  @EnumSource(
      value = StatType.class,
      names = {"BOOLEAN", "VOID", "PROPERTY"})
  void exitUnaryMinusExprSubExprInvalidTypeTest(StatType type) {
    StatParser.ExpressionContext subExpr = createSubExpr();
    StatParser.UnaryMinusExprContext expr = mock(StatParser.UnaryMinusExprContext.class);
    when(expr.expression()).thenReturn(subExpr);
    types.put(subExpr, type);

    new TypeChecker(types, listener, scope).exitUnaryMinusExpr(expr);

    verify(listener).semanticError(1, 1, "expression following '-' must be of type NUMBER.");
  }

  @ParameterizedTest
  @EnumSource(value = StatType.class, names = {"NUMBER"})
  void exitUnaryMinusExprSubExprValidTypeTest(StatType type) {
    StatParser.ExpressionContext subExpr = createSubExpr();
    StatParser.UnaryMinusExprContext expr = mock(StatParser.UnaryMinusExprContext.class);
    when(expr.expression()).thenReturn(subExpr);
    types.put(subExpr, type);

    new TypeChecker(types, listener, scope).exitUnaryMinusExpr(expr);

    verify(listener, times(0)).semanticError(anyInt(), anyInt(), anyString());
    assertEquals(StatType.NUMBER, types.get(expr));
  }

  @ParameterizedTest
  @EnumSource(value = StatType.class, names = {"NUMBER"})
  void exitMultDivideExprValidTypeTest(StatType type) {
    StatParser.ExpressionContext leftSubExpr = createSubExpr();
    StatParser.ExpressionContext rightSubExpr = createSubExpr();
    StatParser.MultDivideExprContext expr = mock(StatParser.MultDivideExprContext.class);
    expr.leftOperand = leftSubExpr;
    expr.rightOperand = rightSubExpr;
    types.put(leftSubExpr, type);
    types.put(rightSubExpr, type);

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
    StatParser.ExpressionContext leftSubExpr = createSubExpr(1, 1);
    StatParser.ExpressionContext rightSubExpr = createSubExpr(1, 10);
    StatParser.MultDivideExprContext expr = mock(StatParser.MultDivideExprContext.class);
    expr.leftOperand = leftSubExpr;
    expr.rightOperand = rightSubExpr;
    expr.operator = operator;
    types.put(leftSubExpr, type);
    types.put(rightSubExpr, type);

    new TypeChecker(types, listener, scope).exitMultDivideExpr(expr);

    verify(listener).semanticError(1, 1, "left operand of 'op' expression must be of type NUMBER.");
    verify(listener)
        .semanticError(1, 10, "right operand of 'op' expression must be of type NUMBER.");
    assertEquals(StatType.NUMBER, types.get(expr));
  }

  @ParameterizedTest
  @EnumSource(value = StatType.class, names = {"NUMBER"})
  void exitAddSubExprValidTypeTest(StatType type) {
    StatParser.ExpressionContext leftSubExpr = createSubExpr();
    StatParser.ExpressionContext rightSubExpr = createSubExpr();
    StatParser.AddSubExprContext expr = mock(StatParser.AddSubExprContext.class);
    expr.leftOperand = leftSubExpr;
    expr.rightOperand = rightSubExpr;
    types.put(leftSubExpr, type);
    types.put(rightSubExpr, type);

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
    StatParser.ExpressionContext leftSubExpr = createSubExpr(1, 1);
    StatParser.ExpressionContext rightSubExpr = createSubExpr(1, 10);
    StatParser.AddSubExprContext expr = mock(StatParser.AddSubExprContext.class);
    expr.leftOperand = leftSubExpr;
    expr.rightOperand = rightSubExpr;
    expr.operator = operator;
    types.put(leftSubExpr, type);
    types.put(rightSubExpr, type);

    new TypeChecker(types, listener, scope).exitAddSubExpr(expr);

    verify(listener).semanticError(1, 1, "left operand of 'op' expression must be of type NUMBER.");
    verify(listener)
        .semanticError(1, 10, "right operand of 'op' expression must be of type NUMBER.");
    assertEquals(StatType.NUMBER, types.get(expr));
  }

  @ParameterizedTest
  @EnumSource(value = StatType.class, names = { "VOID", "PROPERTY" })
  void exitEqualExprInvalidTypesTest(StatType type) {
    Token operator = mock(Token.class);
    when(operator.getText()).thenReturn("op");
    StatParser.ExpressionContext leftSubExpr = createSubExpr(1, 1);
    StatParser.ExpressionContext rightSubExpr = createSubExpr(1, 10);
    StatParser.EqualExprContext expr = mock(StatParser.EqualExprContext.class);
    expr.leftOperand = leftSubExpr;
    expr.rightOperand = rightSubExpr;
    expr.operator = operator;
    types.put(leftSubExpr, type);
    types.put(rightSubExpr, type);

    new TypeChecker(types, listener, scope).exitEqualExpr(expr);

    verify(listener).semanticError(1, 1, "left operand of 'op' expression must be of type NUMBER or BOOLEAN.");
    verify(listener)
        .semanticError(1, 10, "right operand of 'op' expression must be of type NUMBER or BOOLEAN.");
    assertEquals(StatType.BOOLEAN, types.get(expr));
  }

  @Test
  void exitEqualExprValidTypesButDifferentTypesTest() {
    Token operator = mock(Token.class);
    when(operator.getText()).thenReturn("op");
    StatParser.ExpressionContext leftSubExpr = createSubExpr(1, 1);
    StatParser.ExpressionContext rightSubExpr = createSubExpr(1, 10);
    StatParser.EqualExprContext expr = mock(StatParser.EqualExprContext.class);
    expr.leftOperand = leftSubExpr;
    expr.rightOperand = rightSubExpr;
    expr.operator = operator;
    types.put(leftSubExpr, StatType.NUMBER);
    types.put(rightSubExpr, StatType.BOOLEAN);

    new TypeChecker(types, listener, scope).exitEqualExpr(expr);

    verify(listener).semanticError(1, 1, "both operands of 'op' expression must be of the same type.");
    assertEquals(StatType.BOOLEAN, types.get(expr));
  }

  @ParameterizedTest
  @EnumSource(value = StatType.class, names = { "NUMBER", "BOOLEAN" })
  void exitEqualExprValidTypesTest(StatType type) {
    StatParser.ExpressionContext leftSubExpr = createSubExpr(1, 1);
    StatParser.ExpressionContext rightSubExpr = createSubExpr(1, 10);
    StatParser.EqualExprContext expr = mock(StatParser.EqualExprContext.class);
    expr.leftOperand = leftSubExpr;
    expr.rightOperand = rightSubExpr;
    types.put(leftSubExpr, type);
    types.put(rightSubExpr, type);

    new TypeChecker(types, listener, scope).exitEqualExpr(expr);

    verify(listener, times(0)).semanticError(anyInt(), anyInt(), anyString());
    assertEquals(StatType.BOOLEAN, types.get(expr));
  }

  @ParameterizedTest
  @EnumSource(value = StatType.class, names = { "VOID", "PROPERTY", "BOOLEAN" })
  void exitCompExprInvalidTypesTest(StatType type) {
    Token operator = mock(Token.class);
    when(operator.getText()).thenReturn("op");
    StatParser.ExpressionContext leftSubExpr = createSubExpr(1, 1);
    StatParser.ExpressionContext rightSubExpr = createSubExpr(1, 10);
    StatParser.CompExprContext expr = mock(StatParser.CompExprContext.class);
    expr.leftOperand = leftSubExpr;
    expr.rightOperand = rightSubExpr;
    expr.operator = operator;
    types.put(leftSubExpr, type);
    types.put(rightSubExpr, type);

    new TypeChecker(types, listener, scope).exitCompExpr(expr);

    verify(listener).semanticError(1, 1, "left operand of 'op' expression must be of type NUMBER.");
    verify(listener)
            .semanticError(1, 10, "right operand of 'op' expression must be of type NUMBER.");
    assertEquals(StatType.BOOLEAN, types.get(expr));
  }

  @ParameterizedTest
  @EnumSource(value = StatType.class, names = { "NUMBER" })
  void exitCompExprValidTypesTest(StatType type) {
    StatParser.ExpressionContext leftSubExpr = createSubExpr(1, 1);
    StatParser.ExpressionContext rightSubExpr = createSubExpr(1, 10);
    StatParser.CompExprContext expr = mock(StatParser.CompExprContext.class);
    expr.leftOperand = leftSubExpr;
    expr.rightOperand = rightSubExpr;
    types.put(leftSubExpr, type);
    types.put(rightSubExpr, type);

    new TypeChecker(types, listener, scope).exitCompExpr(expr);

    verify(listener, times(0)).semanticError(anyInt(), anyInt(), anyString());
    assertEquals(StatType.BOOLEAN, types.get(expr));
  }

  @ParameterizedTest
  @EnumSource(value = StatType.class, names = { "VOID", "PROPERTY", "NUMBER" })
  void exitLogicExprInvalidTypesTest(StatType type) {
    Token operator = mock(Token.class);
    when(operator.getText()).thenReturn("op");
    StatParser.ExpressionContext leftSubExpr = createSubExpr(1, 1);
    StatParser.ExpressionContext rightSubExpr = createSubExpr(1, 10);
    StatParser.LogicExprContext expr = mock(StatParser.LogicExprContext.class);
    expr.leftOperand = leftSubExpr;
    expr.rightOperand = rightSubExpr;
    expr.operator = operator;
    types.put(leftSubExpr, type);
    types.put(rightSubExpr, type);

    new TypeChecker(types, listener, scope).exitLogicExpr(expr);

    verify(listener).semanticError(1, 1, "left operand of 'op' expression must be of type BOOLEAN.");
    verify(listener)
            .semanticError(1, 10, "right operand of 'op' expression must be of type BOOLEAN.");
    assertEquals(StatType.BOOLEAN, types.get(expr));
  }

  @ParameterizedTest
  @EnumSource(value = StatType.class, names = { "BOOLEAN" })
  void exitLogicExprValidTypesTest(StatType type) {
    StatParser.ExpressionContext leftSubExpr = createSubExpr(1, 1);
    StatParser.ExpressionContext rightSubExpr = createSubExpr(1, 10);
    StatParser.LogicExprContext expr = mock(StatParser.LogicExprContext.class);
    expr.leftOperand = leftSubExpr;
    expr.rightOperand = rightSubExpr;
    types.put(leftSubExpr, type);
    types.put(rightSubExpr, type);

    new TypeChecker(types, listener, scope).exitLogicExpr(expr);

    verify(listener, times(0)).semanticError(anyInt(), anyInt(), anyString());
    assertEquals(StatType.BOOLEAN, types.get(expr));
  }



  @ParameterizedTest
  @EnumSource(value = StatType.class, names = { "VOID", "PROPERTY", "NUMBER" })
  void exitNotExprInvalidTypesTest(StatType type) {
    Token operator = mock(Token.class);
    when(operator.getText()).thenReturn("op");
    StatParser.ExpressionContext subExpr = createSubExpr();
    StatParser.NotExprContext expr = mock(StatParser.NotExprContext.class);
    when(expr.expression()).thenReturn(subExpr);
    types.put(subExpr, type);

    new TypeChecker(types, listener, scope).exitNotExpr(expr);

    verify(listener).semanticError(1, 1, "expression following '!' must be of type BOOLEAN.");
    assertEquals(StatType.BOOLEAN, types.get(expr));
  }

  @ParameterizedTest
  @EnumSource(value = StatType.class, names = { "BOOLEAN" })
  void exitNotExprValidTypesTest(StatType type) {
    StatParser.ExpressionContext subExpr = createSubExpr(1, 1);
    StatParser.NotExprContext expr = mock(StatParser.NotExprContext.class);
    when(expr.expression()).thenReturn(subExpr);
    types.put(subExpr, type);

    new TypeChecker(types, listener, scope).exitNotExpr(expr);

    verify(listener, times(0)).semanticError(anyInt(), anyInt(), anyString());
    assertEquals(StatType.BOOLEAN, types.get(expr));
  }
}
