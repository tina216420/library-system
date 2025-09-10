package springboot.librarysystem.dto;

import java.util.List;

/**
 * DTO for book with location details
 */
public class BookWithLocationsDto {
	public Long id;
	public String title;
	public String author;
	public Integer publicationYear;
	public String type;
	public List<BookLocationInfoDto> locations;

	public BookWithLocationsDto(Long id, String title, String author, Integer publicationYear, String type,
			List<BookLocationInfoDto> locations) {
		this.id = id;
		this.title = title;
		this.author = author;
		this.publicationYear = publicationYear;
		this.type = type;
		this.locations = locations;
	}

	public BookWithLocationsDto(springboot.librarysystem.entity.Book book, List<BookLocationInfoDto> locations) {
		this(book.getId(), book.getTitle(), book.getAuthor(), book.getPublicationYear(), book.getType(), locations);
	}

	/**
	 * DTO for book location info
	 */
	public static class BookLocationInfoDto {
		public String libraryName;
		public int totalQuantity;
		public int availableQuantity;

		public BookLocationInfoDto(String libraryName, int totalQuantity, int availableQuantity) {
			this.libraryName = libraryName;
			this.totalQuantity = totalQuantity;
			this.availableQuantity = availableQuantity;
		}
	}
}
