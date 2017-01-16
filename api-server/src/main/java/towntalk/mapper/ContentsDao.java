package towntalk.mapper;

import towntalk.model.Contents;

import java.util.List;

/**
 * Created by sin31 on 2016-06-09.
 */
public interface ContentsDao {
    List<Contents> getContentsList(Contents contents);

    Contents getContents(Contents contents);

    int insertContents(Contents contents);

    int updateContents(Contents contents);

    int updateViewCount(Contents contents);

    int deleteContents(Contents contents);
}
