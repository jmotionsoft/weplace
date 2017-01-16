package towntalk.mapper;

import towntalk.model.TempPassword;

/**
 * Created by dooseon on 2016. 6. 6..
 */
public interface TempPasswordDao {
    TempPassword getTempPassword(TempPassword tempPassword);

    int createTempPassword(TempPassword tempPassword);

    int deleteTempPassword(TempPassword tempPassword);
}
