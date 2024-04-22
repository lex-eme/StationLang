package com.eno.stationlang.service.compiler.frontend.symboltable;

import com.eno.stationlang.service.compiler.frontend.StatType;

public interface Symbol {

  String getName();

  StatType getType();
}
