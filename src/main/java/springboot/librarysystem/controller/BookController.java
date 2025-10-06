package springboot.librarysystem.controller;

import springboot.librarysystem.dto.ApiResponseDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import springboot.librarysystem.entity.Book;
import springboot.librarysystem.service.BookService;
import springboot.librarysystem.dto.BookWithLocationsDto;
import springboot.librarysystem.dto.LocationRequestDto;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

	@Autowired
	private BookService bookService;

	// Add new book (only accessible by librarians, permission can be extended)
	@PostMapping
	public ResponseEntity<ApiResponseDto<Book>> addBook(@RequestBody Book book) {
		Book saved = bookService.addBook(book);
		return ResponseEntity.ok(new ApiResponseDto<>(200, "Book added successfully", saved));
	}

	// Update book information (only accessible by librarians, permission can be
	// extended)
	@PutMapping("/{bookId}")
	public ResponseEntity<ApiResponseDto<Book>> updateBook(@PathVariable Long bookId, @RequestBody Book book) {
		Book updated = bookService.updateBook(bookId, book);
		return ResponseEntity.ok(new ApiResponseDto<>(200, "Book updated successfully", updated));
	}

	// Create book locations (only accessible by librarians, permission can be
	// extended)
	@PostMapping("/{bookId}/location")
	public ResponseEntity<ApiResponseDto<BookWithLocationsDto.BookLocationInfoDto>> addBookLocation(@PathVariable Long bookId,
			@RequestBody LocationRequestDto location) {
		var saved = bookService.addBookLocation(bookId, location);
		var info = new BookWithLocationsDto.BookLocationInfoDto(saved.getLibrary().getName(),
				saved.getTotalQuantity(), saved.getAvailableQuantity());
		return ResponseEntity.ok(new ApiResponseDto<>(200, "Book location created successfully", info));
	}

	// Update book location (only accessible by librarians, permission can be
	// extended)
	@PutMapping("/{bookId}/location")
	public ResponseEntity<ApiResponseDto<BookWithLocationsDto.BookLocationInfoDto>> updateBookLocation(
			@PathVariable Long bookId, @RequestBody LocationRequestDto location) {
		var updated = bookService.updateBookLocation(bookId, location);
		var info = new BookWithLocationsDto.BookLocationInfoDto(updated.getLibrary().getName(),
				updated.getTotalQuantity(), updated.getAvailableQuantity());
		return ResponseEntity.ok(new ApiResponseDto<>(200, "Book location updated successfully", info));
	}

	// Search books
	@GetMapping("/search")
	public ResponseEntity<ApiResponseDto<List<BookWithLocationsDto>>> searchBooks(
			@RequestParam(required = false) String title, @RequestParam(required = false) String author,
			@RequestParam(required = false) Integer year) {
		List<BookWithLocationsDto> result = bookService.searchBooks(title, author, year);
		return ResponseEntity.ok(new ApiResponseDto<>(200, "Search successful", result));
	}
}
