package springboot.librarysystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springboot.librarysystem.entity.Library;
import springboot.librarysystem.service.LibraryService;
import springboot.librarysystem.dto.ApiResponseDto;

@RestController
@RequestMapping("/api/libraries")
public class LibraryController {
	@Autowired
	private LibraryService libraryService;

	/**
	 * Add a new library branch
	 */
	@PostMapping
	public ResponseEntity<ApiResponseDto<Library>> addLibrary(@RequestBody Library library) {
		Library saved = libraryService.addLibrary(library);
		return ResponseEntity.ok(new ApiResponseDto<>(200, "Library created successfully", saved));
	}

	/**
	 * Update library branch info
	 */
	@PutMapping("/{id}")
	public ResponseEntity<ApiResponseDto<Library>> updateLibrary(@PathVariable Long id, @RequestBody Library library) {
		Library existing = libraryService.getLibraryById(id);
		if (existing == null) {
			throw new IllegalArgumentException("Library not found");
		}
		existing.setName(library.getName());
		Library updated = libraryService.addLibrary(existing);
		return ResponseEntity.ok(new ApiResponseDto<>(200, "Library updated successfully", updated));
	}
}
