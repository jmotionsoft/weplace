package towntalk.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import towntalk.model.Setting;
import towntalk.service.SettingService;
import towntalk.util.HttpReturn;

/**
 * Created by sin31 on 2016-06-14.
 */

@RestController
@RequestMapping(value = "/setting")
public class SettingController {
    static final Logger logger = LoggerFactory.getLogger(SettingController.class);

    @Autowired
    private SettingService settingService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getSetting(){
        int user_no = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Setting setting = settingService.getSetting(user_no);
        if(setting == null){
            return HttpReturn.NOT_FOUND();
        }else{
            return HttpReturn.OK(setting);
        }
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<?> updateSetting(Setting setting){
        int user_no = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        setting.setUser_no(user_no);

        int update_count = settingService.updateSetting(setting);

        if(update_count == 0) {
            return HttpReturn.NOT_FOUND();
        }else if(update_count > 1){
            return HttpReturn.NOT_MODIFIED();
        }else{
            return HttpReturn.NO_CONTENT();
        }
    }
}
