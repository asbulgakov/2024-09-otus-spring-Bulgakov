package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {

    private final GenreRepository genreRepository;

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    @Override
    public Optional<Book> findById(long id) {
        String sql = """
            SELECT b.id, b.title, a.id AS author_id, a.full_name AS author_name, bg.genre_id
            FROM books b
            JOIN authors a ON b.author_id = a.id
            LEFT JOIN books_genres bg ON b.id = bg.book_id
            WHERE b.id = :id
        """;
        Map<String, Object> params = Collections.singletonMap("id", id);
        Book book = namedParameterJdbcOperations.query(
                sql, params, new BookResultSetExtractor(genreRepository)
        );
        return Optional.ofNullable(book);
    }

    @Override
    public List<Book> findAll() {
        var genres = genreRepository.findAll();
        var relations = getAllGenreRelations();
        var books = getAllBooksWithoutGenres();
        mergeBooksInfo(books, genres, relations);
        return books;
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        String sql = "DELETE FROM books WHERE id = :id";
        Map<String, Object> params = Collections.singletonMap("id", id);
        namedParameterJdbcOperations.update(sql, params);
    }

    private List<Book> getAllBooksWithoutGenres() {
        String sql = """
            SELECT b.id, b.title, a.id AS author_id, a.full_name AS author_name
            FROM books b
            JOIN authors a ON b.author_id = a.id
        """;
        return namedParameterJdbcOperations.query(sql, new BookRowMapper());
    }

    private List<BookGenreRelation> getAllGenreRelations() {
        String sql = "SELECT book_id, genre_id FROM books_genres";
        return namedParameterJdbcOperations.query(sql, (rs, rowNum) ->
                new BookGenreRelation(rs.getLong("book_id"), rs.getLong("genre_id")));
    }

    private void mergeBooksInfo(
            List<Book> booksWithoutGenres, List<Genre> genres,
            List<BookGenreRelation> relations
    ) {
        Map<Long, Book> bookMap = booksWithoutGenres.stream()
                .collect(Collectors.toMap(Book::getId, book -> book));
        Map<Long, Genre> genreMap = genres.stream()
                .collect(Collectors.toMap(Genre::getId, genre -> genre));

        for (BookGenreRelation relation : relations) {
            Book book = bookMap.get(relation.bookId());
            Genre genre = genreMap.get(relation.genreId());
            if (book != null && genre != null) {
                book.getGenres().add(genre);
            }
        }
    }

    private Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO books (title, author_id) VALUES (:title, :authorId)";
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("title", book.getTitle())
                .addValue("authorId", book.getAuthor().getId()
                );
        namedParameterJdbcOperations.update(sql, params, keyHolder);
        book.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        batchInsertGenresRelationsFor(book);
        return book;
    }

    private Book update(Book book) {
        String sql = "UPDATE books SET title = :title, author_id = :authorId WHERE id = :id";
        Map<String, Object> params = Map.of(
                "id", book.getId(),
                "title", book.getTitle(),
                "authorId", book.getAuthor().getId()
        );
        int rowsAffected = namedParameterJdbcOperations.update(sql, params);
        if (rowsAffected == 0) {
            throw new EntityNotFoundException("Book not found with id " + book.getId());
        }
        removeGenresRelationsFor(book);
        batchInsertGenresRelationsFor(book);
        return book;
    }

    private void batchInsertGenresRelationsFor(Book book) {
        String sql = "INSERT INTO books_genres (book_id, genre_id) VALUES (:bookId, :genreId)";
        List<Map<String, Long>> batchValues = book.getGenres().stream()
                .map(genre ->
                        Map.of("bookId", book.getId(), "genreId", genre.getId())
                )
                .toList();
        namedParameterJdbcOperations.batchUpdate(sql, batchValues.toArray(new Map[0]));
    }

    private void removeGenresRelationsFor(Book book) {
        String sql = "DELETE FROM books_genres WHERE book_id = :bookId";
        Map<String, Object> params = Collections.singletonMap("bookId", book.getId());
        namedParameterJdbcOperations.update(sql, params);
    }


    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            long id = rs.getLong("id");
            String title = rs.getString("title");
            long authorId = rs.getLong("author_id");
            String authorName = rs.getString("author_name");
            Author author = new Author(authorId, authorName);
            return new Book(id, title, author, new ArrayList<>());
        }
    }

    @SuppressWarnings("ClassCanBeRecord")
    @RequiredArgsConstructor
    private static class BookResultSetExtractor implements ResultSetExtractor<Book> {

        private final GenreRepository genreRepository;

        @Override
        public Book extractData(ResultSet rs) throws SQLException, DataAccessException {
            if (!rs.next()) {
                return null;
            }

            long id = rs.getLong("id");
            String title = rs.getString("title");
            long authorId = rs.getLong("author_id");
            String authorName = rs.getString("author_name");
            Author author = new Author(authorId, authorName);

            Set<Long> genreIds = new HashSet<>();
            do {
                long genreId = rs.getLong("genre_id");
                if (genreId != 0) {
                    genreIds.add(genreId);
                }
            } while (rs.next());

            List<Genre> genres = genreRepository.findAllByIds(genreIds);
            return new Book(id, title, author, genres);
        }
    }

    private record BookGenreRelation(long bookId, long genreId) {
    }
}
