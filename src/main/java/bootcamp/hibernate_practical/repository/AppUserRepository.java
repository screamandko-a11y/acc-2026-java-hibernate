package bootcamp.hibernate_practical.repository;

import bootcamp.hibernate_practical.entity.AppUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long > {
    Optional<AppUser> findByUsername(String username);
}
