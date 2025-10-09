package springboot.librarysystem.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import springboot.librarysystem.service.LoanService;
import springboot.librarysystem.service.UserService;
import springboot.librarysystem.config.SecurityConfig;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoanController.class)
@Import(SecurityConfig.class)
public class LoanControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private LoanService loanService;
    @MockitoBean
    private UserService userService;

    @Test
    void testBorrowBook_success() throws Exception {
        Mockito.doNothing().when(loanService).borrowBook(1L, 2L, 3L);
        mockMvc.perform(post("/api/loans/borrow")
                .param("userId", "1")
                .param("bookId", "2")
                .param("libraryId", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Book borrowed successfully"));
    }

    @Test
    void testBorrowBook_limitReached() throws Exception {
        Mockito.doThrow(new IllegalStateException("Insufficient available quantity in this branch")).when(loanService).borrowBook(1L, 2L, 3L);
        mockMvc.perform(post("/api/loans/borrow")
                .param("userId", "1")
                .param("bookId", "2")
                .param("libraryId", "3"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Insufficient available quantity in this branch"));
    }

    @Test
    void testBorrowBook_fail() throws Exception {
        Mockito.doThrow(new IllegalArgumentException("User, book, or library branch does not exist")).when(loanService).borrowBook(1L, 2L, 3L);
        mockMvc.perform(post("/api/loans/borrow")
                .param("userId", "1")
                .param("bookId", "2")
                .param("libraryId", "3"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User, book, or library branch does not exist"));
    }

    @Test
    void testBorrowBook_transactionException() throws Exception {
        Mockito.doThrow(new org.springframework.transaction.TransactionSystemException("tx error")).when(loanService).borrowBook(1L, 2L, 3L);
        mockMvc.perform(post("/api/loans/borrow")
                .param("userId", "1")
                .param("bookId", "2")
                .param("libraryId", "3"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Transaction failed: Database transaction failed or data is inconsistent"));
    }

    @Test
    void testReturnBook_success() throws Exception {
        Mockito.doNothing().when(loanService).returnBook(10L);
        mockMvc.perform(post("/api/loans/return")
                .param("loanId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Book returned successfully"));
    }

    @Test
    void testReturnBook_fail() throws Exception {
        Mockito.doThrow(new IllegalArgumentException("Loan record does not exist or already returned")).when(loanService).returnBook(11L);
        mockMvc.perform(post("/api/loans/return")
                .param("loanId", "11"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Loan record does not exist or already returned"));
    }

    @Test
    void testNotifyDueSoon() throws Exception {
        Mockito.when(loanService.notifyDueSoon()).thenReturn(2);
        mockMvc.perform(get("/api/loans/notify-due-soon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Notification sent (simulated): 2 emails"));
    }
}
