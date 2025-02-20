package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Comment;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jpa для работы с комментариями")
@DataJpaTest
@Import({JpaCommentRepository.class, JpaBookRepository.class})
class JpaCommentRepositoryTest {

    private static final long FIRST_COMMENT_ID = 1L;

    private static final long FIRST_BOOK_ID = 1L;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TestEntityManager em;

    @DisplayName("должен загружать комментарий по id")
    @Test
    void shouldReturnCorrectCommentById() {
        var actualComment = commentRepository.findById(FIRST_COMMENT_ID);
        var expectedComment = em.find(Comment.class, FIRST_COMMENT_ID);
        assertThat(actualComment).isPresent().get()
                .usingRecursiveComparison().isEqualTo(expectedComment);
    }

    @DisplayName("должен загружать список всех комментариев по id книги")
    @Test
    void shouldReturnCorrectCommentsList() {
        var actualComments = commentRepository.findByBookId(FIRST_BOOK_ID);
        var expectedComments = em.getEntityManager()
                .createQuery("SELECT c FROM Comment c WHERE c.bookId = :bookId", Comment.class)
                .setParameter("bookId", FIRST_BOOK_ID)
                .getResultList();

        assertThat(actualComments).containsExactlyInAnyOrderElementsOf(expectedComments);
        actualComments.forEach(System.out::println);
    }

    @DisplayName("должен сохранять новый комментарий")
    @Test
    void shouldSaveNewComment() {
        var expectedComment = new Comment(0, "New Comment", FIRST_BOOK_ID);
        var returnedComment = commentRepository.save(expectedComment);

        assertThat(returnedComment).isNotNull()
                .matches(comment -> comment.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedComment);

        Comment persistedComment = em.find(Comment.class, returnedComment.getId());

        assertThat(persistedComment).isNotNull()
                .usingRecursiveComparison().isEqualTo(returnedComment);
    }

    @DisplayName("должен сохранять измененный комментарий")
    @Test
    void shouldSaveUpdatedBook() {
        var existingComment = commentRepository.findById(FIRST_COMMENT_ID).orElseThrow();

        existingComment.setText("Updated new comment");

        var returnedComment = commentRepository.save(existingComment);

        assertThat(returnedComment).isNotNull()
                .matches(book -> book.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(existingComment);

        Comment persistedComment = em.find(Comment.class, returnedComment.getId());

        assertThat(persistedComment).isNotNull()
                .usingRecursiveComparison().isEqualTo(returnedComment);
    }

    @DisplayName("должен удалять комментарий по id ")
    @Test
    void shouldDeleteBook() {
        assertThat(commentRepository.findById(FIRST_COMMENT_ID)).isPresent();
        commentRepository.deleteById(FIRST_COMMENT_ID);
        assertThat(commentRepository.findById(FIRST_COMMENT_ID)).isEmpty();
    }
}
