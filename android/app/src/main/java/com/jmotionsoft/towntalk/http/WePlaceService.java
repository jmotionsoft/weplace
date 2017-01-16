package com.jmotionsoft.towntalk.http;

import com.jmotionsoft.towntalk.model.BoardList;
import com.jmotionsoft.towntalk.model.Comment;
import com.jmotionsoft.towntalk.model.Contents;
import com.jmotionsoft.towntalk.model.Member;
import com.jmotionsoft.towntalk.model.Setting;
import com.jmotionsoft.towntalk.model.UserLocation;
import com.jmotionsoft.towntalk.model.Version;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WePlaceService {
    @GET("/version")
    Call<Version> getVersion();

    @FormUrlEncoded
    @POST("/login")
    Call<Member> login(
            @Field("email") String email,
            @Field("password") String password);

    @POST("/join")
    Call<ResponseBody> join(
            @Body Member member);

    @GET("/member")
    Call<Member> getMember();

    @PUT("/member")
    Call<ResponseBody> updateMember(@Body Member member);

    @DELETE("/member")
    Call<ResponseBody> deleteMember(@Query("password") String password);

    @FormUrlEncoded
    @POST("/member/temp_password")
    Call<ResponseBody> getTempPassword(@Field("email") String email);

    @GET("/board")
    Call<BoardList> getBoardList();

    @GET("/board/{board_no}/contents")
    Call<List<Contents>> getContentsList(
            @Path("board_no") int board_no,
            @Query("last_contents_no") Integer last_contents_no);

    @GET("/board/{board_no}/contents/{contents_no}")
    Call<Contents> getContents(
            @Path("board_no") int board_no,
            @Path("contents_no") int contents_no);

    @Multipart
    @POST("/board/{board_no}/contents")
    Call<ResponseBody> addContents(
            @Path("board_no") int board_no,
            @PartMap() Map<String, RequestBody> parts);

    @Multipart
    @POST("/board/{board_no}/contents/{contents_no}")
    Call<ResponseBody> updateContents(
            @Path("board_no") int board_no,
            @Path("contents_no") int contents_no,
            @PartMap() Map<String, RequestBody> parts);

    @DELETE("/board/{board_no}/contents/{contents_no}")
    Call<ResponseBody> deleteContents(
            @Path("board_no") int board_no,
            @Path("contents_no") int contents_no);

    @GET("/contents/{contents_no}/comment")
    Call<List<Comment>> getCommentList(
            @Path("contents_no") int contents_no);

    @GET("/contents/{contents_no}/comment/{comment_no}")
    Call<Comment> getComment(
            @Path("contents_no") int contents_no,
            @Path("comment_no") int comment_no);

    @POST("/contents/{contents_no}/comment")
    Call<ResponseBody> addComment(
            @Path("contents_no") int contents_no,
            @Body Comment comment);

    @PUT("/contents/{contents_no}/comment")
    Call<ResponseBody> editComment(
            @Path("contents_no") int contents_no,
            @Body Comment comment);

    @DELETE("/contents/{contents_no}/comment/{comment_no}")
    Call<ResponseBody> deleteComment(
            @Path("contents_no") int contents_no,
            @Path("comment_no") int comment_no);

    @GET("/location")
    Call<List<UserLocation>> getLocationList();

    @POST("/location")
    Call<ResponseBody> addLocation(@Body UserLocation userLocation);

    @PUT("/location/{location_no}/select")
    Call<ResponseBody> selectLocation(@Path("location_no") int location_no);


    @DELETE("/location/{location_no}")
    Call<ResponseBody> removeLocation(@Path("location_no") int location_no);

    @PUT("/logout")
    Call<ResponseBody> logout();

    @GET("/setting")
    Call<Setting> getSetting();

    @PUT("/setting")
    Call<Setting> updateSetting();
}
