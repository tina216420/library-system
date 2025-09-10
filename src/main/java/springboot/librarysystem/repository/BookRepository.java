package springboot.librarysystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springboot.librarysystem.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
	// SQL query method: search books
	@org.springframework.data.jpa.repository.Query(value = "SELECT * FROM books WHERE (:title IS NULL OR title LIKE %:title%) AND (:author IS NULL OR author LIKE %:author%) AND (:year IS NULL OR publication_year = :year)", nativeQuery = true)
	java.util.List<Book> searchBooksBySql(String title, String author, Integer year);
}
