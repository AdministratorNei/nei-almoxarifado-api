package com.ufrn.nei.almoxarifadoapi.infra;

import com.ufrn.nei.almoxarifadoapi.exception.*;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class RestExceptionHandler {
        @ExceptionHandler(EntityNotFoundException.class)
        public ResponseEntity<RestErrorMessage> handleItemNotFoundException(EntityNotFoundException exception,
                        HttpServletRequest request) {
                log.info("API ERROR - ", exception);
                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(new RestErrorMessage(request, HttpStatus.BAD_REQUEST, exception.getMessage()));
        }

        @ExceptionHandler(CreateEntityException.class)
        public ResponseEntity<RestErrorMessage> handleCreateEntityException(CreateEntityException exception,
                        HttpServletRequest request) {
                log.info("API ERROR - ", exception);

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new RestErrorMessage(request, HttpStatus.BAD_REQUEST, exception.getMessage()));
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<RestErrorMessage> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                        HttpServletRequest request) {
                log.info("API ERROR - ", exception);

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new RestErrorMessage(request, HttpStatus.BAD_REQUEST, "Campo(s) invalido(s)"));
        }

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<RestErrorMessage> handleHttpMessageNotReadableException(
                        HttpMessageNotReadableException exception,
                        HttpServletRequest request) {
                log.info("API ERROR - ", exception);

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new RestErrorMessage(request, HttpStatus.BAD_REQUEST, "Campo(s) invalido(s)"));
        }

        @ExceptionHandler(ItemNotActiveException.class)
        public ResponseEntity<RestErrorMessage> handleItemNotActiveException(ItemNotActiveException exception,
                        HttpServletRequest request) {
                log.info("API ERROR - ", exception);

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new RestErrorMessage(request, HttpStatus.BAD_REQUEST, exception.getMessage()));
        }

        @ExceptionHandler(NotAvailableQuantityException.class)
        public ResponseEntity<RestErrorMessage> handleQuantityNotAvailableToLend(
                        NotAvailableQuantityException exception,
                        HttpServletRequest request) {
                log.info("API ERROR - ", exception);

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new RestErrorMessage(request, HttpStatus.BAD_REQUEST, exception.getMessage()));
        }

        @ExceptionHandler(PasswordInvalidException.class)
        public ResponseEntity<RestErrorMessage> handlePasswordInvalidException(PasswordInvalidException exception,
                        HttpServletRequest request) {
                log.info("API ERROR - ", exception);

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new RestErrorMessage(request, HttpStatus.BAD_REQUEST, exception.getMessage()));
        }

        @ExceptionHandler(OperationErrorException.class)
        public ResponseEntity<RestErrorMessage> handlePasswordInvalidException(OperationErrorException exception,
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
}
