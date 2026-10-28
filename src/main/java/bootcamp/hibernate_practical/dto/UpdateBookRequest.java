package bootcamp.hibernate_practical.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import bootcamp.hibernate_practical.dto.BookResponse;
import bootcamp.hibernate_practical.dto.UpdateBookRequest;

@Getter
@Setter
public class UpdateBookRequest {

    @NotBlank(message = "Title must not be blank")
    private String title;

    @NotBlank(message = "Author must not be blank")
    private String author;

    @NotBlank(message = "Genre must not be blank")
    private String genre;

    @Min(value = 1, message = "Publication year must be greater than 0")
    private int publicationYear;

    private boolean available;
}
