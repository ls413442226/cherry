package com.cherry.dubbo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cherry.domain.db.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {

    String queryUser(String username);


    int queryUserisNull(String username);

    int queryPhoneisNull(String mobile);

    User querIsNull(String username);
}
