package towntalk.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import towntalk.model.Location;
import towntalk.service.LocationService;
import towntalk.util.HttpReturn;

/**
 * Created by dooseon on 2016. 10. 30..
 */

@RestController
@RequestMapping("/location")
public class LocationController {
    static final Logger logger = LoggerFactory.getLogger(LocationController.class);

    @Autowired
    private LocationService locationService;

    @RequestMapping(value = "/{location_no}", method = RequestMethod.GET)
    public ResponseEntity<?> getLocation(@PathVariable int location_no){
        int user_no = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return HttpReturn.OK(locationService.getLocation(location_no, user_no));
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getLocationList(){
        int user_no = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return HttpReturn.OK(locationService.getLocationList(user_no));
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> addLocation(@RequestBody Location location){
        int user_no = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        location.setUser_no(user_no);
        // todo set range level
        //location.setRange_level();

        int add_count = locationService.addLocation(location);
        if(add_count > 0)
            return HttpReturn.NO_CONTENT();
        else
            return HttpReturn.INTERNAL_SERVER_ERROR();
    }

    @RequestMapping(value = "/{location_no}/select", method = RequestMethod.PUT)
    public ResponseEntity<?> selectLocation(@PathVariable int location_no){
        if(locationService.selectLocation(location_no)){
            return HttpReturn.NO_CONTENT();
        }else{
            return HttpReturn.NOT_MODIFIED("");
        }
    }

    @RequestMapping(value="/{location_no}", method = RequestMethod.PUT)
    public ResponseEntity<?> editLocation(@PathVariable int location_no, @RequestBody Location location){
        int user_no = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        location.setLocation_no(location_no);
        location.setUser_no(user_no);
        // todo set range level
        //location.setRange_level();

        int edit_count = locationService.editLocation(location);
        if(edit_count > 0)
            return HttpReturn.NO_CONTENT();
        else
            return HttpReturn.NOT_MODIFIED();

    }

    @RequestMapping(value = "/{location_no}", method = RequestMethod.DELETE)
    public ResponseEntity<?> removeLocation(@PathVariable int location_no){
        int user_no = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        int remove_count = locationService.removeLocation(location_no, user_no);
        if(remove_count > 0)
            return HttpReturn.NO_CONTENT();
        else if (remove_count == -1)
            return HttpReturn.NOT_MODIFIED("At least one must exist.");
        else
            return HttpReturn.INTERNAL_SERVER_ERROR();
    }
}
