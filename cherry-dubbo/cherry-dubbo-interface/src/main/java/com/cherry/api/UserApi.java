package com.cherry.api;

import com.cherry.domain.db.User;

import java.util.List;

public interface UserApi {

    void saveUser(User user);



    void login(User user);

    String queryUser(String username);

    int queryUserIsNull(String username);

    int queryPhoneIsNull(String phone);

    User queryUserNull(String username);
}
