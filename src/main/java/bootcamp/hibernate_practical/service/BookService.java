package bootcamp.hibernate_practical.service;

import bootcamp.hibernate_practical.dto.BookResponse;
import bootcamp.hibernate_practical.dto.CreateBookRequest;
import bootcamp.hibernate_practical.dto.UpdateBookRequest;
import bootcamp.hibernate_practical.entity.Book;
import bootcamp.hibernate_practical.repository.BookRepository;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;

@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public BookResponse createBook(CreateBookRequest request) {
        Book book = new Book(
                request.getTitle(),
                request.getAuthor(),
                request.getGenre(),
                request.getPublicationYear(),
                true
        );
        Book savedBook = bookRepository.save(book);
        return mapToResponse(savedBook);
    }


    // Fetch all books from the repository
    // Convert each Book entity into BookResponse DTO
    // Return the list
    public List<BookResponse> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        List<BookResponse> responses = new ArrayList<>();

        for (Book book : books) {
            BookResponse response = mapToResponse(book);
            responses.add(response);
        }
        return responses;
    }

    public BookResponse getBookById(Long id) {
        // TODO
        // Find the book by its ID
        // Throw RuntimeException if not found
        // Convert the entity to BookResponse
        return null;
    }

    public BookResponse updateBook(Long id, UpdateBookRequest request) {
        // TODO
        // Find existing book
        // Update its fields
        // Save the updated entity
        // Convert to BookResponse
        return null;
    }

    public void deleteBook(Long id) {
        // TODO
    }

    public List<BookResponse> findByAuthor(String author) {
        // TODO
        return null;
    }

    public List<BookResponse> findAvailableBooks(){
        // TODO
        return null;
    }

    private BookResponse mapToResponse(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getGenre(),
                book.getPublicationYear(),
                book.isAvailable()
        );
    }
}
