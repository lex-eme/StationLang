package com.eno.stationlang.service.compiler;

import org.junit.jupiter.api.Test;

class CompilerServiceTest {

  @Test
  void testCompile() {
    CompilerService service = new CompilerService();
    var res =
        service.compile(
            """
                        device battery = 0;
                        device generator = 1;

                        const number minRatio = 0.2;
                        const number maxRatio = 0.4;

                        setup() {
                            writeBoolean(generator, "Lock", true);
                            writeBoolean(generator, "On", false);
                        }

                        update() {
                            boolean shouldRun = false;
                            number ratio = readNumber(battery, "Ratio");
                            boolean isRunning = readBoolean(generator, "On");

                            if (isRunning) {
                                shouldRun = ratio < maxRatio;
                            } else {
                                shouldRun = ratio < minRatio;
                            }

                            writeBoolean(generator, "On", shouldRun);
                        }
                    """);

    res.errors().forEach(compilationError -> System.out.println(compilationError.message()));
  }

  @Test
  void testCompile2() {
    CompilerService service = new CompilerService();
    var res =
        service.compile(
            """
                                device inLine = 0;
                                device outLine = 1;
                                device transformer = 2;

                                setup() {
                                  writeBoolean(transformer, "On", false);
                                }

                                update() {
                                  number requiredPower = readNumber(transformer, "RequiredPower");

                                  number x = readNumber(inLine, "PowerPotential") - readNumber(outLine, "PowerActual");

                                  if (readBoolean(transformer, "On")) {
                                    x = x + requiredPower;
                                  }

                                  writeBoolean(transformer, "On", x > requiredPower);
                                }
                            """);

    res.errors().forEach(compilationError -> System.out.println(compilationError.message()));
  }
}
