package springboot.librarysystem.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import springboot.librarysystem.dto.ApiResponseDto;

import static org.junit.jupiter.api.Assertions.*;

public class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testHandleTransactionSystemException() {
        org.springframework.transaction.TransactionSystemException ex = new org.springframework.transaction.TransactionSystemException("tx error");
        ResponseEntity<ApiResponseDto<Void>> resp = handler.handleTransactionSystemException(ex);
        assertEquals(500, resp.getStatusCodeValue());
        assertNotNull(resp.getBody());
        assertEquals("Transaction failed: Database transaction failed or data is inconsistent", resp.getBody().getMessage());
    }
    
    @Test
    void testHandleIllegalArgument() {
        IllegalArgumentException ex = new IllegalArgumentException("bad arg");
        ResponseEntity<ApiResponseDto<Void>> resp = handler.handleIllegalArgument(ex);
        assertEquals(400, resp.getStatusCodeValue());
        assertNotNull(resp.getBody());
        assertEquals("bad arg", resp.getBody().getMessage());
    }

    @Test
    void testHandleIllegalState() {
        IllegalStateException ex = new IllegalStateException("bad state");
        ResponseEntity<ApiResponseDto<Void>> resp = handler.handleIllegalState(ex);
        assertEquals(400, resp.getStatusCodeValue());
        assertNotNull(resp.getBody());
        assertEquals("bad state", resp.getBody().getMessage());
    }

    @Test
    void testHandleUsernameNotFound() {
        UsernameNotFoundException ex = new UsernameNotFoundException("user not found");
        ResponseEntity<ApiResponseDto<Void>> resp = handler.handleUsernameNotFound(ex);
        assertEquals(404, resp.getStatusCodeValue());
        assertNotNull(resp.getBody());
        assertEquals("user not found", resp.getBody().getMessage());
    }

    @Test
    void testHandleOther() {
        Exception ex = new Exception("any error");
        ResponseEntity<ApiResponseDto<Void>> resp = handler.handleOther(ex);
        assertEquals(500, resp.getStatusCodeValue());
        assertNotNull(resp.getBody());
        assertEquals("Internal server error", resp.getBody().getMessage());
    }
}
