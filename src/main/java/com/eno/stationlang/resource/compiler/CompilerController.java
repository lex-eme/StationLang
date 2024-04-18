package com.eno.stationlang.resource.compiler;

import com.eno.stationlang.resource.compiler.dto.CodeDTO;
import com.eno.stationlang.resource.compiler.dto.ErrorDTO;
import com.eno.stationlang.resource.compiler.dto.ResponseDTO;
import com.eno.stationlang.service.compiler.CompilerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "compile")
public class CompilerController {

  @Autowired CompilerService compilerService;

  @RequestMapping(
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public ResponseDTO compileCode(@RequestBody CodeDTO code) {
    var res = compilerService.compile(code.code());

    return new ResponseDTO(
        res.getCompiledCode(),
        res.getErrors().stream()
            .map(
                compilationError ->
                    new ErrorDTO(
                        compilationError.line(),
                        compilationError.charPosInLine(),
                        compilationError.message()))
            .toList());
  }
}
