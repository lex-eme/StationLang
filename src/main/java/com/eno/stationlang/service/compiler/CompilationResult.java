package com.eno.stationlang.service.compiler;

import com.eno.stationlang.service.compiler.error.CompilationError;
import java.util.List;

public class CompilationResult {
  private final String compiledCode;
  private final List<CompilationError> errors;

  public CompilationResult(String compiledCode, List<CompilationError> errors) {
    this.compiledCode = compiledCode;
    this.errors = errors;
  }

  public String getCompiledCode() {
    return compiledCode;
  }

  public List<CompilationError> getErrors() {
    return errors;
  }
}
