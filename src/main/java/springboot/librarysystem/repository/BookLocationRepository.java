package springboot.librarysystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import springboot.librarysystem.entity.BookLocation;

public interface BookLocationRepository extends JpaRepository<BookLocation, Long> {
	List<BookLocation> findByBookId(Long bookId);

	@org.springframework.data.jpa.repository.Query(value = "SELECT * FROM book_locations WHERE book_id = :bookId AND library_id = :libraryId LIMIT 1", nativeQuery = true)
	BookLocation findByBookIdAndLibraryId(Long bookId, Long libraryId);
}
