package com.cherry.api;

import com.cherry.domain.db.User;

public interface UserApi {

    void saveUser(User user);


    boolean queryUserIsNull(String username);

    boolean queryPhoneIsNull(String phone);
}
