package com.ufrn.nei.almoxarifadoapi.infra;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
public class RestErrorMessage {
    private String hour = LocalDateTime.now(ZoneId.systemDefault()).toString();
    private String method;
    private int status;
    private String statusText;
    private String message;
    private String path;

    public RestErrorMessage(HttpServletRequest request, HttpStatus status, String message) {
        this.path = request.getRequestURI();
        this.method = request.getMethod();
        this.status = status.value();
        this.statusText = status.getReasonPhrase();
        this.message = message;
    }
}
