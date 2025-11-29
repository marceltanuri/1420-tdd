package io.github.marceltanuri.estacionamento.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Trata IllegalStateException, que geralmente indica erros de regras de negócio.
     * Mapeia a exceção para HTTP 400 Bad Request, retornando a mensagem da exceção como corpo da resposta.
     * Isso permite que os testes usem .andExpect(status().isBadRequest()) e .andExpect(content().string(expectedMessage)).
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException ex) {
        // Retorna a mensagem de erro do domínio com status 400
        // O teste espera que o corpo da resposta seja apenas esta string.
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}