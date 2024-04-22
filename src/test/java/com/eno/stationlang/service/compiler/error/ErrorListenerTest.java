package com.eno.stationlang.service.compiler.error;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ErrorListenerTest {

  @Test
  void syntaxErrorTest() {
    List<CompilationError> errors = new ArrayList<>();
    ErrorListener listener = new ErrorListener(errors);

    assertFalse(listener.hasError());
    listener.syntaxError(null, 0, 10, 20, "message", null);

    assertTrue(listener.hasError());
    assertEquals(1, errors.size());
    CompilationError error = errors.get(0);
    assertEquals(10, error.line());
    assertEquals(20, error.charPosInLine());
    assertEquals("message", error.message());
    assertEquals(ErrorType.SYNTAX, error.type());
  }

  @Test
  void reportAmbiguityTest() {
    List<CompilationError> errors = new ArrayList<>();
    ErrorListener listener = new ErrorListener(errors);

    assertFalse(listener.hasError());
    listener.reportAmbiguity(null, null, 10, 20, true, null, null);

    assertTrue(listener.hasError());
    assertEquals(1, errors.size());
    CompilationError error = errors.get(0);
    assertEquals(10, error.line());
    assertEquals(20, error.charPosInLine());
    assertNull(error.message());
    assertEquals(ErrorType.AMBIGUITY, error.type());
  }

  @Test
  void reportAttemptingFullContextTest() {
    List<CompilationError> errors = new ArrayList<>();
    ErrorListener listener = new ErrorListener(errors);

    assertFalse(listener.hasError());
    listener.reportAttemptingFullContext(null, null, 10, 20, null, null);

    assertTrue(listener.hasError());
    assertEquals(1, errors.size());
    CompilationError error = errors.get(0);
    assertEquals(10, error.line());
    assertEquals(20, error.charPosInLine());
    assertNull(error.message());
    assertEquals(ErrorType.CONTEXT, error.type());
  }

  @Test
  void reportContextSensitivityTest() {
    List<CompilationError> errors = new ArrayList<>();
    ErrorListener listener = new ErrorListener(errors);

    assertFalse(listener.hasError());
    listener.reportContextSensitivity(null, null, 10, 20, 0, null);

    assertTrue(listener.hasError());
    assertEquals(1, errors.size());
    CompilationError error = errors.get(0);
    assertEquals(10, error.line());
    assertEquals(20, error.charPosInLine());
    assertNull(error.message());
    assertEquals(ErrorType.CONTEXT, error.type());
  }

  @Test
  void semanticErrorTest() {
    List<CompilationError> errors = new ArrayList<>();
    ErrorListener listener = new ErrorListener(errors);

    assertFalse(listener.hasError());
    listener.semanticError(10, 20, "message");

    assertEquals(1, errors.size());
    CompilationError error = errors.get(0);
    assertEquals(10, error.line());
    assertEquals(20, error.charPosInLine());
    assertEquals("message", error.message());
    assertEquals(ErrorType.SEMANTIC, error.type());
  }
}
