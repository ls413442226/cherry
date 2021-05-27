package com.cherry.dubbo.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cherry.api.UserApi;
import com.cherry.domain.db.User;
import com.cherry.dubbo.mapper.UserMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;


@Service
public class UserApiImpl implements UserApi {

    @Autowired
    private UserMapper userMapper;

    Boolean isNull = false;

    @Override
    public void saveUser(User userDb) {
        userMapper.insert(userDb);
    }


    @Override
    public boolean queryUserIsNull(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        QueryWrapper<User> isNullUser = queryWrapper.eq("username", username);
        if (isNullUser == null){
            isNull = true;
            return isNull;
        }
        return isNull;
    }

    @Override
    public boolean queryPhoneIsNull(String phone) {
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        QueryWrapper<User> isNullPhone = queryWrapper.eq("phone", phone);
        if (isNullPhone == null){
            isNull = true;
            return isNull;
        }
        return isNull;
    }
}
