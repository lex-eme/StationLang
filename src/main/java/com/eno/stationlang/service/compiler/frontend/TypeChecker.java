package com.eno.stationlang.service.compiler.frontend;

import com.eno.stationlang.parser.StatParser;
import com.eno.stationlang.parser.StatVisitor;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

public class TypeChecker implements StatVisitor<Void> {
  @Override
  public Void visitParenExpr(StatParser.ParenExprContext ctx) {
    return null;
  }

  @Override
  public Void visitTrueExpr(StatParser.TrueExprContext ctx) {
    return null;
  }

  @Override
  public Void visitNumberExpr(StatParser.NumberExprContext ctx) {
    return null;
  }

  @Override
  public Void visitAssignStmt(StatParser.AssignStmtContext ctx) {
    return null;
  }

  @Override
  public Void visitProgram(StatParser.ProgramContext ctx) {
    System.out.println("Program visited");
    return null;
  }

  @Override
  public Void visitDeclaration(StatParser.DeclarationContext ctx) {
    return null;
  }

  @Override
  public Void visitType(StatParser.TypeContext ctx) {
    return null;
  }

  @Override
  public Void visitExprStmt(StatParser.ExprStmtContext ctx) {
    return null;
  }

  @Override
  public Void visitUnaryMinusExpr(StatParser.UnaryMinusExprContext ctx) {
    return null;
  }

  @Override
  public Void visitCompExpr(StatParser.CompExprContext ctx) {
    return null;
  }

  @Override
  public Void visitLogicExpr(StatParser.LogicExprContext ctx) {
    return null;
  }

  @Override
  public Void visitMultDivideExpr(StatParser.MultDivideExprContext ctx) {
    return null;
  }

  @Override
  public Void visitPropertyExpr(StatParser.PropertyExprContext ctx) {
    return null;
  }

  @Override
  public Void visitFalseExpr(StatParser.FalseExprContext ctx) {
    return null;
  }

  @Override
  public Void visitReturnStmt(StatParser.ReturnStmtContext ctx) {
    return null;
  }

  @Override
  public Void visitAddSubExpr(StatParser.AddSubExprContext ctx) {
    return null;
  }

  @Override
  public Void visitNotExpr(StatParser.NotExprContext ctx) {
    return null;
  }

  @Override
  public Void visitIfStmt(StatParser.IfStmtContext ctx) {
    return null;
  }

  @Override
  public Void visitFuncDecl(StatParser.FuncDeclContext ctx) {
    return null;
  }

  @Override
  public Void visitVarDef(StatParser.VarDefContext ctx) {
    return null;
  }

  @Override
  public Void visitBlock(StatParser.BlockContext ctx) {
    return null;
  }

  @Override
  public Void visitVarDecl(StatParser.VarDeclContext ctx) {
    return null;
  }

  @Override
  public Void visitParameters(StatParser.ParametersContext ctx) {
    return null;
  }

  @Override
  public Void visitEqualExpr(StatParser.EqualExprContext ctx) {
    return null;
  }

  @Override
  public Void visitVarExpr(StatParser.VarExprContext ctx) {
    return null;
  }

  @Override
  public Void visitCallExpr(StatParser.CallExprContext ctx) {
    return null;
  }

  @Override
  public Void visit(ParseTree tree) {
    return null;
  }

  @Override
  public Void visitChildren(RuleNode node) {
    return null;
  }

  @Override
  public Void visitTerminal(TerminalNode node) {
    return null;
  }

  @Override
  public Void visitErrorNode(ErrorNode node) {
    return null;
  }
}
