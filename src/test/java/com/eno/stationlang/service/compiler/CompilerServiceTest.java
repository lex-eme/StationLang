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
                        number battery = 0;
                        number generator = 1;

                        number minRatio = 0.2;
                        number maxRatio = 0.4;

                        setGeneratorOn(boolean on) {
                            number on = 10;
                            writeBool(generator, "On", on);
                        }

                        setup() {
                            writeBool(generator, "Lock", true);
                            setGeneratorOn(false);
                        }

                        update() {
                            boolean shouldRun = false;
                            number ratio = readNumber(battery, "Ratio");
                            boolean isRunning = readBoolean(generator, "On");

                            if (isRunning) {
                                shouldRun = ratio > maxRatio;
                            } else {
                                shouldRun = ratio < minRatio;
                            }

                            setGeneratorOn(shouldRun);
                        }
                    """);

    res.getErrors().forEach(compilationError -> System.out.println(compilationError.message()));
  }
}
