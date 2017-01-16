package towntalk.service;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import towntalk.mapper.VersionDao;
import towntalk.model.Version;

@Service
public class VersionService {
	static final Logger logger = LoggerFactory.getLogger(VersionService.class);

	@Autowired
	private SqlSession sqlSession;
	
	public Version getLastVersion(){
		VersionDao versionDao = sqlSession.getMapper(VersionDao.class);
		return versionDao.getLastVersion();
	}
}
