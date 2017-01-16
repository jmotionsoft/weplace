package towntalk.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import towntalk.model.Version;
import towntalk.service.VersionService;
import towntalk.spring.security.RestAuthenticationEntryPoint;
import towntalk.util.HttpReturn;

@RestController
@RequestMapping("/version")
public class VersionController {
	static final Logger logger = LoggerFactory.getLogger(RestAuthenticationEntryPoint.class);
	
	@Autowired
	private VersionService VersionService;
	
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<?> getVersion() throws Exception{
		Version version = VersionService.getLastVersion();
		
		if(version == null)
			return HttpReturn.NOT_FOUND();
		
		return HttpReturn.OK(version);
	}
}