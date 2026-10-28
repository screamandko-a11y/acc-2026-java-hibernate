package bootcamp.hibernate_practical.service;

import bootcamp.hibernate_practical.dto.BookResponse;
import bootcamp.hibernate_practical.dto.CreateBookRequest;
import bootcamp.hibernate_practical.dto.UpdateBookRequest;
import bootcamp.hibernate_practical.entity.Book;
import bootcamp.hibernate_practical.exceptions.BookNotFoundException;
import bootcamp.hibernate_practical.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        Optional<Book> result = bookRepository.findById(id);
        if (result.isEmpty()) {
            throw new BookNotFoundException("Book ID " + id + " not found");
        }
        Book book = result.get();
        return mapToResponse(book);
    }


    public BookResponse updateBook(Long id, UpdateBookRequest request) {
        Optional<Book> result = bookRepository.findById(id);
        if (result.isEmpty()) {
            throw new BookNotFoundException("Book ID " + id + " not found");
        }

        Book book = result.get();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setGenre(request.getGenre());
        book.setPublicationYear(request.getPublicationYear());
        book.setAvailable(request.isAvailable());
        bookRepository.save(book);

        return mapToResponse(book);
    }

    public void deleteBook(Long id) {
        Optional<Book> result = bookRepository.findById(id);
        if (result.isEmpty()) {
            throw new BookNotFoundException("Book ID " + id + " not found");
        }

        Book book = result.get();
        bookRepository.delete(book);

    }

    public List<BookResponse> findByAuthor(String author) {
        List<Book> result = bookRepository.findByAuthorContainingIgnoreCase(author);


        List<BookResponse> responses = new ArrayList<>();
        for (Book book : result) {
            BookResponse response = mapToResponse(book);
            responses.add(response);
        }
        return responses;
    }


    public List<BookResponse> findAvailableBooks() {
        List<Book> result = bookRepository.findByAvailableTrue();
        List<BookResponse> responses = new ArrayList<>();
        for (Book book : result) {
            BookResponse response = mapToResponse(book);
            responses.add(response);
        }
        return responses;
    }


    // bonus goals. Return: number of books.

    public long findTheNumberOfBooks() {
        return bookRepository.count();
    }



    public List<BookResponse> findBooksByTitle(String title) {
        List<Book> result =
                bookRepository.findByTitleContainingIgnoreCase(title);

        List<BookResponse> responses = new ArrayList<>();

        for (Book book : result) {
            BookResponse response = mapToResponse(book);
            responses.add(response);
        }

        return responses;
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


    public List<BookResponse> findBooksAfterYear(int year) {
        List<Book> result = bookRepository.findByPublicationYearGreaterThan(year);
        List<BookResponse> responses = new ArrayList<>();
        for (Book book : result) {
            BookResponse response = mapToResponse(book);
            responses.add(response);
        }
        return responses;
    }
}

