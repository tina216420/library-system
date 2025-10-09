package springboot.librarysystem.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import springboot.librarysystem.entity.Library;
import springboot.librarysystem.service.LibraryService;
import springboot.librarysystem.service.UserService;
import springboot.librarysystem.config.SecurityConfig;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LibraryController.class)
@Import(SecurityConfig.class)
public class LibraryControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private LibraryService libraryService;
    @MockitoBean
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void testAddLibrary_success() throws Exception {
        Library library = new Library();
        library.setName("Main Branch");
        Mockito.when(libraryService.addLibrary(Mockito.any(Library.class))).thenReturn(library);
        mockMvc.perform(post("/api/libraries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(library)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Main Branch"));
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void testUpdateLibrary_success() throws Exception {
        Library existing = new Library();
        existing.setId(1L);
        existing.setName("Old Name");
        Library updated = new Library();
        updated.setId(1L);
        updated.setName("New Name");
        Mockito.when(libraryService.getLibraryById(1L)).thenReturn(existing);
        Mockito.when(libraryService.addLibrary(Mockito.any(Library.class))).thenReturn(updated);
        mockMvc.perform(put("/api/libraries/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("New Name"));
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void testUpdateLibrary_notFound() throws Exception {
        Mockito.when(libraryService.getLibraryById(1L)).thenReturn(null);
        Library library = new Library();
        library.setName("Any Name");
        mockMvc.perform(put("/api/libraries/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(library)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Library not found"));
    }
}
