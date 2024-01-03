package com.is4tech.base.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.is4tech.base.dto.ErrorDTO;
import com.is4tech.base.util.Utilities;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;

@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    // Jackson JSON serializer instance
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        var err = handleApiError(ex, request);
        return new ResponseEntity<>(err.getBody(), err.getHeaders(), err.getStatusCode());
    }

    @ApiResponse(description = "Error") // Esta línea es para OpenApi
    @ExceptionHandler({ Exception.class })
    public ResponseEntity<ErrorDTO> handleAll(Exception ex, WebRequest request) {
        return handleApiError(ex, request);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        var httpStatus = HttpStatus.UNAUTHORIZED; // 401
        ErrorDTO error = Utilities.getError(httpStatus.value(), null);
        Utilities.errorLog(request, httpStatus, error.getError().getDescription(), authException);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(httpStatus.value());
        response.getOutputStream().write(objectMapper.writeValueAsBytes(error));
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception) throws IOException, ServletException {
        var httpStatus = HttpStatus.FORBIDDEN; // 403
        ErrorDTO error = Utilities.getError(httpStatus.value(), null);
        Utilities.errorLog(request, httpStatus, error.getError().getDescription(), exception);
        response.setStatus(httpStatus.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getOutputStream().write(objectMapper.writeValueAsBytes(error));
    }

    public ResponseEntity<ErrorDTO> handleApiError(Exception ex, WebRequest request) {
        int errorCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
        String message = null;
        if (ex instanceof NoHandlerFoundException) {
            errorCode = 404;
        } else if (ex instanceof HttpRequestMethodNotSupportedException) {
            errorCode = 405;
        }  else if (ex instanceof HttpMessageNotReadableException) {
            errorCode = 400;
        } else {
            message = ex.getMessage();
            if (message == null || message.isEmpty()) {
                message = ex.toString();
            }
            ResponseStatus annotation = ex.getClass().getAnnotation(ResponseStatus.class);
            if (annotation != null) {
                errorCode = annotation.value().value();
            }
        }
        HttpServletRequest hsq = null;
        if (request instanceof ServletWebRequest) {
            hsq = ((ServletWebRequest) request).getRequest();
        }

        ErrorDTO error = Utilities.getError(errorCode, message);
        Utilities.errorLog(hsq, HttpStatus.valueOf(errorCode), error.getError().getDescription(), ex);

        var headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return new ResponseEntity<>(error, headers, HttpStatus.valueOf(errorCode));
    }
}
