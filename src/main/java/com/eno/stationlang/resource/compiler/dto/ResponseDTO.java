package com.eno.stationlang.resource.compiler.dto;

import java.util.List;

public record ResponseDTO(String compiledCode, List<ErrorDTO> errors) {}
