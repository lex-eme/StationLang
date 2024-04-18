package com.eno.stationlang.service.compiler.error;

public record CompilationError(int line, int charPosInLine, ErrorType type, String message) {}
