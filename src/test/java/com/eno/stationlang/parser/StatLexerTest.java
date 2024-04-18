package com.eno.stationlang.parser;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.ByteBuffer;
import org.antlr.v4.runtime.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class StatLexerTest {

  @ParameterizedTest
  @ValueSource(strings = {"id", "id123", "someRandomID"})
  void validIDTest(String id) {
    var lexer = getLexer(id);
    var token = lexer.nextToken();

    assertEquals(StatLexer.ID, token.getType());
    assertEquals(id, token.getText());
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"0", "1", "987", "10.0", "0.00456", "-0", "-1", "-987", "-10.0", "-0.00456"})
  void validNUMBERTest(String number) {
    var lexer = getLexer(number);
    var token = lexer.nextToken();

    assertEquals(StatLexer.NUMBER, token.getType());
    assertEquals(number, token.getText());
  }

  @ParameterizedTest
  @ValueSource(strings = {"010", "0.0145.30"})
  void invalidNUMBERTest(String number) {
    var lexer = getLexer(number);
    var token = lexer.nextToken();
    assertNotEquals(number, token.getText());
  }

  private StatLexer getLexer(String code) {
    CodePointBuffer buffer = CodePointBuffer.withBytes(ByteBuffer.wrap(code.getBytes()));
    CharStream stream = CodePointCharStream.fromBuffer(buffer);
    return new StatLexer(stream);
  }
}
