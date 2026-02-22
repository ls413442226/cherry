package com.cherry.service.impl;

import com.cherry.domain.auth.dto.UserAuthDTO;
import com.cherry.mapper.UserMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author Aaliyah
 */
@Slf4j
@Service
public class SecurityUserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        List<UserAuthDTO> list =
                userMapper.selectUserWithRolesByUsername(username);

        if (list == null || list.isEmpty()) {
            throw new UsernameNotFoundException("用户不存在");
        }

        UserAuthDTO first = list.get(0);

        Long userId = first.getUserId();
        String password = first.getPassword();

        // ✅ 收集角色
        List<SimpleGrantedAuthority> authorities = list.stream()
                .map(UserAuthDTO::getRoleCode)
                .filter(Objects::nonNull)
                .map(code -> new SimpleGrantedAuthority("ROLE_" + code))
                .toList();

        return new User(
                userId.toString(),
                password,
                authorities
        );
    }

}
