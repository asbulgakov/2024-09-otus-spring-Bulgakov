package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.mappers.BookMapper;
import ru.otus.hw.models.dto.BookDto;
import ru.otus.hw.repositories.JpaAuthorRepository;
import ru.otus.hw.repositories.JpaBookRepository;
import ru.otus.hw.repositories.JpaCommentRepository;
import ru.otus.hw.repositories.JpaGenreRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Сервис для работы с книгами")
@DataJpaTest
@Import({
        BookServiceImpl.class,
        JpaAuthorRepository.class,
        JpaGenreRepository.class,
        JpaBookRepository.class,
        JpaCommentRepository.class,
        BookMapper.class,
})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class BookServiceIntegrationTest {

    private final long AUTHOR_ID = 1L;

    private final long GENRE_FIRST_ID = 1L;

    private final long GENRE_SECOND_ID = 2L;

    @Autowired
    private BookService bookService;

    @Test
    @DisplayName("должен сохранять новую книгу")
    void shouldInsertAndFindBook() {
        BookDto book = bookService.insert("Test Book", AUTHOR_ID, Set.of(GENRE_FIRST_ID, GENRE_SECOND_ID));
        Optional<BookDto> foundBook = bookService.findById(book.getId());

        assertThat(foundBook).isPresent();
        assertThat(foundBook.get().getTitle()).isEqualTo("Test Book");
        assertThat(foundBook.get().getAuthor()).isNotNull();
        assertThat(foundBook.get().getGenres()).hasSize(2);
    }

    @Test
    @DisplayName("должен сохранять измененную книгу")
    void shouldUpdateBook() {
        BookDto book = bookService.insert("Test Book", AUTHOR_ID, Set.of(GENRE_FIRST_ID, GENRE_SECOND_ID));
        BookDto updatedBook = bookService.update(book.getId(), "Updated Book", AUTHOR_ID, Set.of(GENRE_FIRST_ID));

        Optional<BookDto> foundBook = bookService.findById(updatedBook.getId());
        assertThat(foundBook).isPresent();
        assertThat(foundBook.get().getTitle()).isEqualTo("Updated Book");
        assertThat(foundBook.get().getGenres()).hasSize(1);
    }

    @Test
    @DisplayName("должен находить книгу по id")
    void shouldFindBookById() {
        BookDto book = bookService.insert("Test Book", AUTHOR_ID, Set.of(GENRE_FIRST_ID, GENRE_SECOND_ID));
        Optional<BookDto> foundBook = bookService.findById(book.getId());

        assertThat(foundBook).isPresent();
        assertThat(foundBook.get().getId()).isEqualTo(book.getId());
    }

    @Test
    @DisplayName("должен находить все книги")
    void shouldFindAllBooks() {
        List<BookDto> books = bookService.findAll();

        assertThat(books).hasSize(5);
    }

    @Test
    @DisplayName("должен удалять книгу по id")
    void shouldDeleteBook() {
        BookDto book = bookService.insert("Test Book", AUTHOR_ID, Set.of(GENRE_FIRST_ID, GENRE_SECOND_ID));
        bookService.deleteById(book.getId());

        Optional<BookDto> foundBook = bookService.findById(book.getId());
        assertThat(foundBook).isNotPresent();
    }

    @Test
    @DisplayName("Проверяет на отсутствие LazyInitializationException")
    void shouldNotThrowLazyInitializationException() {
        BookDto book = bookService.insert("Test Book", AUTHOR_ID, Set.of(GENRE_FIRST_ID, GENRE_SECOND_ID));
        Optional<BookDto> foundBook = bookService.findById(book.getId());

        assertThat(foundBook).isPresent();
        assertThat(foundBook.get().getAuthor().getFullName()).isNotNull();
        assertThat(foundBook.get().getGenres()).hasSize(2);
    }
}