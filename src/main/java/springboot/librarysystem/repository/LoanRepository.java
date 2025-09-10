package springboot.librarysystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springboot.librarysystem.entity.Loan;

public interface LoanRepository extends JpaRepository<Loan, Long> {
	/**
	 * Count overdue loans for a user (status = 'Borrowed', due_date < today, not returned).
	 */
	@org.springframework.data.jpa.repository.Query(value = "SELECT COUNT(*) FROM loans WHERE user_id = :userId AND status = 'Borrowed' AND due_date < :today AND return_date IS NULL", nativeQuery = true)
	long countOverdueLoansByUserId(Long userId, java.time.LocalDate today);
	/**
	 * Count borrowed books by user and book type (status = 'Borrowed').
	 */
	@org.springframework.data.jpa.repository.Query(value = "SELECT COUNT(*) FROM loans l JOIN books b ON l.book_id = b.id WHERE l.user_id = :userId AND l.status = 'Borrowed' AND b.type = :bookType", nativeQuery = true)
	long countBorrowedByUserIdAndBookType(Long userId, String bookType);
	
	@org.springframework.data.jpa.repository.Query(value = "SELECT * FROM loans WHERE status = 'Borrowed' AND due_date = :dueDate", nativeQuery = true)
	java.util.List<Loan> findDueSoonLoans(java.sql.Date dueDate);
}
