package bootcamp.hibernate_practical.controller;

import bootcamp.hibernate_practical.dto.BookResponse;
import bootcamp.hibernate_practical.dto.UpdateBookRequest;
import bootcamp.hibernate_practical.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import bootcamp.hibernate_practical.dto.CreateBookRequest;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;


@Controller
@RequiredArgsConstructor
public class BookPageController {

    private final BookService bookService;

    @PostMapping("/books/{id}")
    public String updateBook(
            @PathVariable Long id,
            @Valid @ModelAttribute("bookForm") UpdateBookRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("bookId", id);
            return "book-edit";
        }

        bookService.updateBook(id, request);
        return "redirect:/books";
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

        model.addAttribute("bookForm", form);
        model.addAttribute("bookId", id);
        return "book-edit";
    }

    @GetMapping("/books/new")
    public String newBook(Model model) {model.addAttribute("bookForm", new CreateBookRequest());
        return "book-create";

    }

    @PostMapping("/books")
    public String createBook(
            @Valid @ModelAttribute("bookForm") CreateBookRequest request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "book-create";
        }

        bookService.createBook(request);
        return "redirect:/books";
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/books";
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
        return "redirect:/books";
    }

}
