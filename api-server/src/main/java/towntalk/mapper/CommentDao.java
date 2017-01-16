package towntalk.mapper;

import towntalk.model.Comment;

import java.util.List;

/**
 * Created by sin31 on 2016-06-13.
 */
public interface CommentDao {
    List<Comment> getCommentList(Comment comment);

    Comment getComment(Comment comment);

    int insertComment(Comment comment);

    int editComment(Comment comment);

    int deleteComment(Comment comment);
}
