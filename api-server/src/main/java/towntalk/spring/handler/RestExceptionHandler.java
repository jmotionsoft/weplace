package towntalk.spring.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import towntalk.model.LogError;
import towntalk.service.LogService;

/**
 * Created by sin31 on 2016-08-03.
 */

@ControllerAdvice
public class RestExceptionHandler {
    static final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

    @Autowired
    private LogService logService;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> exceptionHandler(Exception ex){
        logger.debug("Error Message: \n{}", ex.getMessage());

        Integer user_no = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        LogError logError = new LogError();
        logError.setId(String.valueOf(System.currentTimeMillis()));
        if(user_no != null) logError.setUser_no(user_no);
        logError.setMessage(ex.getLocalizedMessage());

        logService.addErrorLog(logError);

        return new ResponseEntity<Object>("Error Code: "+logError.getId(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
