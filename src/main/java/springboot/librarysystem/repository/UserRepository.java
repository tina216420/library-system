package springboot.librarysystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springboot.librarysystem.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByUsername(String username);
}
