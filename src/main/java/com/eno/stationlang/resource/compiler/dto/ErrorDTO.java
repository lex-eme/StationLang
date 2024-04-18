package com.eno.stationlang.resource.compiler.dto;

public record ErrorDTO(int line, int posInLine, String message) {}
