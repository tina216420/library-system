package springboot.librarysystem.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import springboot.librarysystem.entity.User;
import springboot.librarysystem.service.UserService;
import springboot.librarysystem.config.SecurityConfig;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateUser_success() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setRole("USER");
        Mockito.when(userService.createUser(Mockito.any(User.class))).thenReturn(true);
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Registration successful"));
    }

    @Test
    void testCreateUser_duplicate() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        Mockito.when(userService.createUser(Mockito.any(User.class))).thenReturn(false);
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Registration failed: librarian verification failed or username already exists"));
    }

    @Test
    void testGetUserById_success() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }

    @Test
    void testGetUserById_notFound() throws Exception {
        Mockito.when(userService.getUserById(2L)).thenReturn(null);
        mockMvc.perform(get("/api/users/2"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void testDeleteUser_success() throws Exception {
        Mockito.when(userService.deleteUser(1L)).thenReturn(true);
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully"));
    }

    @Test
    void testDeleteUser_notFound() throws Exception {
        Mockito.when(userService.deleteUser(2L)).thenReturn(false);
        mockMvc.perform(delete("/api/users/2"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User not found, delete failed"));
    }

    @Test
    void testUpdateUser_success() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("updated");
        Mockito.when(userService.updateUserPassword(Mockito.eq(1L), Mockito.any(User.class))).thenReturn(user);
        mockMvc.perform(patch("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("updated"))
                .andExpect(jsonPath("$.message").value("Update successful"));
    }

    @Test
    void testUpdateUser_notFound() throws Exception {
        Mockito.when(userService.updateUserPassword(Mockito.eq(2L), Mockito.any(User.class))).thenReturn(null);
        User user = new User();
        user.setUsername("notfound");
        mockMvc.perform(patch("/api/users/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Update failed"));
    }

    @Test
    void testLogin_success() throws Exception {
        Mockito.when(userService.login(Mockito.eq("testuser"), Mockito.eq("password"))).thenReturn(true);
        // Create JSON manually to include password field despite WRITE_ONLY setting
        String jsonContent = "{\"username\":\"testuser\",\"password\":\"password\",\"email\":\"test@example.com\",\"role\":\"USER\"}";
        
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"));
    }

    @Test
    void testLogin_fail() throws Exception {
        Mockito.when(userService.login("testuser", "wrongpass")).thenReturn(false);
        // Create JSON manually to include password field despite WRITE_ONLY setting
        String jsonContent = "{\"username\":\"testuser\",\"password\":\"wrongpass\",\"email\":\"test@example.com\",\"role\":\"USER\"}";
        
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Login failed: incorrect username or password"));
    }
}
