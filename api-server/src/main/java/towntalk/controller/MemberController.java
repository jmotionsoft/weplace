package towntalk.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import towntalk.model.Email;
import towntalk.model.Member;
import towntalk.model.TempPassword;
import towntalk.service.MemberService;
import towntalk.service.TempPasswordService;
import towntalk.util.EmailSender;
import towntalk.util.HttpReturn;

@RestController
@RequestMapping("/member")
public class MemberController {
	static final Logger logger = LoggerFactory.getLogger(MemberController.class);

	@Autowired
	private MemberService memberService;

	@Autowired
	private TempPasswordService tempPasswordService;

	@Autowired
	private EmailSender emailSender;

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<?> getMember(){
		int user_no = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		Member member = new Member();
		member.setUser_no(user_no);
		member = memberService.getMember(member);

		if(member == null || member.getEmail() == null)
			return HttpReturn.NOT_FOUND();

		member.setPassword(null);

		logger.debug("nickname: "+member.getNickname());

		return HttpReturn.OK(member);
	}

	@RequestMapping(method = RequestMethod.PUT)
	public ResponseEntity<?> editMember(@RequestBody Member member){
		int user_no = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		member.setUser_no(user_no);

		if(member.getNickname() != null){
			if(!memberService.checkNickName(member.getNickname()))
				return HttpReturn.CONFLICT("nickname");
		}

		int editCount = memberService.editMember(member);
		if(editCount > 0)
			return HttpReturn.NO_CONTENT();
		else
			return HttpReturn.NOT_MODIFIED();
	}

	@RequestMapping(method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteMember(@RequestParam String password){
		int user_no = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		Member searchMember = new Member();
		searchMember.setUser_no(user_no);
		searchMember.setPassword(password);

		/*
		Member member = memberService.getMember(searchMember);
		if(member == null)
			return HttpReturn.NOT_FOUND();

		searchMember.setState(Member.STATE_WITHDRAWAL);

		int updateCount = memberService.editMember(searchMember);
		if(updateCount > 0)
			return HttpReturn.NOT_MODIFIED();
		else
			return HttpReturn.NO_CONTENT();
		*/

		int delete_count = memberService.removeMember(searchMember);
		if(delete_count > 0)
			return HttpReturn.NO_CONTENT();
		else
			return HttpReturn.NOT_MODIFIED();
	}

	@RequestMapping(value = "/temp_password", method = RequestMethod.POST)
	public ResponseEntity<?> tempPassword(@RequestParam("email") String email){
		// 사용자 조회
		Member member = new Member();
		member.setEmail(email);

		member = memberService.getMember(member);
		if(member == null || member.getUser_no() == null)
			return HttpReturn.NOT_FOUND();

		// 유효한 임시 비밀번호가 있는지 조회
		TempPassword tempPassword = new TempPassword();
		tempPassword.setUser_no(member.getUser_no());

		tempPassword = tempPasswordService.getTempPassword(tempPassword);
		if(tempPassword != null && tempPassword.getTemp_password() != null)
			return HttpReturn.CONFLICT();

		// 임시 비밀번호 발급
		tempPassword = new TempPassword();
		tempPassword.setUser_no(member.getUser_no());
		int createCount = tempPasswordService.createTempPassword(tempPassword);
		if(createCount != 1)
			return HttpReturn.INTERNAL_SERVER_ERROR();

		// 이메일 발송
		Email sendEmail = new Email();
		sendEmail.setReciver(email);
		sendEmail.setSubject("Towntalk 임시비밀번호 발급");
		sendEmail.setContent(
				"안녕하세요 We Place입니다.\n"
						+"요청에 의해 We Place의 임시비밀 번호를 발급하오니 아래 비밀번호로 로그인해주시기 바랍니다.\n"
						+"\n"
						+"임시비밀번호: "+tempPassword.getTemp_password()+"\n"
						+"\n"
						+"임시비밀번의 유효기간은 발급시간으로부터 24시간까지 입니다.\n"
						+"감사합니다."
		);

		try{
			emailSender.SendEmail(sendEmail);
			return HttpReturn.NO_CONTENT();
		}catch (Exception e){
			// todo 이메일 발송 실패시 오류 처리 -> 데이터 베이스 삭제

			e.printStackTrace();
			return HttpReturn.INTERNAL_SERVER_ERROR();
		}
	}
}
