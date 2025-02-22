package ru.otus.hw.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Genre;

import java.util.List;

@Data
@AllArgsConstructor
public class BookDto {
    private long id;

    private String title;

    private Author author;

    private List<Genre> genres;
}
