package ru.otus.hw.repositories;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH;

@Repository
@RequiredArgsConstructor
public class JpaBookRepository implements BookRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public Optional<Book> findById(long id) {
        EntityGraph<?> entityGraph = entityManager.createEntityGraph("book-with-author-and-genres");

        Map<String, Object> properties = new HashMap<>();
        properties.put(FETCH.getKey(), entityGraph);

        return Optional.ofNullable(entityManager.find(Book.class, id, properties));
    }

    @Override
    public List<Book> findAll() {
        EntityGraph<?> entityGraph = entityManager.createEntityGraph("book-with-author");

        String jpql = "SELECT DISTINCT b FROM Book b";

        return entityManager.createQuery(jpql, Book.class)
                .setHint(FETCH.getKey(), entityGraph)
                .getResultList();
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            entityManager.persist(book);
        } else {
            if (entityManager.find(Book.class, book.getId()) == null) {
                throw new EntityNotFoundException("Book not found with id " + book.getId());
            }
            entityManager.merge(book);
        }
        return book;
    }

    @Override
    public void deleteById(long id) {
        Book deletedBook = entityManager.find(Book.class, id);
        if (deletedBook != null) {
            entityManager.remove(deletedBook);
        }
    }
}
