package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.dto.BookDto;

@Component
public class BookDtoConverter {

    public BookDto bookToBookDto(Book book) {
        return new BookDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getGenres()
        );
    }

    public Book bookDtoToBook(BookDto bookDto) {
        return new Book(
                bookDto.getId(),
                bookDto.getTitle(),
                bookDto.getAuthor(),
                bookDto.getGenres()
        );
    }
}
