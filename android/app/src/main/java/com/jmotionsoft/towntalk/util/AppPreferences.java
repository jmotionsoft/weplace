package com.jmotionsoft.towntalk.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jmotionsoft.towntalk.model.Member;
import com.jmotionsoft.towntalk.model.UserLocation;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by sin31 on 2016-06-27.
 */
public class AppPreferences {
    private final String TAG = getClass().getSimpleName();

    private final String PREFS_NAME = "we_place_prefs_file";

    private final String ITEM_USER_EMAIL ="user_email";
    private final String ITEM_USER_PASSWORD = "user_password";
    private final String ITEM_LOGIN_TOKEN = "login_token";
    private final String ITEM_LOGIN_TOKEN_EXPIRATION = "login_token_expiration";
    private final String ITEM_MEMBER = "member";
    private final String ITEM_LOCATION = "location";
    private final String ITEM_BOARD_FAVORITES = "board_favorites";

    private Context mContext;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    public AppPreferences(Context context) {
        mContext = context;
        mPreferences = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
    }

    public void setEmail(String email){
        mEditor.putString(ITEM_USER_EMAIL, email);
        mEditor.commit();
    }

    public String getEmail(){
        return mPreferences.getString(ITEM_USER_EMAIL, null);
    }

    public void setPassword(String password){
        mEditor.putString(ITEM_USER_PASSWORD, password);
        mEditor.commit();
    }

    public String getPassword(){
        return mPreferences.getString(ITEM_USER_PASSWORD, null);
    }

    public void setLoginToken(String token){
        mEditor.putString(ITEM_LOGIN_TOKEN, token);
        mEditor.commit();
    }

    public String getLoginToken(){
        return mPreferences.getString(ITEM_LOGIN_TOKEN, null);
    }

    public void setLoginTokenExpiration(String tokenExp){
        Date date = new Date(Long.parseLong(tokenExp));

        mEditor.putString(ITEM_LOGIN_TOKEN_EXPIRATION,
                DateUtil.dateToString(date, DateUtil.FORMAT_DATETIME));
        mEditor.commit();
    }

    public long getLoginTokenExpirationDay(){
        String date = mPreferences.getString(ITEM_LOGIN_TOKEN_EXPIRATION, null);
        if(date == null) return -1;

        try{
            return DateUtil.getDayInterval(DateUtil.FORMAT_DATETIME, date);
        }catch (Exception e){
            CLog.e(TAG, e);
            return -1;
        }
    }

    public void setMember(Member member){
        mEditor.putString(ITEM_MEMBER, new Gson().toJson(member));
        mEditor.commit();
    }

    public Member getMember(){
        String json = mPreferences.getString(ITEM_MEMBER, null);
        if(json == null) return null;

        return new Gson().fromJson(json, Member.class);
    }

    public void setLocation(UserLocation location){
        mEditor.putString(ITEM_LOCATION, new Gson().toJson(location));
        mEditor.commit();
    }

    public UserLocation getLocation(){
        String json = mPreferences.getString(ITEM_LOCATION, null);
        if(json == null) return null;

        return new Gson().fromJson(json, UserLocation.class);
    }

    public void putAndRemoveBoardFavorites(Integer boardNo){
        LinkedHashSet<Integer> boardList = getBoardFavorites();

        if(boardList.contains(boardNo))
            boardList.remove(boardNo);
        else
            boardList.add(boardNo);

        mEditor.putString(ITEM_BOARD_FAVORITES, new Gson().toJson(boardList));
        mEditor.commit();
    }

    public boolean isFavoritesBoard(Integer boardNo){
        LinkedHashSet<Integer> boardList = getBoardFavorites();
        return boardList.contains(boardNo);
    }

    public LinkedHashSet<Integer> getBoardFavorites(){
        String json = mPreferences.getString(ITEM_BOARD_FAVORITES, null);
        if(json == null)
            return new LinkedHashSet<Integer>();

        return new Gson().fromJson(json, new TypeToken<LinkedHashSet<Integer>>() {}.getType());
    }

    public void clearMember(){
        mEditor.putString(ITEM_USER_EMAIL, null);
        mEditor.putString(ITEM_USER_PASSWORD, null);
        mEditor.putString(ITEM_LOGIN_TOKEN, null);
        mEditor.putString(ITEM_MEMBER, new Gson().toJson(null));
        mEditor.commit();
    }
}
