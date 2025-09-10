package springboot.librarysystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import springboot.librarysystem.entity.User;
import springboot.librarysystem.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RestTemplate restTemplate;
    
    public static final String ROLE_LIBRARIAN = "Librarian";
    // Spring Security: Authenticate using database username, password, and role
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String username) throws org.springframework.security.core.userdetails.UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new org.springframework.security.core.userdetails.UsernameNotFoundException("User not found");
        }
        String role = user.getRole();
        String springRole = ROLE_LIBRARIAN.equalsIgnoreCase(role) ? "LIBRARIAN" : "USER";
        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
            .password(user.getPassword())
            .roles(springRole)
            .build();
    }
    
    public boolean createUser(User user) {
        // Check if username already exists
        if (userRepository.findByUsername(user.getUsername()) != null) {
            return false;
        }
        // Librarian requires API verification
        if (ROLE_LIBRARIAN.equalsIgnoreCase(user.getRole())) {
            if (!verifyLibrarian()) {
                return false;
            }
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }

    public boolean login(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return false;
        }
        return passwordEncoder.matches(password, user.getPassword());
    }

    public User getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.orElse(null);
    }

    public User updateUserPassword(Long id, User user) {
        Optional<User> existing = userRepository.findById(id);
        if (existing.isPresent() && user.getPassword() != null && !user.getPassword().isEmpty()) {
            User u = existing.get();
            u.setPassword(passwordEncoder.encode(user.getPassword()));
            return userRepository.save(u);
        } else {
            return null;
        }
    }

    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Simulate API verification for librarian
     */
    public boolean verifyLibrarian() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "todo");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
            "https://todo.com.tw", HttpMethod.GET, entity, String.class);
        return response.getStatusCode().is2xxSuccessful();
    }
}
