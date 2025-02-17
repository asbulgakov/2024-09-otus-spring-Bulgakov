package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class JpaGenreRepository implements GenreRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public List<Genre> findAll() {
        String jpql = "select g from Genre g";

        return entityManager.createQuery(jpql, Genre.class).getResultList();
    }

    @Override
    public List<Genre> findAllByIds(Set<Long> ids) {
        String jpql = "SELECT g FROM Genre g " +
                "WHERE g.id IN :ids";

        return entityManager.createQuery(jpql, Genre.class)
                .setParameter("ids", ids)
                .getResultList();
    }
}
