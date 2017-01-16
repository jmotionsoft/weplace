package towntalk.util;

import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

@Component
public class CryptoUtil {
	static final Logger logger = LoggerFactory.getLogger(CryptoUtil.class);
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private AppConfig appConfig;

	public String encryptPrivateAES256(String value) throws Exception{
		return encryptAES256(getPrivateKeySpec(), value);
	}
	
	public String decryptPrivateAES256(String value) throws Exception{
		return decryptAES256(getPrivateKeySpec(), value);
	}
	
	public String encryptPublicAES256(String value) throws Exception{
		return encryptAES256(getPublicKeySpec(), value);
	}
	
	public String decryptPublicAES256(String value) throws Exception{
		return decryptAES256(getPublicKeySpec(), value);
	}
	
	private SecretKeySpec getPrivateKeySpec(){
		byte[] bytes = new byte[32];
		bytes = Arrays.copyOfRange(Base64Utils.decodeFromString(appConfig.getPrivateKey()), 0, 32);
		
		return new SecretKeySpec(bytes, "AES");
	}
	
	private SecretKeySpec getPublicKeySpec(){
		byte[] bytes = new byte[32];
		bytes = Arrays.copyOfRange(Base64Utils.decodeFromString(appConfig.getPublicKey()), 0, 32);
		
		return new SecretKeySpec(bytes, "AES");
	}
	
	private String encryptAES256(SecretKeySpec spec, String value) throws Exception{
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, spec);

		String encrypted = Base64Utils.encodeToString(cipher.doFinal(value.getBytes()));
		return encrypted;
	}
	
	private String decryptAES256(SecretKeySpec spec, String value) throws Exception{
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, spec);
		
		String decrypted = new String(cipher.doFinal(Base64Utils.decodeFromString((value))));
		return decrypted;
	}
	
	public String encryptPassword(String password){
		return passwordEncoder.encode(password);
	}
	
	public boolean checkPassword(String rawPassword, String encodedPassword){
		return passwordEncoder.matches(rawPassword, encodedPassword);
	}
	
	public String encryptUserNo(int userNo) throws Exception{
		String sumUserNo = ""+userNo+"";
		return encryptPublicAES256(sumUserNo);
	}
	
	public int decryptUserNo(String value) throws Exception{
		String sumUserNo = decryptPublicAES256(value);
		int length = sumUserNo.length();
		
		String userNo = sumUserNo.substring(3).substring(0, length-6);
		
		return Integer.parseInt(userNo);
	}
	
	public String base64encoding(String value){
		return Base64Utils.encodeToString(value.getBytes());
	}
	
	public String base64decoding(String value){
		return new String(Base64Utils.decodeFromString(value));
	}
}


