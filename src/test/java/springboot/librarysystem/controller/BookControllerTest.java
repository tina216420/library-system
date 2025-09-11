package springboot.librarysystem.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.context.annotation.Import;
import springboot.librarysystem.config.SecurityConfig;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import springboot.librarysystem.service.BookService;
import springboot.librarysystem.service.UserService;
import springboot.librarysystem.dto.BookWithLocationsDto;
import springboot.librarysystem.dto.LocationRequestDto;
import springboot.librarysystem.entity.Book;
import springboot.librarysystem.entity.BookLocation;
import springboot.librarysystem.entity.Library;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
@Import(SecurityConfig.class)
public class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookService bookService;
    @MockBean
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void testAddBook_success() throws Exception {
        Book book = new Book();
        book.setTitle("Test Book");
        Mockito.when(bookService.addBook(Mockito.any(Book.class))).thenReturn(book);
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Test Book"));
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void testUpdateBook_success() throws Exception {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Updated Title");
        Mockito.when(bookService.updateBook(Mockito.eq(1L), Mockito.any(Book.class))).thenReturn(book);
        mockMvc.perform(put("/api/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Updated Title"));
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void testAddBookLocation_success() throws Exception {
        LocationRequestDto req = new LocationRequestDto();
        req.libraryId = 2L;
        req.totalQuantity = 10;
        req.availableQuantity = 8;
        Mockito.doNothing().when(bookService).addBookLocation(Mockito.eq(1L), Mockito.any(LocationRequestDto.class));
        mockMvc.perform(post("/api/books/1/location")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void testUpdateBookLocation_success() throws Exception {
        BookLocation bookLocation = Mockito.mock(BookLocation.class);
        Library library = new Library();
        library.setName("Main Branch");
        Mockito.when(bookLocation.getLibrary()).thenReturn(library);
        Mockito.when(bookLocation.getTotalQuantity()).thenReturn(10);
        Mockito.when(bookLocation.getAvailableQuantity()).thenReturn(8);
        Mockito.when(bookService.updateBookLocation(Mockito.eq(1L), Mockito.any(LocationRequestDto.class))).thenReturn(bookLocation);
        LocationRequestDto req = new LocationRequestDto();
        req.libraryId = 2L;
        req.totalQuantity = 10;
        req.availableQuantity = 8;
        mockMvc.perform(put("/api/books/1/location")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void testSearchBooks_success() throws Exception {
        BookWithLocationsDto dto = new BookWithLocationsDto(1L, "Search Title", "Author", 2023, "Type", List.of());
        Mockito.when(bookService.searchBooks("Search Title", null, null)).thenReturn(List.of(dto));
        mockMvc.perform(get("/api/books/search?title=Search Title"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title").value("Search Title"));
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void testAddBook_notFound() throws Exception {
        Mockito.when(bookService.addBook(Mockito.any(Book.class))).thenThrow(new IllegalArgumentException("Book not found"));
        Book book = new Book();
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Book not found"));
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void testUpdateBook_notFound() throws Exception {
        Mockito.when(bookService.updateBook(Mockito.eq(1L), Mockito.any(Book.class))).thenThrow(new IllegalArgumentException("Book not found"));
        Book book = new Book();
        mockMvc.perform(put("/api/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Book not found"));
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void testAddBookLocation_duplicate() throws Exception {
        Mockito.doThrow(new IllegalArgumentException("Book location for this library already exists")).when(bookService).addBookLocation(Mockito.eq(1L), Mockito.any(LocationRequestDto.class));
        LocationRequestDto req = new LocationRequestDto();
        req.libraryId = 2L;
        req.totalQuantity = 10;
        req.availableQuantity = 8;
        mockMvc.perform(post("/api/books/1/location")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Book location for this library already exists"));
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void testUpdateBookLocation_notFound() throws Exception {
        Mockito.when(bookService.updateBookLocation(Mockito.eq(1L), Mockito.any(LocationRequestDto.class))).thenThrow(new IllegalArgumentException("Book location not found"));
        LocationRequestDto req = new LocationRequestDto();
        req.libraryId = 2L;
        req.totalQuantity = 10;
        req.availableQuantity = 8;
        mockMvc.perform(put("/api/books/1/location")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Book location not found"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testAddBook_noPermission_userRole() throws Exception {
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new Book())))
                .andExpect(status().isForbidden()).andExpect(jsonPath("$.code").value(403))
                .andExpect(jsonPath("$.message").value("Forbidden: You do not have permission"));
    }

    @Test
    void testAddBook_noPermission() throws Exception {
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new Book())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("Unauthorized: Please login"));
    }
}
