package springboot.librarysystem.controller;

import springboot.librarysystem.dto.ApiResponseDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springboot.librarysystem.entity.User;
import springboot.librarysystem.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	private UserService userService;

	/**
	 * Register a new user (member or librarian)
	 */
	@PostMapping
	public ResponseEntity<ApiResponseDto<Void>> createUser(@RequestBody User user) {
		boolean success = userService.createUser(user);
		if (!success) {
			throw new IllegalArgumentException("Registration failed: librarian verification failed or username already exists");
		}
		return ResponseEntity.ok(new ApiResponseDto<>(200, "Registration successful", null));
	}

	@PostMapping("/login")
	public ResponseEntity<ApiResponseDto<Void>> login(@RequestBody User user) {
		boolean success = userService.login(user.getUsername(), user.getPassword());
		if (!success) {
			throw new IllegalArgumentException("Login failed: incorrect username or password");
		}
		return ResponseEntity.ok(new ApiResponseDto<>(200, "Login successful", null));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponseDto<User>> getUserById(@PathVariable Long id) {
		User user = userService.getUserById(id);
		if (user == null) {
			throw new IllegalArgumentException("User not found");
		}
		return ResponseEntity.ok(new ApiResponseDto<>(200, "Query successful", user));
	}

	// Update user password
	@PatchMapping("/{id}")
	public ResponseEntity<ApiResponseDto<User>> updateUser(@PathVariable Long id, @RequestBody User user) {
		User updated = userService.updateUserPassword(id, user);
		if (updated == null) {
			throw new IllegalArgumentException("Update failed");
		}
		return ResponseEntity.ok(new ApiResponseDto<>(200, "Update successful", updated));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponseDto<Void>> deleteUser(@PathVariable Long id) {
		boolean deleted = userService.deleteUser(id);
		if (!deleted) {
			throw new IllegalArgumentException("User not found, delete failed");
		}
		return ResponseEntity.ok(new ApiResponseDto<>(200, "User deleted successfully", null));
	}
}
