package towntalk.service;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import towntalk.mapper.BoardDao;
import towntalk.model.Board;
import towntalk.model.Group;

import java.util.List;

/**
 * Created by dooseon on 2016. 6. 6..
 */
@Service
public class BoardService {
    @Autowired
    private SqlSession sqlSession;

    public List<Group> getGroupList(){
        BoardDao boardDao = sqlSession.getMapper(BoardDao.class);
        return boardDao.getGroupList();
    }

    public List<Board> getBoardList(){
        BoardDao boardDao = sqlSession.getMapper(BoardDao.class);
        return boardDao.getBoardList();
    }
}
