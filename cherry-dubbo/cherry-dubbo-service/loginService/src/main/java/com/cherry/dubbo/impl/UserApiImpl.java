package com.cherry.dubbo.impl;


import com.cherry.api.UserApi;
import com.cherry.domain.login.db.User;
import com.cherry.dubbo.mapper.UserMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;



@Service
public class UserApiImpl implements UserApi {

    @Autowired
    private UserMapper userMapper;


    @Override
    public void saveUser(User userDb) {
        userMapper.insert(userDb);
    }



    @Override
    public User queryUser(String username) {
       User user = userMapper.queryUser(username);
        return user;
    }

    @Override
    public int queryUserIsNull(String username) {
        return userMapper.queryUserisNull(username);
    }

    @Override
    public int queryPhoneIsNull(String phone) {
        return userMapper.queryPhoneisNull(phone);
    }

}
