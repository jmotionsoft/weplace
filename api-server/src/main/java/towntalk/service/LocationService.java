package towntalk.service;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import towntalk.mapper.LocationDao;
import towntalk.model.Location;
import towntalk.util.AppConfig;
import towntalk.util.LocationUtil;

import java.util.List;

/**
 * Created by dooseon on 2016. 10. 30..
 */

@Service
public class LocationService {
    @Autowired
    private SqlSession sqlSession;
    @Autowired
    private AppConfig appConfig;

    public Location getLocation(int location_no, int user_no){
        LocationDao locationDao = sqlSession.getMapper(LocationDao.class);

        Location location = new Location();
        location.setLocation_no(location_no);
        location.setUser_no(user_no);
        location.setCrypt_key(appConfig.getPrivateKey());

        return locationDao.getLocation(location);
    }

    public List<Location> getLocationList(int user_no){
        LocationDao locationDao = sqlSession.getMapper(LocationDao.class);

        Location location = new Location();
        location.setUser_no(user_no);
        location.setCrypt_key(appConfig.getPrivateKey());

        return locationDao.getLocationList(location);
    }

    public int addLocation(Location location){
        LocationDao locationDao = sqlSession.getMapper(LocationDao.class);

        location.setCrypt_key(appConfig.getPrivateKey());

        int range_km = getLocationRange(location);
        location.setLatitude_range(LocationUtil.getSearchLatitude(range_km));
        location.setLongitude_range(LocationUtil.getSearchLongitude(range_km));

        return locationDao.addLocation(location);
    }

    @Transactional
    public boolean selectLocation(int location_no){
        LocationDao locationDao = sqlSession.getMapper(LocationDao.class);

        int user_no = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Location location = new Location();
        location.setUser_no(user_no);
        location.setDefault_yn("N");

        if(locationDao.editLocation(location) < 1){
            sqlSession.rollback();
            return false;
        }

        location.setLocation_no(location_no);
        location.setDefault_yn("Y");

        int update_count = locationDao.editLocation(location);
        if(update_count > 1)
            sqlSession.rollback();

        return update_count == 1;
    }

    public int editLocation(Location location){
        LocationDao locationDao = sqlSession.getMapper(LocationDao.class);

        location.setCrypt_key(appConfig.getPrivateKey());

        return locationDao.editLocation(location);
    }

    @Transactional
    public int removeLocation(int location_no, int user_no){
        LocationDao locationDao = sqlSession.getMapper(LocationDao.class);

        Location location = new Location();
        location.setCrypt_key(appConfig.getPrivateKey());
        location.setUser_no(user_no);

        List<Location> locationList = locationDao.getLocationList(location);
        if(locationList.size() < 2){
            return -1;
        }

        location.setLocation_no(location_no);
        int delete_count = locationDao.removeLocation(location);

        location.setLocation_no(null);
        locationList.clear();

        locationList = locationDao.getLocationList(location);
        location.setLocation_no(locationList.get(0).getLocation_no());
        location.setDefault_yn("Y");
        locationDao.editLocation(location);

        return delete_count;
    }

    private int getLocationRange(Location location){
        LocationDao locationDao = sqlSession.getMapper(LocationDao.class);
        location.setCrypt_key(appConfig.getPrivateKey());

        int range_km = LocationUtil.MAX_RANGE_HALF_KM;
        while (range_km > LocationUtil.MIN_RANGE_HALF_KM){
            location.setLatitude_range(LocationUtil.getSearchLatitude(range_km));
            location.setLongitude_range(LocationUtil.getSearchLongitude(range_km));

            if(locationDao.getUserCount(location) >= LocationUtil.RANGE_USER_COUNT){
                range_km = range_km - 2;
            }else{
                break;
            }
        }

        return range_km;
    }
}
