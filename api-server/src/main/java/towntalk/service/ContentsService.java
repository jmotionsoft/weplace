package towntalk.service;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import towntalk.mapper.ContentsDao;
import towntalk.model.Contents;
import towntalk.util.AppConfig;
import towntalk.util.CryptoUtil;
import towntalk.util.LocationUtil;

import java.util.List;

/**
 * Created by sin31 on 2016-06-09.
 */
@Service
public class ContentsService {

    @Autowired
    private SqlSession sqlSession;
    @Autowired
    private AppConfig appConfig;


    public List<Contents> getContentsList(Contents contents){
        ContentsDao contentsDao = sqlSession.getMapper(ContentsDao.class);

        contents.setCrypt_key(appConfig.getPrivateKey());

        return contentsDao.getContentsList(contents);
    }

    @Transactional
    public Contents getContents(int board_no, int contents_no){
        ContentsDao contentsDao = sqlSession.getMapper(ContentsDao.class);

        Contents contents = new Contents();
        contents.setBoard_no(board_no);
        contents.setContents_no(contents_no);
        contents.setCrypt_key(appConfig.getPrivateKey());

        contentsDao.updateViewCount(contents);

        return contentsDao.getContents(contents);
    }

    public int insertContents(Contents contents){
        ContentsDao contentsDao = sqlSession.getMapper(ContentsDao.class);

        contents.setState(Contents.STATE_ACTIVE);
        contents.setCrypt_key(appConfig.getPrivateKey());

        return contentsDao.insertContents(contents);
    }

    @Transactional
    public int updateContents(Contents contents){
        ContentsDao contentsDao = sqlSession.getMapper(ContentsDao.class);

        int update_count = contentsDao.updateContents(contents);
        if(update_count > 1){
            sqlSession.rollback();
        }

        return update_count;
    }

    @Transactional
    public int deleteContents(Contents contents){
        ContentsDao contentsDao = sqlSession.getMapper(ContentsDao.class);

        int delete_count = contentsDao.deleteContents(contents);
        if(delete_count > 1){
            sqlSession.rollback();
        }

        return delete_count;
    }
}
