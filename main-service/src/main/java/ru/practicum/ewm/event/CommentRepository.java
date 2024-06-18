package ru.practicum.ewm.event;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.event.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllCommentByEventId(Long id);
}
