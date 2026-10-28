package bootcamp.hibernate_practical.controller;

import bootcamp.hibernate_practical.dto.BookResponse;
import bootcamp.hibernate_practical.dto.CreateBookRequest;
import bootcamp.hibernate_practical.dto.UpdateBookRequest;
import bootcamp.hibernate_practical.service.BookService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequiredArgsConstructor
public class BookPageController {

    private static final String REDIRECT_TO_BOOKS = "redirect:/books";
    private static final String BOOK_FORM_ATTRIBUTE = "bookForm";

    private final BookService bookService;

    @PostMapping("/books/{id}")
    public String updateBook(
            @PathVariable Long id,
            @Valid @ModelAttribute(BOOK_FORM_ATTRIBUTE) UpdateBookRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("bookId", id);
            return "book-edit";
        }

        bookService.updateBook(id, request);
        return REDIRECT_TO_BOOKS;
    }

    @GetMapping("/books/{id}/edit")
    public String editBook(@PathVariable Long id, Model model) {
        BookResponse book = bookService.getBookById(id);

        UpdateBookRequest form = new UpdateBookRequest();
        form.setTitle(book.getTitle());
        form.setAuthor(book.getAuthor());
        form.setGenre(book.getGenre());
        form.setPublicationYear(book.getPublicationYear());
        form.setAvailable(book.isAvailable());

        model.addAttribute(BOOK_FORM_ATTRIBUTE, form);
        model.addAttribute("bookId", id);
        return "book-edit";
    }

    @GetMapping("/books/new")
    public String newBook(Model model) {
        model.addAttribute(BOOK_FORM_ATTRIBUTE, new CreateBookRequest());
        return "book-create";

    }

    @PostMapping("/books")
    public String createBook(
            @Valid @ModelAttribute(BOOK_FORM_ATTRIBUTE) CreateBookRequest request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "book-create";
        }

        bookService.createBook(request);
        return REDIRECT_TO_BOOKS;
    }

    @GetMapping("/")
    public String home() {
        return REDIRECT_TO_BOOKS;
    }

    @GetMapping("/books")
    public String books(Model model, HttpServletRequest request) {
        model.addAttribute("books", bookService.getAllBooks());
        model.addAttribute("isAdmin", request.isUserInRole("ADMIN"));
        return "books";
    }

    @PostMapping("/books/{id}/delete")
    public String deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return REDIRECT_TO_BOOKS;
    }

}
