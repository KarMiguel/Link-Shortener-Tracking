package io.github.karMiguel.capzip.exceptions.handler;

import io.github.karMiguel.capzip.exceptions.EntityNotFoundException;
import io.github.karMiguel.capzip.exceptions.InvalidJwtAuthenticationException;
import io.github.karMiguel.capzip.exceptions.UrlRedirectException;
import io.github.karMiguel.capzip.exceptions.UsernameUniqueViolationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorMessage> accessDeniedException(
            AccessDeniedException ex,
            HttpServletRequest request
    ) {
        log.error("Acesso negado - {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, HttpStatus.FORBIDDEN,  ex.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorMessage> entityNotFoundException(
            EntityNotFoundException ex,
            HttpServletRequest request
    ) {
        log.error("Entidade não encontrada - {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, HttpStatus.NOT_FOUND,  ex.getMessage()));
    }

    @ExceptionHandler(UsernameUniqueViolationException.class)
    public ResponseEntity<ErrorMessage> usernameUniqueViolationException(
            UsernameUniqueViolationException ex,
            HttpServletRequest request
    ) {
        log.error("Violacao de nome de usuário único - {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, HttpStatus.CONFLICT, "Nome de usuário já existente."));
    }
    @ExceptionHandler(InvalidJwtAuthenticationException.class)
    public ResponseEntity<ErrorMessage> invalidJwtAuthenticationException(
            InvalidJwtAuthenticationException ex,
            HttpServletRequest request
    ) {
        log.error("Autenticação JWT inválida - {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, HttpStatus.UNAUTHORIZED, ex.getMessage()));
    }
    @ExceptionHandler(UrlRedirectException.class)
    public ResponseEntity<ErrorMessage> UrlRedirectException(
            UrlRedirectException ex,
            HttpServletRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, HttpStatus.BAD_REQUEST, "Url inválida!"));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> methodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request,
            BindingResult result
    ) {
        log.error("Argumento inválido - {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, HttpStatus.UNPROCESSABLE_ENTITY,  ex.getMessage(), result));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> genericExceptionHandler(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("Erro interno do servidor - {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor."));
    }
}
