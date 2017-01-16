package towntalk.service;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import towntalk.mapper.TempPasswordDao;
import towntalk.model.TempPassword;

import java.util.Random;

/**
 * Created by dooseon on 2016. 6. 6..
 */
@Service
public class TempPasswordService {
    @Autowired
    private SqlSession sqlSession;

    public TempPassword getTempPassword(TempPassword tempPassword){
        TempPasswordDao tempPasswordDao = sqlSession.getMapper(TempPasswordDao.class);
        return tempPasswordDao.getTempPassword(tempPassword);
    }

    public int deleteTempPassword(TempPassword tempPassword){
        TempPasswordDao tempPasswordDao = sqlSession.getMapper(TempPasswordDao.class);
        return tempPasswordDao.deleteTempPassword(tempPassword);
    }

    public int createTempPassword(TempPassword tempPassword){
        TempPasswordDao tempPasswordDao = sqlSession.getMapper(TempPasswordDao.class);

        deleteTempPassword(tempPassword);

        tempPassword.setTemp_password(makeTempPassword());

        return tempPasswordDao.createTempPassword(tempPassword);
    }

    private String makeTempPassword(){
        StringBuffer buffer = new StringBuffer();
        Random random = new Random();

        String chars[] = ("A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z," +
                "a,b,c,d,e,f,g,l,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z," +
                "0,1,2,3,4,5,6,7,8,9").split(",");

        int chars_size = chars.length;
        for(int i = 0; 8 > i; i++){
            buffer.append(chars[random.nextInt(chars_size)]);
        }

        return buffer.toString();
    }
}
