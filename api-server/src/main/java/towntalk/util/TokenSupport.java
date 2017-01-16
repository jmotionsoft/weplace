package towntalk.util;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import towntalk.model.Member;

@Component
public class TokenSupport {
	static final Logger logger = LoggerFactory.getLogger(TokenSupport.class);
	
	public static final String TOKEN_NAME = "X-Auth-Token";
	public static final String TOKEN_EXP = "X-Auth-Token-Exp";

	@Autowired
	private AppConfig appConfig;
	@Autowired
	private CryptoUtil cryptoUtil;

	private Long tokenExp = 0L;

	public TokenSupport(){
		
	}
	
	public String getToken(Member member) throws Exception{
		tokenExp = System.currentTimeMillis() + Long.parseLong(appConfig.getTokenExpiration());

		String token = Jwts.builder()
				.setHeaderParam("typ", "JWT")
				.setSubject(appConfig.getTokenSubject())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(tokenExp))
				.claim("user_no", cryptoUtil.encryptUserNo(member.getUser_no()))
				.claim("role", "ROLE_MEMBER")
				.signWith(SignatureAlgorithm.HS512, appConfig.getSecretKey())
				.compact();
		
		return token;
	}

	public Long getTokenExp(){
		return tokenExp;
	}
}
