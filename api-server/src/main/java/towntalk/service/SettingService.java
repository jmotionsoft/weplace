package towntalk.service;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import towntalk.mapper.SettingDao;
import towntalk.model.Setting;

/**
 * Created by sin31 on 2016-06-14.
 */

@Service
public class SettingService {

    @Autowired
    private SqlSession sqlSession;

    @Transactional
    public Setting getSetting(int user_no){
        SettingDao settingDao = sqlSession.getMapper(SettingDao.class);

        Setting search_setting = new Setting();
        search_setting.setUser_no(user_no);

        Setting setting = settingDao.getSetting(search_setting);
        if(setting != null){
            return setting;
        }

        insertSetting(search_setting);

        return settingDao.getSetting(search_setting);
    }

    public int insertSetting(Setting setting){
        SettingDao settingDao = sqlSession.getMapper(SettingDao.class);

        return settingDao.insertSetting(setting);
    }

    @Transactional
    public int updateSetting(Setting setting){
        SettingDao settingDao = sqlSession.getMapper(SettingDao.class);

        int update_count = settingDao.updateSetting(setting);
        if(update_count > 1){
            sqlSession.rollback();
        }

        return update_count;
    }
}
