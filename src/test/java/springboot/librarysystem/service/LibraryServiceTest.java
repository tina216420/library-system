package springboot.librarysystem.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import springboot.librarysystem.entity.Library;
import springboot.librarysystem.repository.LibraryRepository;
import springboot.librarysystem.repository.BookRepository;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class LibraryServiceTest {
    @Autowired
    private LibraryService libraryService;
    @MockBean
    private LibraryRepository libraryRepository;
    @MockBean
    private BookRepository bookRepository;

    @Test
    void testAddLibrary_success() {
        Library library = new Library();
        library.setName("Main Branch");
        Mockito.when(libraryRepository.save(Mockito.any(Library.class))).thenReturn(library);
        Library result = libraryService.addLibrary(library);
        assertEquals("Main Branch", result.getName());
    }

    @Test
    void testGetLibrary_notFound() {
        Mockito.when(libraryRepository.findById(1L)).thenReturn(Optional.empty());
        Library result = libraryService.getLibraryById(1L);
        assertNull(result);
    }
}
