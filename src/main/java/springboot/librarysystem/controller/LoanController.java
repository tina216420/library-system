package springboot.librarysystem.controller;

import springboot.librarysystem.dto.ApiResponseDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springboot.librarysystem.service.LoanService;

@RestController
@RequestMapping("/api/loans")
public class LoanController {
	@Autowired
	private LoanService loanService;

	// Borrow book
	@PostMapping("/borrow")
	public ResponseEntity<ApiResponseDto<Void>> borrowBook(@RequestParam Long userId, @RequestParam Long bookId,
			@RequestParam Long libraryId) {
		loanService.borrowBook(userId, bookId, libraryId);
		return ResponseEntity.ok(new ApiResponseDto<>(200, "Book borrowed successfully", null));
	}

	// Return book
	@PostMapping("/return")
	public ResponseEntity<ApiResponseDto<Void>> returnBook(@RequestParam Long loanId) {
		loanService.returnBook(loanId);
		return ResponseEntity.ok(new ApiResponseDto<>(200, "Book returned successfully", null));
	}

	// Notify users whose loans are due within 5 days (manual trigger)
	@GetMapping("/notify-due-soon")
	public ResponseEntity<ApiResponseDto<Void>> notifyDueSoon() {
		int count = loanService.notifyDueSoon();
		String msg = "Notification sent (simulated): " + count + " emails";
		return ResponseEntity.ok(new ApiResponseDto<>(200, msg, null));
	}
}
