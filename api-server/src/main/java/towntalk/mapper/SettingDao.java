package towntalk.mapper;

import towntalk.model.Setting;

/**
 * Created by sin31 on 2016-06-14.
 */
public interface SettingDao {
    Setting getSetting(Setting setting);

    int insertSetting(Setting setting);

    int updateSetting(Setting setting);
}
