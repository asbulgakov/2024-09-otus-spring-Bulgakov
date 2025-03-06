package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.JpaCommentRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Сервис для работы с комментариями")
@DataJpaTest
@Import({CommentServiceImpl.class, JpaCommentRepository.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class CommentServiceIntegrationTest {

    @Autowired
    private CommentService commentService;

    @Test
    @DisplayName("должен сохранять новый комментарий")
    void shouldInsertAndFindComment() {
        Comment comment = commentService.insert("Test Comment", 1L);
        Optional<Comment> foundComment = commentService.findById(comment.getId());

        assertThat(foundComment).isPresent();
        assertThat(foundComment.get().getText()).isEqualTo("Test Comment");
    }

    @Test
    @DisplayName("должен сохранять измененный комментарий")
    void shouldUpdateComment() {
        Comment comment = commentService.insert("Test Comment", 1L);
        Comment updatedComment = commentService.update(comment.getId(), "Updated Comment");

        Optional<Comment> foundComment = commentService.findById(updatedComment.getId());
        assertThat(foundComment).isPresent();
        assertThat(foundComment.get().getText()).isEqualTo("Updated Comment");
    }

    @Test
    @DisplayName("должен находить комментарий по id")
    void shouldFindCommentById() {
        Comment comment = commentService.insert("Test Comment", 1L);
        Optional<Comment> foundComment = commentService.findById(comment.getId());

        assertThat(foundComment).isPresent();
        assertThat(foundComment.get().getId()).isEqualTo(comment.getId());
    }

    @Test
    @DisplayName("должен находить комментарии по id книги")
    void shouldFindCommentsByBookId() {
        List<Comment> comments = commentService.findByBookId(1L);

        assertThat(comments).hasSize(2);
    }

    @Test
    @DisplayName("должен удалять комментарий по id")
    void shouldDeleteComment() {
        Comment comment = commentService.insert("Test Comment", 1L);
        commentService.deleteById(comment.getId());

        Optional<Comment> foundComment = commentService.findById(comment.getId());
        assertThat(foundComment).isNotPresent();
    }

    @Test
    @DisplayName("Проверяет на отсутствие LazyInitializationException")
    void shouldNotThrowLazyInitializationException() {
        Comment comment = commentService.insert("Test Comment", 1L);
        Optional<Comment> foundComment = commentService.findById(comment.getId());

        assertThat(foundComment).isPresent();
    }
}