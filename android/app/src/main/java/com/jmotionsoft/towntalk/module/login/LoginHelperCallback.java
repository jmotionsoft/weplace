package com.jmotionsoft.towntalk.module.login;

import com.jmotionsoft.towntalk.model.Member;

/**
 * Created by sin31 on 2016-07-28.
 */
public interface LoginHelperCallback {
    void loginSuccess(Member member);

    void loginFalse(String message);
}
