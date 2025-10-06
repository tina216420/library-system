
package springboot.librarysystem.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import springboot.librarysystem.repository.LoanRepository;
import springboot.librarysystem.repository.UserRepository;
import springboot.librarysystem.repository.BookRepository;
import springboot.librarysystem.repository.BookLocationRepository;
import springboot.librarysystem.repository.LibraryRepository;
import springboot.librarysystem.entity.Loan;
import springboot.librarysystem.entity.User;
import springboot.librarysystem.entity.Book;
import springboot.librarysystem.entity.Library;
import springboot.librarysystem.entity.BookLocation;

@Service
public class LoanService {
	public static final String STATUS_BORROWED = "Borrowed";
	public static final String STATUS_RETURNED = "Returned";
	public static final String STATUS_OVERDUE = "Overdue";
	public static final String TYPE_BOOK = "圖書";
	
	@Value("${library.loan.duration.months:1}")
	private int loanDurationMonths;
	@Value("${library.notification.days.before:5}")
	private int notificationDaysBefore;
	@Value("${library.borrow.limit.book:5}")
	private int bookBorrowLimit;
	@Value("${library.borrow.limit.other:10}")
	private int otherBorrowLimit;
	@Autowired
	private LibraryRepository libraryRepository;
	@Autowired
	private BookLocationRepository bookLocationRepository;
	@Autowired
	private LoanRepository loanRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BookRepository bookRepository;

	/**
	 * Borrow a book for a user from a specific library branch.
	 */
	@Transactional
	public void borrowBook(Long userId, Long bookId, Long libraryId) {
		User user = userRepository.findById(userId).orElse(null);
		Book book = bookRepository.findById(bookId).orElse(null);
		Library library = libraryRepository.findById(libraryId).orElse(null);
		if (user == null || book == null || library == null)
			throw new IllegalArgumentException("User, book, or library branch does not exist");

		// Query BookLocation directly using SQL
		BookLocation targetLocation = bookLocationRepository.findByBookIdAndLibraryId(bookId, libraryId);
		if (targetLocation == null || targetLocation.getAvailableQuantity() <= 0) {
			throw new IllegalStateException("Insufficient available quantity in this branch");
		}

		// Forbidden: have not returned the books on time to borrow them again
		long overdueCount = loanRepository.countOverdueLoansByUserId(userId, java.time.LocalDate.now());
		if (overdueCount > 0) {
			throw new IllegalStateException("You have overdue books. Please return them before borrowing new ones.");
		}

		// Query borrowed count by book type using SQL
		long userTypeCount = loanRepository.countBorrowedByUserIdAndBookType(userId, book.getType());
		long bookTypeLimit = TYPE_BOOK.equals(book.getType()) ? bookBorrowLimit : otherBorrowLimit;
		if (userTypeCount >= bookTypeLimit) {
			throw new IllegalStateException("Borrowing limit reached: " + book.getType() + " max " + bookTypeLimit);
		}

		// Borrow period: configurable months
		java.time.LocalDate now = java.time.LocalDate.now();
		java.time.LocalDate due = now.plusMonths(loanDurationMonths);
		Loan loan = new Loan();
		loan.setUser(user);
		loan.setBook(book);
		loan.setBorrowDate(now);
		loan.setDueDate(due);
		loan.setStatus(STATUS_BORROWED);
		loan.setLibrary(library);
		loanRepository.save(loan);

		// On successful borrow, decrease available quantity in branch by 1
		targetLocation.setAvailableQuantity(targetLocation.getAvailableQuantity() - 1);
		bookLocationRepository.save(targetLocation);
	}

	/**
	 * Return a borrowed book and update status/quantity.
	 */
	@Transactional
	public void returnBook(Long loanId) {
		Loan loan = loanRepository.findById(loanId).orElse(null);
		if (loan == null || !STATUS_BORROWED.equals(loan.getStatus()))
			throw new IllegalArgumentException("Loan record does not exist or already returned");

		java.time.LocalDate now = java.time.LocalDate.now();
		loan.setReturnDate(now);
		if (loan.getDueDate().isBefore(now)) {
			loan.setStatus(STATUS_OVERDUE);
		} else {
			loan.setStatus(STATUS_RETURNED);
		}
		loanRepository.save(loan);

		// On successful return, increase available quantity in branch by 1
		BookLocation targetLocation = bookLocationRepository.findByBookIdAndLibraryId(loan.getBook().getId(),
				loan.getLibrary().getId());
		if (targetLocation != null) {
			targetLocation.setAvailableQuantity(targetLocation.getAvailableQuantity() + 1);
			bookLocationRepository.save(targetLocation);
		}
	}

	/**
	 * Notify users whose loans are due within configurable days (simulated with
	 * System.out.println).
	 */
	public int notifyDueSoon() {
		java.time.LocalDate now = java.time.LocalDate.now();
		java.time.LocalDate dueSoonDate = now.plusDays(notificationDaysBefore);
		java.sql.Date sqlDueSoonDate = java.sql.Date.valueOf(dueSoonDate);
		java.util.List<Loan> dueSoonLoans = loanRepository.findDueSoonLoans(sqlDueSoonDate);
		dueSoonLoans.forEach(l -> {
			System.out.println("Notification: User " + l.getUser().getUsername() + " - Your borrowed book '"
					+ l.getBook().getTitle() + "' is due in " + notificationDaysBefore + " days!");
		});
		return dueSoonLoans.size();
	}
}
