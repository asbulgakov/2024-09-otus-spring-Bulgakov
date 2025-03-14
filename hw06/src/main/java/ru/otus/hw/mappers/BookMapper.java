package ru.otus.hw.mappers;

import org.springframework.stereotype.Component;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.models.dto.AuthorDto;
import ru.otus.hw.models.dto.BookDto;
import ru.otus.hw.models.dto.GenreDto;

import java.util.List;

@Component
public class BookMapper {

    public BookDto toDto(Book book) {
        return new BookDto(
                book.getId(),
                book.getTitle(),
                new AuthorDto(book.getAuthor().getId(), book.getAuthor().getFullName()),
                genresToGenreDtos(book.getGenres())
        );
    }

    private List<GenreDto> genresToGenreDtos(List<Genre> genres) {
        return genres.stream()
                .map(genre -> new GenreDto(genre.getId(), genre.getName()))
                .toList();
    }
}
