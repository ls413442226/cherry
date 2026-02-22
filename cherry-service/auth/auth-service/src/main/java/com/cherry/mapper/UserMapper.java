package com.cherry.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cherry.domain.auth.dto.UserAuthDTO;
import com.cherry.domain.auth.dto.UserWithRoleDTO;
import com.cherry.domain.auth.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Aaliyah
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    List<UserAuthDTO> selectUserWithRolesByUsername(String username);

    UserWithRoleDTO selectUserWithRolesByUserId(Long userId);

    List<String> selectRoleCodesByUserId(Long userId);
}
