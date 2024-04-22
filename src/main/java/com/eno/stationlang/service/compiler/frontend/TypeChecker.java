package com.eno.stationlang.service.compiler.frontend;

import com.eno.stationlang.parser.StatBaseListener;
import com.eno.stationlang.parser.StatParser;
import com.eno.stationlang.service.compiler.error.SemanticErrorListener;
import com.eno.stationlang.service.compiler.frontend.symboltable.Function;
import com.eno.stationlang.service.compiler.frontend.symboltable.Scope;
import com.eno.stationlang.service.compiler.frontend.symboltable.Symbol;
import com.eno.stationlang.service.compiler.frontend.symboltable.Variable;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

public class TypeChecker extends StatBaseListener {

  private final ParseTreeProperty<StatType> types;
  private final SemanticErrorListener listener;
  private Scope currentScope;

  public TypeChecker(SemanticErrorListener listener, Scope globalScope) {
    this(new ParseTreeProperty<>(), listener, globalScope);
  }

  public TypeChecker(
      ParseTreeProperty<StatType> types, SemanticErrorListener listener, Scope globalScope) {
    this.types = types;
    this.listener = listener;
    this.currentScope = globalScope;
  }

  @Override
  public void exitVarDef(StatParser.VarDefContext ctx) {
    StatType type;

    if (ctx.type().BOOLEANTYPE() != null) type = StatType.BOOLEAN;
    else if (ctx.type().NUMBERTYPE() != null) type = StatType.NUMBER;
    else type = null;

    Variable variable = new Variable(ctx.ID().getText(), type);
    currentScope.define(variable);
  }

  @Override
  public void enterFuncDecl(StatParser.FuncDeclContext ctx) {
    StatType type;

    if (ctx.type() != null) {
      if (ctx.type().BOOLEANTYPE() != null) type = StatType.BOOLEAN;
      else if (ctx.type().NUMBERTYPE() != null) type = StatType.NUMBER;
      else type = null;
    } else {
      type = StatType.VOID;
    }

    Function function = new Function(ctx.ID().getText(), type, currentScope);
    currentScope.define(function);
    currentScope = function;
  }

  @Override
  public void exitFuncDecl(StatParser.FuncDeclContext ctx) {
    currentScope = currentScope.getParentScope();
  }

  @Override
  public void exitParenExpr(StatParser.ParenExprContext ctx) {
    types.put(ctx, types.get(ctx.expression()));
  }

  @Override
  public void exitVarExpr(StatParser.VarExprContext ctx) {
    Symbol symbol = currentScope.resolve(ctx.ID().getText());

    if (symbol == null) {
      reportSemanticError(ctx, "variable '" + ctx.ID().getText() + "' is not defined.");
      types.put(ctx, StatType.VOID);
      return;
    }

    types.put(ctx, symbol.getType());
  }

  @Override
  public void exitCallExpr(StatParser.CallExprContext ctx) {
    Symbol symbol = currentScope.resolve(ctx.ID().getText());

    if (symbol == null) {
      reportSemanticError(ctx, "function '" + ctx.ID().getText() + "' is not defined.");
      types.put(ctx, StatType.VOID);
      return;
    }

    if (symbol instanceof Function function) {
      types.put(ctx, function.getType());
      System.out.println("TODO: check arguments type and count.");
      return;
    }

    reportSemanticError(ctx, "can only call functions.");
  }

  @Override
  public void exitPropertyExpr(StatParser.PropertyExprContext ctx) {
    types.put(ctx, StatType.PROPERTY);
  }

  @Override
  public void exitNumberExpr(StatParser.NumberExprContext ctx) {
    types.put(ctx, StatType.NUMBER);
  }

  @Override
  public void exitUnaryMinusExpr(StatParser.UnaryMinusExprContext ctx) {
    var expr = ctx.expression();
    if (types.get(expr) != StatType.NUMBER) {
      reportSemanticError(expr, "expression following '-' must be of type NUMBER.");
    }

    types.put(ctx, StatType.NUMBER);
  }

  @Override
  public void exitMultDivideExpr(StatParser.MultDivideExprContext ctx) {
    checkNumberOperands(ctx.leftOperand, ctx.rightOperand, ctx.operator);
    types.put(ctx, StatType.NUMBER);
  }

  @Override
  public void exitAddSubExpr(StatParser.AddSubExprContext ctx) {
    checkNumberOperands(ctx.leftOperand, ctx.rightOperand, ctx.operator);
    types.put(ctx, StatType.NUMBER);
  }

  @Override
  public void exitTrueExpr(StatParser.TrueExprContext ctx) {
    types.put(ctx, StatType.BOOLEAN);
  }

  @Override
  public void exitFalseExpr(StatParser.FalseExprContext ctx) {
    types.put(ctx, StatType.BOOLEAN);
  }

  @Override
  public void exitEqualExpr(StatParser.EqualExprContext ctx) {
    var left = ctx.leftOperand;
    var right = ctx.rightOperand;
    var operator = ctx.operator;
    var leftType = types.get(left);
    var rightType = types.get(right);

    checkNumberOrBooleanOperands(left, right, operator);
    if (leftType != rightType) {
      reportSemanticError(
              left,
              "both operands of '" + operator.getText() + "' expression must be of the same type.");
    }

    types.put(ctx, StatType.BOOLEAN);
  }

  @Override
  public void exitCompExpr(StatParser.CompExprContext ctx) {
    checkNumberOperands(ctx.leftOperand, ctx.rightOperand, ctx.operator);
    types.put(ctx, StatType.BOOLEAN);
  }

  @Override
  public void exitLogicExpr(StatParser.LogicExprContext ctx) {
    checkBooleanOperands(ctx.leftOperand, ctx.rightOperand, ctx.operator);
    types.put(ctx, StatType.BOOLEAN);
  }

  @Override
  public void exitNotExpr(StatParser.NotExprContext ctx) {
    var expr = ctx.expression();
    if (types.get(expr) != StatType.BOOLEAN) {
      reportSemanticError(expr, "expression following '!' must be of type BOOLEAN.");
    }

    types.put(ctx, StatType.BOOLEAN);
  }

  private void checkNumberOperands(
          StatParser.ExpressionContext left, StatParser.ExpressionContext right, Token operator) {
    if (types.get(left) != StatType.NUMBER) {
      reportSemanticError(
              left, "left operand of '" + operator.getText() + "' expression must be of type NUMBER.");
    }

    if (types.get(right) != StatType.NUMBER) {
      reportSemanticError(
              right,
              "right operand of '" + operator.getText() + "' expression must be of type NUMBER.");
    }
  }

  private void checkBooleanOperands(StatParser.ExpressionContext left, StatParser.ExpressionContext right, Token operator) {
    if (types.get(left) != StatType.BOOLEAN) {
      reportSemanticError(
              left, "left operand of '" + operator.getText() + "' expression must be of type BOOLEAN.");
    }

    if (types.get(right) != StatType.BOOLEAN) {
      reportSemanticError(
              right,
              "right operand of '" + operator.getText() + "' expression must be of type BOOLEAN.");
    }
  }

  private void checkNumberOrBooleanOperands(StatParser.ExpressionContext left, StatParser.ExpressionContext right, Token operator) {
    if (types.get(left) != StatType.BOOLEAN && types.get(left) != StatType.NUMBER) {
      reportSemanticError(
              left, "left operand of '" + operator.getText() + "' expression must be of type NUMBER or BOOLEAN.");
    }

    if (types.get(right) != StatType.BOOLEAN && types.get(right) != StatType.NUMBER) {
      reportSemanticError(
              right,
              "right operand of '" + operator.getText() + "' expression must be of type NUMBER or BOOLEAN.");
    }
  }

  private void reportSemanticError(ParserRuleContext ctx, String message) {
    listener.semanticError(ctx.start.getLine(), ctx.start.getCharPositionInLine(), message);
  }
}
