package com.eno.stationlang.parser;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.ByteBuffer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CodePointBuffer;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class StatParserTest {

  @Test
  void numberVarDeclTest() {
    String code = "number x;";
    var parser = getParser(code);
    var ctx = parser.varDecl();

    assertNull(ctx.expression());
    assertEquals(ctx.varDef().type().getText(), "number");
    assertEquals(ctx.varDef().ID().getText(), "x");
  }

  @ParameterizedTest
  @ValueSource(strings = {"10", "true", "compute()", "9.8 + 5.2"})
  void numberVarDeclWithInitTest(String expr) {
    String code = "number x = " + expr + ";";
    var parser = getParser(code);
    var ctx = parser.varDecl();

    assertNotNull(ctx.varDef());
    assertNotNull(ctx.expression());
  }

  @Nested
  class statementExprTest {

    @Test
    void ifStmtTest() {
      String code = "if (true) {x = 5;}";
      var parser = getParser(code);
      var ctx = parser.statement();

      var ifStmtCtx = assertInstanceOf(StatParser.IfStmtContext.class, ctx);
      assertInstanceOf(StatParser.TrueExprContext.class, ifStmtCtx.condition);
      assertEquals(1, ifStmtCtx.block().size());
    }

    @Test
    void ifElseStmtTest() {
      String code = "if (true) {x = 5;} else {x = 10;}";
      var parser = getParser(code);
      var ctx = parser.statement();

      var ifStmtCtx = assertInstanceOf(StatParser.IfStmtContext.class, ctx);
      assertInstanceOf(StatParser.TrueExprContext.class, ifStmtCtx.condition);
      assertEquals(2, ifStmtCtx.block().size());
    }

    @Test
    void assignStmtTest() {
      String code = "myVar = 100;";
      var parser = getParser(code);
      var ctx = parser.statement();

      var assignStmtCtx = assertInstanceOf(StatParser.AssignStmtContext.class, ctx);
      assertInstanceOf(StatParser.VarExprContext.class, assignStmtCtx.left);
      assertInstanceOf(StatParser.NumberExprContext.class, assignStmtCtx.right);
    }

    @Test
    void exprStmtTest() {
      String code = "call();";
      var parser = getParser(code);
      var ctx = parser.statement();

      var exprStmtCtx = assertInstanceOf(StatParser.ExprStmtContext.class, ctx);
      assertInstanceOf(StatParser.CallExprContext.class, exprStmtCtx.expression());
    }

    @Test
    void returnStmtTest() {
      String code = "return call();";
      var parser = getParser(code);
      var ctx = parser.statement();

      var returnStmtCtx = assertInstanceOf(StatParser.ReturnStmtContext.class, ctx);
      assertInstanceOf(StatParser.CallExprContext.class, returnStmtCtx.expression());
    }
  }

  @Nested
  class expressionTest {

    @Test
    void parenExprTest() {
      String code = "(10 + 5)";
      var parser = getParser(code);
      var ctx = parser.expression();

      var parenExprCtx = assertInstanceOf(StatParser.ParenExprContext.class, ctx);
      assertEquals("10+5", parenExprCtx.expression().getText());
    }

    @Test
    void varExprTest() {
      String code = "varName";
      var parser = getParser(code);
      var ctx = parser.expression();

      var varExprCtx = assertInstanceOf(StatParser.VarExprContext.class, ctx);
      assertEquals("varName", varExprCtx.ID().getText());
    }

    @Test
    void callExprTest() {
      String code = "function()";
      var parser = getParser(code);
      var ctx = parser.expression();

      var callExprCtx = assertInstanceOf(StatParser.CallExprContext.class, ctx);
      assertEquals("function", callExprCtx.ID().getText());
      assertEquals(0, callExprCtx.expression().size());
    }

    @Test
    void callExprWithArgumentsTest() {
      String code = "function(10, true, x)";
      var parser = getParser(code);
      var ctx = parser.expression();

      var callExprCtx = assertInstanceOf(StatParser.CallExprContext.class, ctx);
      assertEquals("function", callExprCtx.ID().getText());
      assertEquals(3, callExprCtx.expression().size());
    }

    @Test
    void propertyExprTest() {
      String code = "\"Activate\"";
      var parser = getParser(code);
      var ctx = parser.expression();

      var propertyExprCtx = assertInstanceOf(StatParser.PropertyExprContext.class, ctx);
      assertEquals("Activate", propertyExprCtx.ID().getText());
    }

    @Test
    void numberExprTest() {
      String code = "-10";
      var parser = getParser(code);
      var ctx = parser.expression();

      var numberExprCtx = assertInstanceOf(StatParser.NumberExprContext.class, ctx);
      assertEquals("-10", numberExprCtx.NUMBER().getText());
    }

    @Test
    void unaryMinusExprTest() {
      String code = "--10";
      var parser = getParser(code);
      var ctx = parser.expression();

      var unaryMinusExprCtx = assertInstanceOf(StatParser.UnaryMinusExprContext.class, ctx);
      assertEquals("-10", unaryMinusExprCtx.expression().getText());
    }

    @Test
    void multExprTest() {
      String code = "11 * 23";
      var parser = getParser(code);
      var ctx = parser.expression();

      var multDivideExprCtx = assertInstanceOf(StatParser.MultDivideExprContext.class, ctx);
      assertEquals("11", multDivideExprCtx.expression(0).getText());
      assertEquals("23", multDivideExprCtx.expression(1).getText());
    }

    @Test
    void divideExprTest() {
      String code = "11 / 23";
      var parser = getParser(code);
      var ctx = parser.expression();

      var multDivideExprCtx = assertInstanceOf(StatParser.MultDivideExprContext.class, ctx);
      assertEquals("11", multDivideExprCtx.expression(0).getText());
      assertEquals("23", multDivideExprCtx.expression(1).getText());
    }

    @Test
    void addExprTest() {
      String code = "11 + 23";
      var parser = getParser(code);
      var ctx = parser.expression();

      var addSubExprCtx = assertInstanceOf(StatParser.AddSubExprContext.class, ctx);
      assertEquals("11", addSubExprCtx.expression(0).getText());
      assertEquals("23", addSubExprCtx.expression(1).getText());
    }

    @Test
    void subExprTest() {
      String code = "11 - 23";
      var parser = getParser(code);
      var ctx = parser.expression();

      var addSubExprCtx = assertInstanceOf(StatParser.AddSubExprContext.class, ctx);
      assertEquals("11", addSubExprCtx.expression(0).getText());
      assertEquals("23", addSubExprCtx.expression(1).getText());
    }

    @Test
    void trueExprTest() {
      String code = "true";
      var parser = getParser(code);
      var ctx = parser.expression();

      assertInstanceOf(StatParser.TrueExprContext.class, ctx);
    }

    @Test
    void falseExprTest() {
      String code = "false";
      var parser = getParser(code);
      var ctx = parser.expression();

      assertInstanceOf(StatParser.FalseExprContext.class, ctx);
    }

    @Test
    void equalExprTest() {
      String code = "11 == 23";
      var parser = getParser(code);
      var ctx = parser.expression();

      var equalExprCtx = assertInstanceOf(StatParser.EqualExprContext.class, ctx);
      assertEquals("11", equalExprCtx.expression(0).getText());
      assertEquals("23", equalExprCtx.expression(1).getText());
    }

    @Test
    void notEqualExprTest() {
      String code = "11 != 23";
      var parser = getParser(code);
      var ctx = parser.expression();

      var equalExprCtx = assertInstanceOf(StatParser.EqualExprContext.class, ctx);
      assertEquals("11", equalExprCtx.expression(0).getText());
      assertEquals("23", equalExprCtx.expression(1).getText());
    }

    @Test
    void LTExprTest() {
      String code = "11 < 23";
      var parser = getParser(code);
      var ctx = parser.expression();

      var compExprCtx = assertInstanceOf(StatParser.CompExprContext.class, ctx);
      assertEquals("11", compExprCtx.expression(0).getText());
      assertEquals("23", compExprCtx.expression(1).getText());
    }

    @Test
    void GTExprTest() {
      String code = "11 > 23";
      var parser = getParser(code);
      var ctx = parser.expression();

      var compExprCtx = assertInstanceOf(StatParser.CompExprContext.class, ctx);
      assertEquals("11", compExprCtx.expression(0).getText());
      assertEquals("23", compExprCtx.expression(1).getText());
    }

    @Test
    void LTEExprTest() {
      String code = "11 <= 23";
      var parser = getParser(code);
      var ctx = parser.expression();

      var compExprCtx = assertInstanceOf(StatParser.CompExprContext.class, ctx);
      assertEquals("11", compExprCtx.expression(0).getText());
      assertEquals("23", compExprCtx.expression(1).getText());
    }

    @Test
    void GTEExprTest() {
      String code = "11 >= 23";
      var parser = getParser(code);
      var ctx = parser.expression();

      var compExprCtx = assertInstanceOf(StatParser.CompExprContext.class, ctx);
      assertEquals("11", compExprCtx.expression(0).getText());
      assertEquals("23", compExprCtx.expression(1).getText());
    }

    @Test
    void andExprTest() {
      String code = "true && false";
      var parser = getParser(code);
      var ctx = parser.expression();

      var logicExprCtx = assertInstanceOf(StatParser.LogicExprContext.class, ctx);
      assertEquals("true", logicExprCtx.expression(0).getText());
      assertEquals("false", logicExprCtx.expression(1).getText());
    }

    @Test
    void orExprTest() {
      String code = "true || false";
      var parser = getParser(code);
      var ctx = parser.expression();

      var logicExprCtx = assertInstanceOf(StatParser.LogicExprContext.class, ctx);
      assertEquals("true", logicExprCtx.expression(0).getText());
      assertEquals("false", logicExprCtx.expression(1).getText());
    }

    @Test
    void notExprTest() {
      String code = "!true";
      var parser = getParser(code);
      var ctx = parser.expression();

      var notExprCtx = assertInstanceOf(StatParser.NotExprContext.class, ctx);
      assertEquals("true", notExprCtx.expression().getText());
    }
  }

  private static StatParser getParser(String code) {
    CodePointBuffer buffer = CodePointBuffer.withBytes(ByteBuffer.wrap(code.getBytes()));
    CharStream stream = CodePointCharStream.fromBuffer(buffer);
    StatLexer lexer = new StatLexer(stream);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    StatParser parser = new StatParser(tokens);
    parser.addErrorListener(new ThrowingErrorListener());
    return parser;
  }
}
