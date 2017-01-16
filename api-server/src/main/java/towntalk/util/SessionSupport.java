package towntalk.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import towntalk.model.Member;
import towntalk.model.MemberDetails;

public class SessionSupport {
	static final Logger logger = LoggerFactory.getLogger(SessionSupport.class);
	
	@Autowired 
	private AuthenticationManager authenticationManager;
	
	private void createSession(HttpServletRequest request, Member member){
		if(authenticationManager == null)
			logger.debug("authenticationManager is null");
		else
			logger.debug("authenticationManager is not null");
		
		if(request.getSession().getAttribute("userInfo") != null){
			MemberDetails memberDetails = (MemberDetails) request.getSession().getAttribute("userInfo");
			logger.debug("userInfo is not null: "+memberDetails.getUsername());
		}
		
		UsernamePasswordAuthenticationToken authenticationToken 
			= new UsernamePasswordAuthenticationToken(member.getEmail(), member.getPassword());
		
		SecurityContextHolder.getContext().setAuthentication(authenticationManager.authenticate(authenticationToken));
		
		HttpSession session = request.getSession(true);
		session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());		
	}
}
