package ru.otus.hw.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.dto.CommentDto;

@Component
@RequiredArgsConstructor
public class CommentMapper {

    private final BookMapper bookMapper;

    public CommentDto toDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                bookMapper.toDto(comment.getBook())
        );
    }
}
