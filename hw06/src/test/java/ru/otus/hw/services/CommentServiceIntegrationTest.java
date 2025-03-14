package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.mappers.BookMapper;
import ru.otus.hw.mappers.CommentMapper;
import ru.otus.hw.models.dto.CommentDto;
import ru.otus.hw.repositories.JpaBookRepository;
import ru.otus.hw.repositories.JpaCommentRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Сервис для работы с комментариями")
@DataJpaTest
@Import({
        CommentServiceImpl.class,
        JpaCommentRepository.class,
        JpaBookRepository.class,
        CommentMapper.class,
        BookMapper.class
})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class CommentServiceIntegrationTest {

    @Autowired
    private CommentService commentService;

    @Test
    @DisplayName("должен сохранять новый комментарий")
    void shouldInsertAndFindComment() {
        CommentDto comment = commentService.insert("Test Comment", 1L);
        Optional<CommentDto> foundComment = commentService.findById(comment.getId());

        assertThat(foundComment).isPresent();
        assertThat(foundComment.get().getText()).isEqualTo("Test Comment");
    }

    @Test
    @DisplayName("должен сохранять измененный комментарий")
    void shouldUpdateComment() {
        CommentDto comment = commentService.insert("Test Comment", 1L);
        CommentDto updatedComment = commentService.update(comment.getId(), "Updated Comment");

        Optional<CommentDto> foundComment = commentService.findById(updatedComment.getId());
        assertThat(foundComment).isPresent();
        assertThat(foundComment.get().getText()).isEqualTo("Updated Comment");
    }

    @Test
    @DisplayName("должен находить комментарий по id")
    void shouldFindCommentById() {
        CommentDto comment = commentService.insert("Test Comment", 1L);
        Optional<CommentDto> foundComment = commentService.findById(comment.getId());

        assertThat(foundComment).isPresent();
        assertThat(foundComment.get().getId()).isEqualTo(comment.getId());
    }

    @Test
    @DisplayName("должен находить комментарии по id книги")
    void shouldFindCommentsByBookId() {
        List<CommentDto> comments = commentService.findByBookId(1L);

        assertThat(comments).hasSize(4);
    }

    @Test
    @DisplayName("должен удалять комментарий по id")
    void shouldDeleteComment() {
        CommentDto comment = commentService.insert("Test Comment", 1L);
        commentService.deleteById(comment.getId());

        Optional<CommentDto> foundComment = commentService.findById(comment.getId());
        assertThat(foundComment).isNotPresent();
    }

    @Test
    @DisplayName("Проверяет на отсутствие LazyInitializationException")
    void shouldNotThrowLazyInitializationException() {
        CommentDto comment = commentService.insert("Test Comment", 1L);
        Optional<CommentDto> foundComment = commentService.findById(comment.getId());

        assertThat(foundComment).isPresent();
    }
}