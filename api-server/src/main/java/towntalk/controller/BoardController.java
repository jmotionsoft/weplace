package towntalk.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import towntalk.model.Board;
import towntalk.model.Group;
import towntalk.service.BoardService;
import towntalk.util.HttpReturn;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dooseon on 2016. 6. 6..
 */
@RestController
public class BoardController {
    static final Logger logger = LoggerFactory.getLogger(BoardController.class);

    @Autowired
    private BoardService boardService;

    @RequestMapping("/board")
    public ResponseEntity<?> getBoardList(){
        List<Group> groupList = boardService.getGroupList();
        List<Board> boardList = boardService.getBoardList();

        Map resultMap = new LinkedHashMap();
        resultMap.put("groupList", groupList);
        resultMap.put("boardList", boardList);

        return HttpReturn.OK(resultMap);
    }

}
