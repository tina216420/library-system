package springboot.librarysystem.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import springboot.librarysystem.entity.Loan;
import springboot.librarysystem.entity.Book;
import springboot.librarysystem.entity.User;
import springboot.librarysystem.entity.Library;
import springboot.librarysystem.entity.BookLocation;
import springboot.librarysystem.repository.LoanRepository;
import springboot.librarysystem.repository.BookRepository;
import springboot.librarysystem.repository.UserRepository;
import springboot.librarysystem.repository.LibraryRepository;
import springboot.librarysystem.repository.BookLocationRepository;
import java.util.Optional;
import java.util.List;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class LoanServiceTest {
    @Autowired
    private LoanService loanService;
    @MockBean
    private LoanRepository loanRepository;
    @MockBean
    private BookRepository bookRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private LibraryRepository libraryRepository;
    @MockBean
    private BookLocationRepository bookLocationRepository;

    @Test
    void testBorrowBook_success() {
        User user = new User(); user.setId(1L);
        Book book = new Book(); book.setId(2L); book.setType("書籍");
        Library library = new Library(); library.setId(3L);
        BookLocation location = new BookLocation(); location.setBook(book); location.setLibrary(library); location.setAvailableQuantity(5);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(bookRepository.findById(2L)).thenReturn(Optional.of(book));
        Mockito.when(libraryRepository.findById(3L)).thenReturn(Optional.of(library));
        Mockito.when(bookLocationRepository.findByBookIdAndLibraryId(2L, 3L)).thenReturn(location);
        Mockito.when(loanRepository.countOverdueLoansByUserId(1L, LocalDate.now())).thenReturn(0L);
        Mockito.when(loanRepository.countBorrowedByUserIdAndBookType(1L, "書籍")).thenReturn(0L);
        loanService.borrowBook(1L, 2L, 3L);
        assertEquals(4, location.getAvailableQuantity());
    }

    @Test
    void testBorrowBook_noStock() {
        User user = new User(); user.setId(1L);
        Book book = new Book(); book.setId(2L); book.setType("圖書");
        Library library = new Library(); library.setId(3L);
        BookLocation location = new BookLocation(); location.setBook(book); location.setLibrary(library); location.setAvailableQuantity(0);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(bookRepository.findById(2L)).thenReturn(Optional.of(book));
        Mockito.when(libraryRepository.findById(3L)).thenReturn(Optional.of(library));
        Mockito.when(bookLocationRepository.findByBookIdAndLibraryId(2L, 3L)).thenReturn(location);
        Exception ex = assertThrows(IllegalStateException.class, () -> loanService.borrowBook(1L, 2L, 3L));
        assertTrue(ex.getMessage().contains("Insufficient available quantity in this branch"));
    }

    @Test
    void testBorrowBook_limitReached() {
        User user = new User(); user.setId(1L);
        Book book = new Book(); book.setId(2L); book.setType("圖書");
        Library library = new Library(); library.setId(3L);
        BookLocation location = new BookLocation(); location.setBook(book); location.setLibrary(library); location.setAvailableQuantity(5);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(bookRepository.findById(2L)).thenReturn(Optional.of(book));
        Mockito.when(libraryRepository.findById(3L)).thenReturn(Optional.of(library));
        Mockito.when(bookLocationRepository.findByBookIdAndLibraryId(2L, 3L)).thenReturn(location);
        Mockito.when(loanRepository.countOverdueLoansByUserId(1L, LocalDate.now())).thenReturn(0L);
        Mockito.when(loanRepository.countBorrowedByUserIdAndBookType(1L, "圖書")).thenReturn(5L);
        Exception ex = assertThrows(IllegalStateException.class, () -> loanService.borrowBook(1L, 2L, 3L));
        assertTrue(ex.getMessage().contains("Borrowing limit reached"));
    }

    @Test
    void testBorrowBook_noUser() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Exception ex = assertThrows(IllegalArgumentException.class, () -> loanService.borrowBook(1L, 2L, 3L));
        assertTrue(ex.getMessage().contains("User, book, or library branch does not exist"));
    }

    @Test
    void testBorrowBook_noBook() {
        User user = new User(); user.setId(1L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(bookRepository.findById(2L)).thenReturn(Optional.empty());
        Exception ex = assertThrows(IllegalArgumentException.class, () -> loanService.borrowBook(1L, 2L, 3L));
        assertTrue(ex.getMessage().contains("User, book, or library branch does not exist"));
    }

    @Test
    void testBorrowBook_noLibrary() {
        User user = new User(); user.setId(1L);
        Book book = new Book(); book.setId(2L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(bookRepository.findById(2L)).thenReturn(Optional.of(book));
        Mockito.when(libraryRepository.findById(3L)).thenReturn(Optional.empty());
        Exception ex = assertThrows(IllegalArgumentException.class, () -> loanService.borrowBook(1L, 2L, 3L));
        assertTrue(ex.getMessage().contains("User, book, or library branch does not exist"));
    }

    
    @Test
    void testBorrowBook_overdueForbidden() {
        User user = new User(); user.setId(1L);
        Book book = new Book(); book.setId(2L); book.setType("圖書");
        Library library = new Library(); library.setId(3L);
        BookLocation location = new BookLocation(); location.setBook(book); location.setLibrary(library); location.setAvailableQuantity(5);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(bookRepository.findById(2L)).thenReturn(Optional.of(book));
        Mockito.when(libraryRepository.findById(3L)).thenReturn(Optional.of(library));
        Mockito.when(bookLocationRepository.findByBookIdAndLibraryId(2L, 3L)).thenReturn(location);
        Mockito.when(loanRepository.countOverdueLoansByUserId(1L, LocalDate.now())).thenReturn(2L); // Simulate overdue
        Exception ex = assertThrows(IllegalStateException.class, () -> loanService.borrowBook(1L, 2L, 3L));
        assertTrue(ex.getMessage().contains("You have overdue books. Please return them before borrowing new ones."));
    }

    @Test
    void testReturnBook_success() {
        User user = new User(); user.setId(1L);
        Book book = new Book(); book.setId(2L); book.setType("書籍");
        Library library = new Library(); library.setId(3L);
        BookLocation location = new BookLocation(); location.setBook(book); location.setLibrary(library); location.setAvailableQuantity(2);
        Loan loan = new Loan(); loan.setId(10L); loan.setUser(user); loan.setBook(book); loan.setLibrary(library); loan.setStatus(LoanService.STATUS_BORROWED); loan.setDueDate(LocalDate.now().plusDays(1));
        Mockito.when(loanRepository.findById(10L)).thenReturn(Optional.of(loan));
        Mockito.when(bookLocationRepository.findByBookIdAndLibraryId(2L, 3L)).thenReturn(location);
        loanService.returnBook(10L);
        assertEquals(3, location.getAvailableQuantity());
        assertEquals(LoanService.STATUS_RETURNED, loan.getStatus());
    }

    @Test
    void testReturnBook_overdue() {
        User user = new User(); user.setId(1L);
        Book book = new Book(); book.setId(2L); book.setType("圖書");
        Library library = new Library(); library.setId(3L);
        BookLocation location = new BookLocation(); location.setBook(book); location.setLibrary(library); location.setAvailableQuantity(2);
        Loan loan = new Loan(); loan.setId(11L); loan.setUser(user); loan.setBook(book); loan.setLibrary(library); loan.setStatus(LoanService.STATUS_BORROWED); loan.setDueDate(LocalDate.now().minusDays(1));
        Mockito.when(loanRepository.findById(11L)).thenReturn(Optional.of(loan));
        Mockito.when(bookLocationRepository.findByBookIdAndLibraryId(2L, 3L)).thenReturn(location);
        loanService.returnBook(11L);
        assertEquals(3, location.getAvailableQuantity());
        assertEquals(LoanService.STATUS_OVERDUE, loan.getStatus());
    }

    @Test
    void testReturnBook_notFound() {
        Mockito.when(loanRepository.findById(12L)).thenReturn(Optional.empty());
        Exception ex = assertThrows(IllegalArgumentException.class, () -> loanService.returnBook(12L));
        assertTrue(ex.getMessage().contains("Loan record does not exist"));
    }

    @Test
    void testNotifyDueSoon() {
        User user = new User(); user.setUsername("testuser");
        Book book = new Book(); book.setTitle("Java Book");
        Loan loan = new Loan(); loan.setUser(user); loan.setBook(book);
        List<Loan> dueSoonLoans = List.of(loan);
        java.sql.Date sqlDueSoonDate = java.sql.Date.valueOf(java.time.LocalDate.now().plusDays(5));
        Mockito.when(loanRepository.findDueSoonLoans(sqlDueSoonDate)).thenReturn(dueSoonLoans);
        int result = loanService.notifyDueSoon();
        assertEquals(1, result);
    }

    @Test
    void testBorrowBook_transactionRollback() {
        User user = new User(); user.setId(1L);
        Book book = new Book(); book.setId(2L); book.setType("圖書");
        Library library = new Library(); library.setId(3L);
        BookLocation location = new BookLocation(); location.setBook(book); location.setLibrary(library); location.setAvailableQuantity(1);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(bookRepository.findById(2L)).thenReturn(Optional.of(book));
        Mockito.when(libraryRepository.findById(3L)).thenReturn(Optional.of(library));
        Mockito.when(bookLocationRepository.findByBookIdAndLibraryId(2L, 3L)).thenReturn(location);
        Mockito.when(loanRepository.countOverdueLoansByUserId(1L, LocalDate.now())).thenReturn(0L);
        Mockito.when(loanRepository.countBorrowedByUserIdAndBookType(1L, "圖書")).thenReturn(0L);
        // Exception occurred when save
        Mockito.doThrow(new org.springframework.transaction.TransactionSystemException("DB error")).when(loanRepository).save(Mockito.any(Loan.class));
        assertThrows(org.springframework.transaction.TransactionSystemException.class, () -> loanService.borrowBook(1L, 2L, 3L));
        // Check that inventory has not been reduced
        assertEquals(1, location.getAvailableQuantity());
        // loanRepository.save is called only once
        Mockito.verify(loanRepository, Mockito.times(1)).save(Mockito.any(Loan.class));
    }

    @Test
    void testReturnBook_transactionRollback() {
        User user = new User(); user.setId(1L);
        Book book = new Book(); book.setId(2L); book.setType("圖書");
        Library library = new Library(); library.setId(3L);
        BookLocation location = new BookLocation(); location.setBook(book); location.setLibrary(library); location.setAvailableQuantity(2);
        Loan loan = new Loan(); loan.setId(20L); loan.setUser(user); loan.setBook(book); loan.setLibrary(library); loan.setStatus(LoanService.STATUS_BORROWED); loan.setDueDate(LocalDate.now().plusDays(1));
        Mockito.when(loanRepository.findById(20L)).thenReturn(Optional.of(loan));
        Mockito.when(bookLocationRepository.findByBookIdAndLibraryId(2L, 3L)).thenReturn(location);
        // Exception occurred when save
        Mockito.doThrow(new org.springframework.transaction.TransactionSystemException("DB error")).when(loanRepository).save(Mockito.any(Loan.class));
        assertThrows(org.springframework.transaction.TransactionSystemException.class, () -> loanService.returnBook(20L));
        // Check that inventory has not been increased
        assertEquals(2, location.getAvailableQuantity());
        // loanRepository.save is called only once
        Mockito.verify(loanRepository, Mockito.times(1)).save(Mockito.any(Loan.class));
    }
}
