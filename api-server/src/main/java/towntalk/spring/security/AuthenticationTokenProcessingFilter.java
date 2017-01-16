package towntalk.spring.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import towntalk.model.Member;
import towntalk.model.RequestInfo;
import towntalk.service.MemberService;
import towntalk.util.AppConfig;
import towntalk.util.CryptoUtil;
import towntalk.util.TokenSupport;

@Component("authenticationTokenProcessingFilter")
public class AuthenticationTokenProcessingFilter extends GenericFilterBean{
	static final Logger logger = LoggerFactory.getLogger(AuthenticationTokenProcessingFilter.class);

	@Autowired
	private AppConfig appConfig;
	@Autowired
	private CryptoUtil cryptoUtil;
	@Autowired
	private MemberService memberService;

	public AuthenticationTokenProcessingFilter() {
		
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {		
		
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;

		RequestInfo requestInfo = new RequestInfo(httpServletRequest);
		logger.info(requestInfo.toString());

		String token = httpServletRequest.getHeader(TokenSupport.TOKEN_NAME);
		if(token != null && !token.trim().equals("")){
			try{
				Jws<Claims> claims = Jwts.parser().setSigningKey(appConfig.getSecretKey()).parseClaimsJws(token);
				
				String _sub = claims.getBody().getSubject();
				String _user_no = claims.getBody().get("user_no", String.class);
				String _role = claims.getBody().get("role", String.class);
				Date _exp = claims.getBody().getExpiration();
				
				int user_no = cryptoUtil.decryptUserNo(_user_no);

				Member member = new Member();
				member.setUser_no(user_no);
				member.setState(Member.STATE_ACTIVE);
				member = memberService.getMember(member);

				if(member == null) return;
				
				if(!_sub.equals(appConfig.getTokenSubject()))
					return;
				
				if(System.currentTimeMillis() > _exp.getTime())
					return;
				
				List<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
				roles.add(new SimpleGrantedAuthority(_role));
				
				UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user_no, null, roles);
				authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails((HttpServletRequest) request));
				SecurityContextHolder.getContext().setAuthentication(authenticationToken);
			}catch(Exception e){
				logger.error(e.getMessage(), e);
			}
		}
		
		chain.doFilter(request, response);
	}
}
