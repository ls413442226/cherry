package com.cherry.dubbo.impl;

import com.cherry.api.UserApi;
import com.cherry.dubbo.mapper.UserMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;


@Service
public class UserApiImpl implements UserApi {

    @Autowired
    private UserMapper userMapper;


}
