package springboot.librarysystem.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import springboot.librarysystem.entity.Book;
import springboot.librarysystem.entity.BookLocation;
import springboot.librarysystem.entity.Library;
import springboot.librarysystem.repository.*;
import springboot.librarysystem.dto.LocationRequestDto;
import springboot.librarysystem.dto.BookWithLocationsDto;

@Service
public class BookService {
	@Autowired
	private LibraryRepository libraryRepository;
	@Autowired
	private BookRepository bookRepository;
	@Autowired
	private BookLocationRepository bookLocationRepository;

	public Book addBook(Book book) {
		return bookRepository.save(book);
	}

	public Book updateBook(Long bookId, Book updatedBook) {
		Book book = bookRepository.findById(bookId).orElse(null);
		if (book == null) {
			throw new IllegalArgumentException("Book not found");
		}
		book.setTitle(updatedBook.getTitle());
		book.setAuthor(updatedBook.getAuthor());
		book.setPublicationYear(updatedBook.getPublicationYear());
		book.setType(updatedBook.getType());
		return bookRepository.save(book);
	}

	/**
	 * Add a book location for a specific book. If the library does not exist,
	 * create it.
	 * 
	 * @param bookId   Book ID
	 * @param location Location request
	 */
	public void addBookLocation(Long bookId, LocationRequestDto location) {
		Book book = bookRepository.findById(bookId).orElse(null);
		if (book == null) {
			throw new IllegalArgumentException("Book does not exist");
		}
		Library library = libraryRepository.findById(location.libraryId).orElse(null);
		if (library == null) {
			throw new IllegalArgumentException("Library does not exist");
		}
		// Check if book location already exists for this book and library
		BookLocation existing = bookLocationRepository.findByBookIdAndLibraryId(bookId, location.libraryId);
		if (existing != null) {
			throw new IllegalArgumentException("Book location for this library already exists");
		}
		BookLocation bookLocation = new BookLocation();
		bookLocation.setBook(book);
		bookLocation.setLibrary(library);
		bookLocation.setTotalQuantity(location.totalQuantity);
		bookLocation.setAvailableQuantity(location.availableQuantity);
		bookLocationRepository.save(bookLocation);
	}

	/**
	 * Update a book location for a specific book and library.
	 */
	public BookLocation updateBookLocation(Long bookId, LocationRequestDto location) {
		BookLocation bookLocation = bookLocationRepository.findByBookIdAndLibraryId(bookId, location.libraryId);
		if (bookLocation == null) {
			throw new IllegalArgumentException("Book location not found");
		}
		bookLocation.setTotalQuantity(location.totalQuantity);
		bookLocation.setAvailableQuantity(location.availableQuantity);
		return bookLocationRepository.save(bookLocation);
	}

	public List<BookWithLocationsDto> searchBooks(String title, String author, Integer year) {
		List<Book> books = bookRepository.searchBooksBySql(title, author, year);
		return books.stream().map(b -> {
			List<BookWithLocationsDto.BookLocationInfoDto> locations = getBookLocations(b.getId());
			return new BookWithLocationsDto(b, locations);
		}).toList();
	}

	private List<BookWithLocationsDto.BookLocationInfoDto> getBookLocations(Long bookId) {
		List<BookLocation> locations = bookLocationRepository.findByBookId(bookId);
		return locations.stream().map(loc -> new BookWithLocationsDto.BookLocationInfoDto(loc.getLibrary().getName(),
				loc.getTotalQuantity(), loc.getAvailableQuantity())).toList();
	}
}
