package com.eno.stationlang.service.compiler.backend;

import com.eno.stationlang.parser.StatBaseVisitor;
import com.eno.stationlang.parser.StatParser;
import com.eno.stationlang.service.compiler.frontend.StatType;
import com.eno.stationlang.service.compiler.frontend.symboltable.*;
import org.apache.commons.lang3.NotImplementedException;
import org.stringtemplate.v4.ST;

public class CodeGenerator extends StatBaseVisitor<String> {
  private Scope currentScope;
  private int resultRegister = 0;

  public CodeGenerator(Scope currentScope) {
    this.currentScope = currentScope;
  }

  @Override
  public String visitVarDecl(StatParser.VarDeclContext ctx) {
    if (currentScope instanceof Function) {
      Variable symbol = (Variable) currentScope.resolve(ctx.varDef().ID().getText());
      if (ctx.expression() != null) {
        int reg = resultRegister;
        resultRegister = symbol.getIndexInScope();
        ST template = new ST("move <varReg> <temporaryReg>");
        template.add("varReg", "r" + symbol.getIndexInScope());
        template.add("temporaryReg", visit(ctx.expression()));
        System.out.println(template.render());
        resultRegister = reg;
      }
    }

    return null;
  }

  @Override
  public String visitFuncDecl(StatParser.FuncDeclContext ctx) {
    ST template = new ST("<name>:");
    template.add("name", ctx.ID().getText());
    System.out.println(template.render());

    var function = (Function) currentScope.resolve(ctx.ID().getText());
    if (function.getName().equals("update")) {
      System.out.println("yield");
    }
    currentScope = function;

    visitChildren(ctx);

    if (function.getName().equals("update") || function.getName().equals("setup")) {
      System.out.println("j update");
    } else {
      System.out.println("j ra");
    }
    currentScope = currentScope.getParentScope();

    return null;
  }

  @Override
  public String visitIfStmt(StatParser.IfStmtContext ctx) {
    System.out.println("beqz " + visit(ctx.condition) + " else");
    visit(ctx.block(0));
    if (ctx.block(1) != null) {
      System.out.println("j endif");
      System.out.println("else:");
      visit(ctx.block(1));
      System.out.println("endif:");
    } else {
      System.out.println("else:");
    }

    return null;
  }

  @Override
  public String visitAssignStmt(StatParser.AssignStmtContext ctx) {
    int reg = resultRegister;
    Variable variable = (Variable) currentScope.resolve(ctx.ID().getText());
    resultRegister = variable.getIndexInScope();
    visit(ctx.right);
    resultRegister = reg;
    return null;
  }

  @Override
  public String visitParenExpr(StatParser.ParenExprContext ctx) {
    return visit(ctx.expression());
  }

  @Override
  public String visitVarExpr(StatParser.VarExprContext ctx) {
    var resolved = currentScope.resolve(ctx.ID().getText());
    if (resolved instanceof Variable variable) {
      return "r" + variable.getIndexInScope();
    } else if (resolved instanceof Constant constant) {
      return constant.getValue();
    } else if (resolved instanceof Device device) {
      return "d" + device.getValue();
    }

    throw new RuntimeException();
  }

  private String callBuiltInFunction(
      String name, String device, String property, StatParser.ExpressionContext context) {
    return switch (name) {
      case "readNumber", "readBoolean" -> {
        System.out.println(
            new ST("l <reg> <device> <property>")
                .add("reg", "r" + resultRegister)
                .add("device", device)
                .add("property", property)
                .render());
        yield "r" + resultRegister;
      }
      case "writeNumber", "writeBoolean" -> {
        System.out.println(
            new ST("s <device> <property> <reg>")
                .add("reg", visit(context))
                .add("device", device)
                .add("property", property)
                .render());
        yield null;
      }
      default -> throw new NotImplementedException(name);
    };
  }

  @Override
  public String visitCallExpr(StatParser.CallExprContext ctx) {
    Function function = (Function) currentScope.resolve(ctx.ID().getText());

    if (function.isBuiltIn()) {
      var device = visit(ctx.expression(0));
      var propertyCtx = (StatParser.PropertyExprContext) ctx.expression(1);
      return callBuiltInFunction(
          function.getName(), device, propertyCtx.ID().getText(), ctx.expression(2));
    } else {
      System.out.println("j " + function.getName());
      if (function.getType() == StatType.VOID) {
        return null;
      } else {
        return "r10";
      }
    }
  }

  @Override
  public String visitNumberExpr(StatParser.NumberExprContext ctx) {
    return ctx.getText();
  }

  @Override
  public String visitUnaryMinusExpr(StatParser.UnaryMinusExprContext ctx) {
    int reg = resultRegister;
    resultRegister = 10;
    ST template = new ST("sub r<destReg> 0 <reg>");
    template.add("destReg", reg);
    template.add("reg", visit(ctx.expression()));
    System.out.println(template.render());
    resultRegister = reg;
    return "r" + resultRegister;
  }

  @Override
  public String visitMultDivideExpr(StatParser.MultDivideExprContext ctx) {
    String op;

    switch (ctx.operator.getType()) {
      case StatParser.TIMES -> op = "mul";
      case StatParser.DIVIDE -> op = "div";
      default -> throw new UnsupportedOperationException();
    }

    int reg = resultRegister;
    resultRegister = 10;
    String leftReg = visit(ctx.leftOperand);
    resultRegister = 11;
    String rightReg = visit(ctx.rightOperand);
    resultRegister = reg;

    ST template = new ST("<op> r<resultReg> <left> <right>");
    template.add("op", op);
    template.add("resultReg", resultRegister);
    template.add("left", leftReg);
    template.add("right", rightReg);

    System.out.println(template.render());
    return "r" + resultRegister;
  }

  @Override
  public String visitAddSubExpr(StatParser.AddSubExprContext ctx) {
    String op;

    switch (ctx.operator.getType()) {
      case StatParser.PLUS -> op = "add";
      case StatParser.MINUS -> op = "sub";
      default -> throw new UnsupportedOperationException();
    }
    int reg = resultRegister;
    resultRegister = 10;
    String leftReg = visit(ctx.leftOperand);
    resultRegister = 11;
    String rightReg = visit(ctx.rightOperand);
    resultRegister = reg;

    ST template = new ST("<op> r<resultReg> <left> <right>");
    template.add("op", op);
    template.add("resultReg", resultRegister);
    template.add("left", leftReg);
    template.add("right", rightReg);

    System.out.println(template.render());
    return "r" + resultRegister;
  }

  @Override
  public String visitFalseExpr(StatParser.FalseExprContext ctx) {
    return "0";
  }

  @Override
  public String visitTrueExpr(StatParser.TrueExprContext ctx) {
    return "1";
  }

  @Override
  public String visitEqualExpr(StatParser.EqualExprContext ctx) {
    String op;

    switch (ctx.operator.getType()) {
      case StatParser.EQUAL -> op = "seq";
      case StatParser.DIFF -> op = "sne";
      default -> throw new UnsupportedOperationException();
    }
    int reg = resultRegister;
    resultRegister = 10;
    String leftReg = visit(ctx.leftOperand);
    resultRegister = 11;
    String rightReg = visit(ctx.rightOperand);
    resultRegister = reg;

    ST template = new ST("<op> r<resultReg> <left> <right>");
    template.add("op", op);
    template.add("resultReg", resultRegister);
    template.add("left", leftReg);
    template.add("right", rightReg);

    System.out.println(template.render());
    return "r" + resultRegister;
  }

  @Override
  public String visitCompExpr(StatParser.CompExprContext ctx) {
    String op;

    switch (ctx.operator.getType()) {
      case StatParser.LT -> op = "slt";
      case StatParser.LTE -> op = "sle";
      case StatParser.GT -> op = "sgt";
      case StatParser.GTE -> op = "sge";
      default -> throw new UnsupportedOperationException();
    }
    int reg = resultRegister;
    resultRegister = 10;
    String leftReg = visit(ctx.leftOperand);
    resultRegister = 11;
    String rightReg = visit(ctx.rightOperand);
    resultRegister = reg;

    ST template = new ST("<op> r<resultReg> <left> <right>");
    template.add("op", op);
    template.add("resultReg", resultRegister);
    template.add("left", leftReg);
    template.add("right", rightReg);

    System.out.println(template.render());
    return "r" + resultRegister;
  }

  @Override
  public String visitLogicExpr(StatParser.LogicExprContext ctx) {
    String op;

    switch (ctx.operator.getType()) {
      case StatParser.AND -> op = "and";
      case StatParser.OR -> op = "or";
      default -> throw new UnsupportedOperationException();
    }
    int reg = resultRegister;
    resultRegister = 10;
    String leftReg = visit(ctx.leftOperand);
    resultRegister = 11;
    String rightReg = visit(ctx.rightOperand);
    resultRegister = reg;

    ST template = new ST("<op> r<resultReg> <left> <right>");
    template.add("op", op);
    template.add("resultReg", resultRegister);
    template.add("left", leftReg);
    template.add("right", rightReg);

    System.out.println(template.render());
    return "r" + resultRegister;
  }

  @Override
  public String visitNotExpr(StatParser.NotExprContext ctx) {
    int reg = resultRegister;
    resultRegister = 10;
    ST template = new ST("seqz r<resultReg> <reg>");
    template.add("resultReg", reg);
    template.add("reg", visit(ctx.expression()));
    resultRegister = reg;

    System.out.println(template.render());
    return "r" + resultRegister;
  }
}
