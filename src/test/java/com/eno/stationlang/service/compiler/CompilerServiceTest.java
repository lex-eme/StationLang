package com.eno.stationlang.service.compiler;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CompilerServiceTest {

  @Test
  void testCompile() {
    CompilerService service = new CompilerService();
    var res =
        service.compile(
            """
                number x = 4;
                number transformer = 0;

                compute(number n): number {
                  return x * n;
                }

                setup() {
                  number z = (10 + 5) - 2;
                  setNumber(transformer, "PowerRequired", compute(z));
                  setBoolean(transformer, "Lock", true);
                  number power = loadNumber(transformer, "PowerRequired");
                  boolean on = loadBoolean(transformer, "On");
                  if ("xxx" == "zzz") {
                    z = 10;
                  }
                }
                """);

    res.getErrors().forEach(compilationError -> System.out.println(compilationError.message()));
  }
}
