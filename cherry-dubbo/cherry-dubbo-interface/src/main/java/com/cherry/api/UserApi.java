package com.cherry.api;

import com.cherry.domain.login.db.User;


public interface UserApi {

    void saveUser(User user);

    User queryUser(String username);

    int queryUserIsNull(String username);

    int queryPhoneIsNull(String phone);

}
