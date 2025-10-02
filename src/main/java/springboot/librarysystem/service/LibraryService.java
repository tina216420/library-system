package springboot.librarysystem.service;

import springboot.librarysystem.entity.Library;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LibraryService {
	// Business logic related to library branches
	@org.springframework.beans.factory.annotation.Autowired
	private springboot.librarysystem.repository.LibraryRepository libraryRepository;

	/**
	 * Add a new library branch
	 */
	@Transactional
	public Library addLibrary(Library library) {
		return libraryRepository.save(library);
	}

	/**
	 * Get library branch by id
	 */
	public Library getLibraryById(Long id) {
		return libraryRepository.findById(id).orElse(null);
	}
}
