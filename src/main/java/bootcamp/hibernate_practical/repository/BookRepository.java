package bootcamp.hibernate_practical.repository;

import bootcamp.hibernate_practical.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByAuthorContainingIgnoreCase(String author);
    List<Book> findByAvailableTrue();

    // bonus goal
    List<Book> findByTitleContainingIgnoreCase(String title);

    // new method for my self-study
    List<Book> findByPublicationYearGreaterThan(int year);

}
