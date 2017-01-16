package towntalk.controller;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import towntalk.model.Member;
import towntalk.service.MemberService;
import towntalk.util.HttpReturn;
import towntalk.util.TokenSupport;

@RestController
public class LoginController {
	static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	
	@Autowired
	private TokenSupport tokenSupport;

	@Autowired
	private MemberService memberService;

	@RequestMapping("/login")
	public ResponseEntity<?> login(
			@RequestParam(value="email") String email, 
			@RequestParam(value="password") String password,
			HttpServletResponse response) throws Exception{

		Member searchMember = new Member();
		searchMember.setEmail(email);
		searchMember.setPassword(password);
		searchMember.setState(Member.STATE_ACTIVE);

		Member member = memberService.getMemberFromPassword(searchMember);
		if(member == null || member.getUser_no() == null)
			return HttpReturn.NOT_FOUND();
		
		String token = tokenSupport.getToken(member);
		response.addHeader(TokenSupport.TOKEN_NAME, token);
		response.addHeader(TokenSupport.TOKEN_EXP, String.valueOf(tokenSupport.getTokenExp()));

		member.setPassword(null);
		member.setState(null);

		return HttpReturn.OK(member);
	}

	@RequestMapping("/logout")
	public ResponseEntity<?> logout() throws Exception{
		int user_no = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return HttpReturn.NO_CONTENT();
	}
}
