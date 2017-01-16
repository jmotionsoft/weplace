package towntalk.spring.security;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import towntalk.model.MemberDetails;

@Component("restAuthenticationProvider")
public class RestAuthenticationProvider implements AuthenticationProvider{
	static final Logger logger = LoggerFactory.getLogger(RestAuthenticationProvider.class);

	@Override
	public boolean supports(Class<?> authentication) {
		logger.debug("supports(): start");
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}	
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		logger.debug("authenticate(): start");
		
		String email = (String)authentication.getPrincipal();
		String password = (String)authentication.getCredentials();
		
		logger.info("Success Authenticate => email: {}, password: {}", email, password);
		
		List<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
		roles.add(new SimpleGrantedAuthority("ROLE_MEMBER"));
		
		MemberDetails memberDetails = new MemberDetails();
		memberDetails.setUsername(email);
		memberDetails.setAuthorities(roles);
		
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password, roles);
		authenticationToken.setDetails(memberDetails);
		
		return authenticationToken;
	}

}
