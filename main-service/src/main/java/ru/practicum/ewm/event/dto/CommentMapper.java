package ru.practicum.ewm.event.dto;


import ru.practicum.ewm.event.model.Comment;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.dto.UserMapper;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;

public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .author(UserMapper.toUserShortDto(comment.getAuthor()))
                .created(comment.getCreated())
                .edited(comment.isEdited())
                .build();
    }

    public static Comment toComment(NewCommentDto newCommentDto, Event event, User author) {
        return Comment.builder()
                .text(newCommentDto.getText())
                .event(event)
                .author(author)
                .created(LocalDateTime.now())
                .edited(false)
                .build();
    }

    public static Comment toComment(UpdateCommentRequest updateCommentRequest, Comment updatedComment) {
        Comment.CommentBuilder commentBuilder = Comment.builder();

        commentBuilder.id(updatedComment.getId());
        commentBuilder.event(updatedComment.getEvent());
        commentBuilder.author(updatedComment.getAuthor());
        commentBuilder.created(updatedComment.getCreated());
        commentBuilder.edited(true);

        commentBuilder.text(updateCommentRequest.getText());

        return commentBuilder.build();
    }
}
