package towntalk.mapper;

import towntalk.model.Board;
import towntalk.model.Group;

import java.util.List;

/**
 * Created by dooseon on 2016. 6. 6..
 */
public interface BoardDao {
    List<Group> getGroupList();

    List<Board> getBoardList();
}
