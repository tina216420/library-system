package springboot.librarysystem.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import springboot.librarysystem.dto.ApiResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleTransactionSystemException(TransactionSystemException ex) {
    ApiResponseDto<Void> response = new ApiResponseDto<>(500, "Transaction failed: Database transaction failed or data is inconsistent", null);
        return ResponseEntity.internalServerError().body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        ApiResponseDto<Void> response = new ApiResponseDto<>(400, ex.getMessage(), null);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleIllegalState(IllegalStateException ex) {
        ApiResponseDto<Void> response = new ApiResponseDto<>(400, ex.getMessage(), null);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(org.springframework.security.core.userdetails.UsernameNotFoundException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleUsernameNotFound(org.springframework.security.core.userdetails.UsernameNotFoundException ex) {
        ApiResponseDto<Void> response = new ApiResponseDto<>(404, ex.getMessage(), null);
        return ResponseEntity.status(404).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<Void>> handleOther(Exception ex) {
        log.error("Unexpected error occurred", ex);
        ApiResponseDto<Void> response = new ApiResponseDto<>(500, "Internal server error", null);
        return ResponseEntity.internalServerError().body(response);
    }
}
