package com.eno.stationlang.service.compiler.error;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ErrorListenerTest {

    @Test
    void syntaxErrorTest() {
        List<CompilationError> errors = new ArrayList<>();
        ErrorListener listener = new ErrorListener(errors);

        listener.syntaxError(null, 0, 10, 20, "message", null);

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

        listener.reportAmbiguity(null, null, 10, 20, true, null, null);

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

        listener.reportAttemptingFullContext(null, null, 10, 20, null, null);

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

        listener.reportContextSensitivity(null, null, 10, 20, 0, null);

        assertEquals(1, errors.size());
        CompilationError error = errors.get(0);
        assertEquals(10, error.line());
        assertEquals(20, error.charPosInLine());
        assertNull(error.message());
        assertEquals(ErrorType.CONTEXT, error.type());
    }

}