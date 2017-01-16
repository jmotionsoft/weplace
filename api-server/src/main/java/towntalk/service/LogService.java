package towntalk.service;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import towntalk.mapper.LogErrorDao;
import towntalk.model.LogError;

/**
 * Created by sin31 on 2016-08-03.
 */

@Service
public class LogService {
    @Autowired
    private SqlSession sqlSession;

    public int addErrorLog(LogError logError){
        LogErrorDao logErrorDao = sqlSession.getMapper(LogErrorDao.class);
        return logErrorDao.addLog(logError);
    }
}
