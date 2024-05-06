package com.eno.stationlang.service.compiler;

import com.eno.stationlang.parser.StatLexer;
import com.eno.stationlang.parser.StatParser;
import com.eno.stationlang.service.compiler.backend.CodeGenerator;
import com.eno.stationlang.service.compiler.error.CompilationError;
import com.eno.stationlang.service.compiler.error.ErrorListener;
import com.eno.stationlang.service.compiler.frontend.TypeChecker;
import com.eno.stationlang.service.compiler.frontend.symboltable.GlobalScope;
import com.eno.stationlang.service.compiler.frontend.symboltable.Scope;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.springframework.stereotype.Service;

@Service
public class CompilerService {

  public CompilationResult compile(String code) {
    CodePointBuffer buffer = CodePointBuffer.withBytes(ByteBuffer.wrap(code.getBytes()));
    CharStream stream = CodePointCharStream.fromBuffer(buffer);

    List<CompilationError> errors = new ArrayList<>();
    ErrorListener listener = new ErrorListener(errors);

    StatLexer lexer = new StatLexer(stream);
    lexer.removeErrorListeners();
    lexer.addErrorListener(listener);
    CommonTokenStream tokens = new CommonTokenStream(lexer);

    StatParser parser = new StatParser(tokens);
    parser.removeErrorListeners();
    parser.addErrorListener(listener);
    ParseTree tree = parser.program();

    if (listener.hasError()) {
      // TODO: check if errors contains any AMBIGUITY or CONTEXT error
      return new CompilationResult("Not yet compiled.", errors);
    }

    Scope globalScope = new GlobalScope();
    TypeChecker typeChecker = new TypeChecker(listener, globalScope);
    ParseTreeWalker walker = new ParseTreeWalker();
    walker.walk(typeChecker, tree);
    CodeGenerator codeGenerator = new CodeGenerator(globalScope);
    codeGenerator.visit(tree);

    return new CompilationResult("Not yet compiled.", errors);
  }
}
