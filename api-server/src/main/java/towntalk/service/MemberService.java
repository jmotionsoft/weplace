package towntalk.service;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import towntalk.controller.JoinController;
import towntalk.mapper.LocationDao;
import towntalk.mapper.MemberDao;
import towntalk.model.Location;
import towntalk.model.Member;
import towntalk.util.AppConfig;
import towntalk.util.CryptoUtil;

@Service
public class MemberService {
    static final Logger logger = LoggerFactory.getLogger(MemberService.class);

    @Autowired
    private SqlSession sqlSession;
    @Autowired
    private AppConfig appConfig;
    @Autowired
    private LocationService locationService;

    public Member getMember(Member member){
        MemberDao memberDao = sqlSession.getMapper(MemberDao.class);
        member.setCrypt_key(appConfig.getPrivateKey());
        return memberDao.getMember(member);
    }

    public Member getMember(int user_no){
        Member member = new Member();
        member.setUser_no(user_no);

        return getMember(member);
    }

    public Member getMemberFromPassword(Member member){
        MemberDao memberDao = sqlSession.getMapper(MemberDao.class);
        member.setCrypt_key(appConfig.getPrivateKey());
        return memberDao.getMemberFromPassword(member);
    }

    @Transactional
    public boolean createMember(Member member){
        MemberDao memberDao = sqlSession.getMapper(MemberDao.class);

        member.setCrypt_key(appConfig.getPrivateKey());
        memberDao.createMember(member);

        if(member.getUser_no() == null){
            logger.error("createMember() => User No is Null.");
            sqlSession.rollback();
            return false;
        }

        Location location = new Location();
        location.setUser_no(member.getUser_no());
        if(member.getAddress() == null || member.getAddress().trim().equals("")){
            location.setLocation_name("DEFAULT");
        }else{
            String name = "DEFAULT";
            try{
                name = member.getAddress().substring(member.getAddress().lastIndexOf(" "), member.getAddress().length());
            }catch (Exception e){
                e.printStackTrace();
            }
            location.setLocation_name(name);
        }
        location.setLatitude(member.getLatitude());
        location.setLongitude(member.getLongitude());
        location.setAddress(member.getAddress());
        location.setDefault_yn("Y");

        locationService.addLocation(location);
        return true;
    }

    public int editMember(Member member){
        MemberDao memberDao = sqlSession.getMapper(MemberDao.class);
        member.setCrypt_key(appConfig.getPrivateKey());
        return memberDao.editMember(member);
    }

    public int removeMember(Member member){
        MemberDao memberDao = sqlSession.getMapper(MemberDao.class);
        return memberDao.removeMember(member);
    }

    public boolean checkEmail(String email) throws Exception{
        MemberDao memberDao = sqlSession.getMapper(MemberDao.class);

        Member member = new Member();
        member.setCrypt_key(appConfig.getPrivateKey());
        member.setEmail(email);

        if(memberDao.countMember(member) > 0)
            return false;
        else
            return true;
    }

    public boolean checkNickName(String nickName){
        MemberDao memberDao = sqlSession.getMapper(MemberDao.class);

        Member member = new Member();
        member.setCrypt_key(appConfig.getPrivateKey());
        member.setNickname(nickName);

        if(memberDao.countMember(member) > 0)
            return false;
        else
            return true;
    }
}
