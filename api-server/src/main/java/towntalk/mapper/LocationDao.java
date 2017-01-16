package towntalk.mapper;

import towntalk.model.Location;

import java.util.List;

/**
 * Created by dooseon on 2016. 10. 30..
 */
public interface LocationDao {
    Location getLocation(Location location);

    List<Location> getLocationList(Location location);

    int getUserCount(Location location);

    int addLocation(Location location);

    int editLocation(Location location);

    int removeLocation(Location location);
}
