package ru.otus.hw.mappers;

import org.springframework.stereotype.Component;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Genre;
import ru.otus.hw.models.dto.AuthorDto;
import ru.otus.hw.models.dto.GenreDto;

@Component
public class GenreMapper {

    public GenreDto toDto(Genre genre) {
        return new GenreDto(genre.getId(), genre.getName());
    }
}
