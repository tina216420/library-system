package springboot.librarysystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springboot.librarysystem.entity.Library;

public interface LibraryRepository extends JpaRepository<Library, Long> {

}
