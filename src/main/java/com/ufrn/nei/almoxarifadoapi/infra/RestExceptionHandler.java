package com.ufrn.nei.almoxarifadoapi.infra;

import com.ufrn.nei.almoxarifadoapi.exception.*;

import jakarta.mail.SendFailedException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class RestExceptionHandler {
        @ExceptionHandler({EntityNotFoundException.class, ItemNotActiveException.class, StatusNotFoundException.class})
        public ResponseEntity<RestErrorMessage> handleNotFoundStatus(RuntimeException exception,
                        HttpServletRequest request) {
                log.info("API ERROR - ", exception);
                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(new RestErrorMessage(request, HttpStatus.NOT_FOUND, exception.getMessage()));
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<RestErrorMessage> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                        HttpServletRequest request) {
                log.info("API ERROR - ", exception);

                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                                .body(new RestErrorMessage(request, HttpStatus.UNPROCESSABLE_ENTITY, "Campo(s) invalido(s)"));
        }

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<RestErrorMessage> handleHttpMessageNotReadableException(
                        HttpMessageNotReadableException exception,
                        HttpServletRequest request) {
                log.info("API ERROR - ", exception);

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new RestErrorMessage(request, HttpStatus.BAD_REQUEST, "Requisição inválida"));
        }

        @ExceptionHandler({PasswordInvalidException.class, InvalidRecoveryTokenException.class,
                OperationErrorException.class, NotAvailableQuantityException.class,
                ModifyStatusException.class})
        public ResponseEntity<RestErrorMessage> handleBadRequest(RuntimeException exception,
                        HttpServletRequest request) {
                log.info("API ERROR - ", exception);

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new RestErrorMessage(request, HttpStatus.BAD_REQUEST, exception.getMessage()));
        }

        @ExceptionHandler(DataIntegrityViolationException.class)
        public ResponseEntity<RestErrorMessage> handleErrorOnDatabase(DataIntegrityViolationException exception,
                        HttpServletRequest request) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new RestErrorMessage(request, HttpStatus.BAD_REQUEST,
                                                exception.getLocalizedMessage()));
        }

        @ExceptionHandler(UnauthorizedAccessException.class)
        public ResponseEntity<RestErrorMessage> handleErrorOnDatabase(UnauthorizedAccessException exception,
                                                                      HttpServletRequest request) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new RestErrorMessage(request, HttpStatus.UNAUTHORIZED,
                                exception.getLocalizedMessage()));
        }

        @ExceptionHandler(ConflictUpdatePasswordException.class)
        public ResponseEntity<RestErrorMessage> handleMultipleIntentionsOnUpdatePassword(ConflictUpdatePasswordException exception, HttpServletRequest request) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new RestErrorMessage(request, HttpStatus.CONFLICT,
                                exception.getLocalizedMessage()));
        }

        @ExceptionHandler({MailSendException.class, MailAuthenticationException.class})
        public ResponseEntity<RestErrorMessage> handleErrorOnMailSender(Exception exception,
                                                                      HttpServletRequest request) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new RestErrorMessage(request, HttpStatus.INTERNAL_SERVER_ERROR,
                                exception.getCause().getLocalizedMessage()));
        }
}
