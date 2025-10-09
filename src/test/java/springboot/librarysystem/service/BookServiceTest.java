package springboot.librarysystem.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import springboot.librarysystem.entity.Book;
import springboot.librarysystem.entity.BookLocation;
import springboot.librarysystem.entity.Library;
import springboot.librarysystem.repository.BookRepository;
import springboot.librarysystem.repository.BookLocationRepository;
import springboot.librarysystem.repository.LibraryRepository;
import springboot.librarysystem.dto.LocationRequestDto;

import java.util.Optional;
import java.util.List;
import java.util.Collections;

@SpringBootTest
public class BookServiceTest {
    @Autowired
    private BookService bookService;

    @MockitoBean
    private BookRepository bookRepository;
    @MockitoBean
    private LibraryRepository libraryRepository;
    @MockitoBean
    private BookLocationRepository bookLocationRepository;

    @Test
    void testAddBookLocation_success() {
        Book book = new Book();
        book.setId(1L);
        Library library = new Library();
        library.setId(2L);
        LocationRequestDto req = new LocationRequestDto();
        req.libraryId = 2L;
        req.totalQuantity = 10;
        req.availableQuantity = 8;

        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        Mockito.when(libraryRepository.findById(2L)).thenReturn(Optional.of(library));
        Mockito.when(bookLocationRepository.findByBookIdAndLibraryId(1L, 2L)).thenReturn(null);

        assertDoesNotThrow(() -> bookService.addBookLocation(1L, req));
    }

    @Test
    void testAddBookLocation_bookNotFound() {
        LocationRequestDto req = new LocationRequestDto();
        req.libraryId = 2L;
        req.totalQuantity = 10;
        req.availableQuantity = 8;
        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> bookService.addBookLocation(1L, req));
    }

    @Test
    void testAddBookLocation_libraryNotFound() {
        Book book = new Book();
        book.setId(1L);
        LocationRequestDto req = new LocationRequestDto();
        req.libraryId = 2L;
        req.totalQuantity = 10;
        req.availableQuantity = 8;
        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        Mockito.when(libraryRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> bookService.addBookLocation(1L, req));
    }

    @Test
    void testAddBookLocation_duplicateLocation() {
        Book book = new Book();
        book.setId(1L);
        Library library = new Library();
        library.setId(2L);
        BookLocation existing = new BookLocation();
        LocationRequestDto req = new LocationRequestDto();
        req.libraryId = 2L;
        req.totalQuantity = 10;
        req.availableQuantity = 8;
        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        Mockito.when(libraryRepository.findById(2L)).thenReturn(Optional.of(library));
        Mockito.when(bookLocationRepository.findByBookIdAndLibraryId(1L, 2L)).thenReturn(existing);
        assertThrows(IllegalArgumentException.class, () -> bookService.addBookLocation(1L, req));
    }

    @Test
    void testUpdateBookLocation_success() {
        BookLocation bookLocation = new BookLocation();
        Library library = new Library();
        library.setName("Main Branch");
        bookLocation.setLibrary(library);
        bookLocation.setTotalQuantity(5);
        bookLocation.setAvailableQuantity(3);

        LocationRequestDto req = new LocationRequestDto();
        req.libraryId = 2L;
        req.totalQuantity = 10;
        req.availableQuantity = 8;

        Mockito.when(bookLocationRepository.findByBookIdAndLibraryId(1L, 2L)).thenReturn(bookLocation);
        Mockito.when(bookLocationRepository.save(Mockito.any(BookLocation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookLocation updated = bookService.updateBookLocation(1L, req);
        assertEquals(10, updated.getTotalQuantity());
        assertEquals(8, updated.getAvailableQuantity());
    }

    @Test
    void testAddBook_success() {
        Book book = new Book();
        book.setTitle("Test Book");
        Mockito.when(bookRepository.save(Mockito.any(Book.class))).thenReturn(book);
        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(
            new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("librarian", null, java.util.List.of(() -> "ROLE_LIBRARIAN")));
        Book result = bookService.addBook(book);
        assertEquals("Test Book", result.getTitle());
    }

    @Test
    void testUpdateBook_success() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Old Title");
        Book updatedBook = new Book();
        updatedBook.setTitle("New Title");
        updatedBook.setAuthor("Author");
        updatedBook.setPublicationYear(2023);
        updatedBook.setType("Type");
        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        Mockito.when(bookRepository.save(Mockito.any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Book result = bookService.updateBook(1L, updatedBook);
        assertEquals("New Title", result.getTitle());
        assertEquals("Author", result.getAuthor());
        assertEquals(2023, result.getPublicationYear());
        assertEquals("Type", result.getType());
    }

    @Test
    void testUpdateBook_notFound() {
        Book updatedBook = new Book();
        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> bookService.updateBook(1L, updatedBook));
    }

    @Test
    void testSearchBooks_success() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Search Title");
        Mockito.when(bookRepository.searchBooksBySql("Search Title", null, null)).thenReturn(List.of(book));
        Mockito.when(bookLocationRepository.findByBookId(1L)).thenReturn(Collections.emptyList());
        var result = bookService.searchBooks("Search Title", null, null);
        assertEquals(1, result.size());
        assertEquals("Search Title", result.get(0).title);
    }

    @Test
    void testUpdateBookLocation_notFound() {
        LocationRequestDto req = new LocationRequestDto();
        req.libraryId = 2L;
        req.totalQuantity = 10;
        req.availableQuantity = 8;
        Mockito.when(bookLocationRepository.findByBookIdAndLibraryId(1L, 2L)).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> bookService.updateBookLocation(1L, req));
    }
}
