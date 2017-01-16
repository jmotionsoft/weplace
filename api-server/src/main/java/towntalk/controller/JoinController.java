package towntalk.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import towntalk.model.Member;
import towntalk.service.MemberService;
import towntalk.util.HttpReturn;

import javax.validation.Valid;

@RestController
public class JoinController {
    static final Logger logger = LoggerFactory.getLogger(JoinController.class);

    @Autowired
    private MemberService memberService;

    @RequestMapping(value = "/join", method = RequestMethod.POST)
    public ResponseEntity<?> join(@Valid @RequestBody Member member) throws Exception{
        if(!memberService.checkEmail(member.getEmail()))
            return HttpReturn.CONFLICT("email");

        if(!memberService.checkNickName(member.getNickname()))
            return HttpReturn.CONFLICT("nickname");

        if(member.getLatitude() == null || member.getLongitude() == null || member.getAddress() == null){
            return HttpReturn.BAD_REQUEST("location is null.");
        }

        member.setState(Member.STATE_ACTIVE);

        if(memberService.createMember(member))
            return HttpReturn.NO_CONTENT();
        else
            return HttpReturn.INTERNAL_SERVER_ERROR();
    }
}
