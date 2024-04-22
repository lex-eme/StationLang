package com.eno.stationlang.service.compiler.error;

public interface SemanticErrorListener {
  void semanticError(int line, int charPosInLine, String message);
}
