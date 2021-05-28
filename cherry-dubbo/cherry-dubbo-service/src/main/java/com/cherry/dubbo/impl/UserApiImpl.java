package com.cherry.dubbo.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cherry.api.UserApi;
import com.cherry.domain.db.User;
import com.cherry.dubbo.mapper.UserMapper;
import org.apache.dubbo.config.annotation.Service;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@Service
public class UserApiImpl implements UserApi {

    @Autowired
    private UserMapper userMapper;
    

    @Override
    public void saveUser(User userDb) {
        userMapper.insert(userDb);
    }



    @Override
    public String queryUser(String username) {
        String s = userMapper.queryUser(username);
        return s;
    }

    @Override
    public int queryUserIsNull(String username) {
        return userMapper.queryUserisNull(username);
    }

    @Override
    public int queryPhoneIsNull(String phone) {
        return userMapper.queryPhoneisNull(phone);
    }

    @Override
    public User queryUserNull(String username) {
        User user = userMapper.querIsNull(username);
        return user;

    }

    @Override
    public void login(User user) {

    }
}
