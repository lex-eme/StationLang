package com.eno.stationlang.service.compiler;

import com.eno.stationlang.service.compiler.error.CompilationError;
import java.util.List;

public record CompilationResult(String compiledCode, List<CompilationError> errors) {}
