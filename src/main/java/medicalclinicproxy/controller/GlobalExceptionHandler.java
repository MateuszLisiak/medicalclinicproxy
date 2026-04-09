package medicalclinicproxy.controller;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import medicalclinicproxy.exception.MedicalclinicException;
import medicalclinicproxy.model.dto.ErrorDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MedicalclinicException.class)
    public ResponseEntity<ErrorDto> handleMedicalclinicException(MedicalclinicException ex) {
        ErrorDto error = new ErrorDto(
                ex.getHttpStatus(),
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(error);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorDto> handleFeignException(FeignException ex) {

        int status = ex.status();

        ErrorDto error = new ErrorDto(
                org.springframework.http.HttpStatus.BAD_GATEWAY,
                "Feign error - status: " + status,
                java.time.LocalDateTime.now()
        );

        return ResponseEntity
                .status(502)
                .body(error);
    }
}