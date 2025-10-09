package springboot.librarysystem.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "book_locations", uniqueConstraints = @UniqueConstraint(columnNames = { "book_id", "library_id" }))
public class BookLocation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "book_id")
	private Book book;

	@ManyToOne
	@JoinColumn(name = "library_id")
	private Library library;

	@Column(nullable = false)
	private Integer totalQuantity;

	@Column(nullable = false)
	private Integer availableQuantity;

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public Library getLibrary() {
		return library;
	}

	public void setLibrary(Library library) {
		this.library = library;
	}

	public Integer getTotalQuantity() {
		return totalQuantity;
	}

	public void setTotalQuantity(Integer totalQuantity) {
		this.totalQuantity = totalQuantity;
	}

	public Integer getAvailableQuantity() {
		return availableQuantity;
	}

	public void setAvailableQuantity(Integer availableQuantity) {
		this.availableQuantity = availableQuantity;
	}
}
