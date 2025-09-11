package springboot.librarysystem.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;
import springboot.librarysystem.entity.User;
import springboot.librarysystem.repository.UserRepository;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTest {
    @Autowired
    private UserService userService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private RestTemplate restTemplate;

    @Test
    void testCreateUser_success() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setRole("USER");
        Mockito.when(userRepository.findByUsername("testuser")).thenReturn(null);
        Mockito.when(passwordEncoder.encode("password")).thenReturn("encoded");
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
        boolean result = userService.createUser(user);
        assertTrue(result);
    }

    @Test
    void testCreateUser_duplicate() {
        User user = new User();
        user.setUsername("testuser");
        Mockito.when(userRepository.findByUsername("testuser")).thenReturn(user);
        boolean result = userService.createUser(user);
        assertFalse(result);
    }

    @Test
    void testLogin_success() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("encoded");
        Mockito.when(userRepository.findByUsername("testuser")).thenReturn(user);
        Mockito.when(passwordEncoder.matches("password", "encoded")).thenReturn(true);
        boolean result = userService.login("testuser", "password");
        assertTrue(result);
    }

    @Test
    void testLogin_fail() {
        Mockito.when(userRepository.findByUsername("nouser")).thenReturn(null);
        boolean result = userService.login("nouser", "password");
        assertFalse(result);
    }

    @Test
    void testGetUserById_found() {
        User user = new User();
        user.setId(1L);
        Mockito.when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));
        User result = userService.getUserById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetUserById_notFound() {
        Mockito.when(userRepository.findById(2L)).thenReturn(java.util.Optional.empty());
        User result = userService.getUserById(2L);
        assertNull(result);
    }

    @Test
    void testDeleteUser_success() {
        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        boolean result = userService.deleteUser(1L);
        assertTrue(result);
    }

    @Test
    void testDeleteUser_notFound() {
        Mockito.when(userRepository.existsById(2L)).thenReturn(false);
        boolean result = userService.deleteUser(2L);
        assertFalse(result);
    }

    @Test
    void testCreateLibrarian_success() {
        User user = new User();
        user.setUsername("librarian");
        user.setPassword("password");
        user.setRole("Librarian");
        Mockito.when(userRepository.findByUsername("librarian")).thenReturn(null);
        Mockito.when(passwordEncoder.encode("password")).thenReturn("encoded");
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
        UserService spyService = Mockito.spy(userService);
        Mockito.doReturn(true).when(spyService).verifyLibrarian();
        boolean result = spyService.createUser(user);
        assertTrue(result);
    }

    @Test
    void testCreateLibrarian_verifyFail() {
        User user = new User();
        user.setUsername("librarian");
        user.setPassword("password");
        user.setRole("Librarian");
        Mockito.when(userRepository.findByUsername("librarian")).thenReturn(null);
        UserService spyService = Mockito.spy(userService);
        Mockito.doReturn(false).when(spyService).verifyLibrarian();
        boolean result = spyService.createUser(user);
        assertFalse(result);
    }

    @Test
    void testUpdateUser_success() {
        User oldUser = new User();
        oldUser.setId(1L);
        oldUser.setUsername("old");
        oldUser.setPassword("oldpass");
        oldUser.setRole("USER");
        oldUser.setEmail("old@mail.com");
        User newUser = new User();
        newUser.setPassword("newpass");
        Mockito.when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(oldUser));
        Mockito.when(passwordEncoder.encode("newpass")).thenReturn("encoded");
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(oldUser);
        User result = userService.updateUserPassword(1L, newUser);
        assertNotNull(result);
        assertEquals("old", result.getUsername());
        assertEquals("encoded", result.getPassword());
        assertEquals("USER", result.getRole());
        assertEquals("old@mail.com", result.getEmail());
    }

    @Test
    void testUpdateUser_notFound() {
        User newUser = new User();
        Mockito.when(userRepository.findById(2L)).thenReturn(java.util.Optional.empty());
        User result = userService.updateUserPassword(2L, newUser);
        assertNull(result);
    }

    @Test
    void testUpdateUserPassword_emptyPassword() {
        User oldUser = new User();
        oldUser.setId(1L);
        oldUser.setUsername("old");
        oldUser.setPassword("oldpass");
        oldUser.setRole("USER");
        oldUser.setEmail("old@mail.com");
        User newUser = new User();
        newUser.setPassword(""); // password is empty
        Mockito.when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(oldUser));
        User result = userService.updateUserPassword(1L, newUser);
        assertNull(result);
    }

    @Test
    void testUpdateUserPassword_passwordIsNull() {
        User oldUser = new User();
        oldUser.setId(1L);
        oldUser.setUsername("old");
        oldUser.setPassword("oldpass");
        oldUser.setRole("USER");
        oldUser.setEmail("old@mail.com");
        User newUser = new User();
        newUser.setPassword(null); // password is null
        Mockito.when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(oldUser));
        User result = userService.updateUserPassword(1L, newUser);
        assertNull(result);
    }

    @Test
    void testLoadUserByUsername_success() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("encoded");
        user.setRole("LIBRARIAN");
        Mockito.when(userRepository.findByUsername("testuser")).thenReturn(user);
        org.springframework.security.core.userdetails.UserDetails details = userService.loadUserByUsername("testuser");
        assertEquals("testuser", details.getUsername());
        assertEquals("encoded", details.getPassword());
        assertTrue(details.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_LIBRARIAN")));
    }

    @Test
    void testLoadUserByUsername_userSuccess() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("encoded");
        user.setRole("USER");
        Mockito.when(userRepository.findByUsername("testuser")).thenReturn(user);
        org.springframework.security.core.userdetails.UserDetails details = userService.loadUserByUsername("testuser");
        assertEquals("testuser", details.getUsername());
        assertEquals("encoded", details.getPassword());
        assertTrue(details.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void testLoadUserByUsername_notFound() {
        Mockito.when(userRepository.findByUsername("nouser")).thenReturn(null);
        assertThrows(org.springframework.security.core.userdetails.UsernameNotFoundException.class,
            () -> userService.loadUserByUsername("nouser"));
    }

    @Test
    void testVerifyLibrarian_mockResponse() {
        // Arrange
        org.mockito.Mockito.when(restTemplate.exchange(
            org.mockito.ArgumentMatchers.eq("https://todo.com.tw"),
            org.mockito.ArgumentMatchers.eq(HttpMethod.GET),
            org.mockito.ArgumentMatchers.any(HttpEntity.class),
            org.mockito.ArgumentMatchers.eq(String.class)
        )).thenReturn(new ResponseEntity<>("MOCK", HttpStatus.OK));

        // Act
        boolean result = userService.verifyLibrarian();

        // Assert
        assertTrue(result);
    }
}
