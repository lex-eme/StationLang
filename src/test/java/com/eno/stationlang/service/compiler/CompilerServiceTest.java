package com.eno.stationlang.service.compiler;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CompilerServiceTest {

  @Test
  void testCompile() {
    CompilerService service = new CompilerService();
    service.compile(
        """
                number x = 4;

                setup() {

                }""");
  }
}
