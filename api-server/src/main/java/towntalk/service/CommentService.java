package towntalk.service;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import towntalk.mapper.CommentDao;
import towntalk.model.Comment;
import towntalk.util.AppConfig;
import towntalk.util.CryptoUtil;

import java.util.List;

/**
 * Created by sin31 on 2016-06-13.
 */

@Service
public class CommentService {
    @Autowired
    private SqlSession sqlSession;
    @Autowired
    private AppConfig appConfig;

    public List<Comment> getCommentList(Comment comment){
        CommentDao commentDao = sqlSession.getMapper(CommentDao.class);

        comment.setCrypt_key(appConfig.getPrivateKey());

        return commentDao.getCommentList(comment);
    }

    public Comment getComment(Comment comment){
        CommentDao commentDao = sqlSession.getMapper(CommentDao.class);
        comment.setCrypt_key(appConfig.getPrivateKey());

        return commentDao.getComment(comment);
    }

    public int insertComment(Comment comment){
        CommentDao commentDao = sqlSession.getMapper(CommentDao.class);

        comment.setState(Comment.STATE_ACTIVE);

        return commentDao.insertComment(comment);
    }

    public int editComment(Comment comment){
        CommentDao commentDao = sqlSession.getMapper(CommentDao.class);

        return commentDao.editComment(comment);
    }

    @Transactional
    public int deleteComment(Comment comment){
        CommentDao commentDao = sqlSession.getMapper(CommentDao.class);

        int delete_count = commentDao.deleteComment(comment);
        if(delete_count > 1){
            sqlSession.rollback();
        }

        return delete_count;
    }
}
