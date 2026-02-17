package com.cherry.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.cherry.domain.auth.vo.UserVo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<UserVo> {
    UserVo selectByUsername(String username);
}
