package com.cherry.dubbo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cherry.domain.login.db.User;

public interface UserMapper extends BaseMapper<User> {

    User queryUser(String username);


    int queryUserisNull(String username);

    int queryPhoneisNull(String mobile);
}
