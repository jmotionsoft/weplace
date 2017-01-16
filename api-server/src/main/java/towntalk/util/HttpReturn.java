package towntalk.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class HttpReturn {
	/**
	 * Http Status 200
	 * @param object
	 * @return
	 */
	public static ResponseEntity<?> OK(Object object){
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(object);
	}
	
	/**
	 * Http Status 201
	 * @return
	 */
	public static ResponseEntity<?> CREATED(){
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.build();
	}
	
	/**
	 * Http Status 202
	 * 접수 되었으며, 처리중
	 * @return
	 */
	public static ResponseEntity<?> ACCEPTED(){
		return ResponseEntity
				.status(HttpStatus.ACCEPTED)
				.build();
	}
	
	/**
	 * Http Status 204
	 * 처리 되었으나 컨텐츠를 제공하지 않음 
	 * @return
	 */
	public static ResponseEntity<?> NO_CONTENT(){
		return ResponseEntity
				.status(HttpStatus.NO_CONTENT)
				.build();
	}
	
	/**
	 * Http Status 304
	 * @param object
	 * @return
	 */
	public static ResponseEntity<?> NOT_MODIFIED(Object object){
		return ResponseEntity
				.status(HttpStatus.NOT_MODIFIED)
				.body(object);
	}
	
	/**
	 * Http Status 304
	 * @return
	 */	
	public static ResponseEntity<?> NOT_MODIFIED(){
		return NOT_MODIFIED("Not Modified");
	}
	
	/**
	 * Http Status 400
	 * 잘못된 요청 
	 * @param object
	 * @return
	 */
	public static ResponseEntity<?> BAD_REQUEST(Object object){
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(object);
	}
	
	/**
	 * Http Status 400
	 * 잘못된 요청 
	 * @return
	 */
	public static ResponseEntity<?> BAD_REQUEST(){
		return BAD_REQUEST("Bad Request");
	}
	
	/**
	 * Http Status 401
	 * 권한 없음 
	 * @param object
	 * @return
	 */
	public static ResponseEntity<?> UNAUTHORIZED(Object object){
		return ResponseEntity
				.status(HttpStatus.UNAUTHORIZED)
				.body(object);
	}
	
	/**
	 * Http Status 401
	 * 권한 없음
	 * @return
	 */
	public static ResponseEntity<?> UNAUTHORIZED(){
		return UNAUTHORIZED("Unauthorized");
	}
	
	/**
	 * Http Status 403
	 * 서버가 요청을 거부
	 * @param object
	 * @return
	 */
	public static ResponseEntity<?> FORBIDDEN(Object object){
		return ResponseEntity
				.status(HttpStatus.FORBIDDEN)
				.body(object);
	}
	
	/**
	 * Http Status 403
	 * 서버가 요청을 거부
	 * @return
	 */
	public static ResponseEntity<?> FORBIDDEN(){
		return FORBIDDEN("Forbidden");
	}
	
	/**
	 * Http Status 404
	 * 요청하는 컨텐츠가 없음
	 * @param object
	 * @return
	 */
	public static ResponseEntity<?> NOT_FOUND(Object object){
		return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.body(object);
	}
	
	/**
	 * Http Status 404
	 * 요청하는 컨텐츠가 없음
	 * @return
	 */
	public static ResponseEntity<?> NOT_FOUND(){
		return NOT_FOUND("Not Found");
	}
	
	/**
	 * Http Status 409
	 * 처리중 컨텐츠가 출돌됨 
	 * @param object
	 * @return
	 */
	public static ResponseEntity<?> CONFLICT(Object object){
		return ResponseEntity
				.status(HttpStatus.CONFLICT)
				.body(object);
	}
	
	/**
	 * Http Status 409
	 * 처리중 컨텐츠가 출돌됨 
	 * @return
	 */
	public static ResponseEntity<?> CONFLICT(){
		return CONFLICT("Conflict");
	}
	
	/**
	 * Http Status 500
	 * 서버 에러 
	 * @param object
	 * @return
	 */
	public static ResponseEntity<?> INTERNAL_SERVER_ERROR(Object object){
		return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(object);
	}
	
	/**
	 * Http Status 500
	 * 서버 에러 
	 * @return
	 */
	public static ResponseEntity<?> INTERNAL_SERVER_ERROR(){
		return INTERNAL_SERVER_ERROR("Internal Server Error");
	}
}
