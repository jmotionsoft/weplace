package com.jmotionsoft.towntalk.model;

import java.util.List;

/**
 * Created by sin31 on 2016-06-27.
 */
public class BoardList {
    public static final int PAGE_COUNT = 20;

    private List<Group> groupList;
    private List<Board> boardList;

    public List<Group> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<Group> groupList) {
        this.groupList = groupList;
    }

    public List<Board> getBoardList() {
        return boardList;
    }

    public void setBoardList(List<Board> boardList) {
        this.boardList = boardList;
    }
}
