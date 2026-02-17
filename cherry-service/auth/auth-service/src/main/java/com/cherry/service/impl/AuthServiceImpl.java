package com.cherry.service.impl;

import com.cherry.api.AuthService;
import com.cherry.domain.auth.pojo.TokenPair;
import com.cherry.domain.auth.vo.UserVo;
import com.cherry.mapper.UserMapper;
import com.cherry.commons.utils.JwtUtil;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@DubboService
public class AuthServiceImpl implements AuthService {

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;

    @Resource
    private UserMapper userMapper;

    @Override
    public TokenPair login(String username, String password, String deviceId, List<String> roles) {

        UserVo user = userMapper.selectByUsername(username);

        System.out.println("user：" + user);

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("密码错误");
        }

        Long userId = user.getId();

        String accessToken = JwtUtil.generateAccessToken(userId, deviceId, roles);

        String refreshToken = UUID.randomUUID().toString();

        String key = "login:" + userId + ":" + deviceId;

        redisTemplate.opsForValue()
                .set(key, refreshToken, 7, TimeUnit.DAYS);

        TokenPair pair = new TokenPair();
        pair.setAccessToken(accessToken);
        pair.setRefreshToken(refreshToken);

        System.out.println("pair:" + pair);

        return pair;
    }

    @Override
    public boolean checkLogin(Long userId, String deviceId) {
        String key = "login:" + userId + ":" + deviceId;
        return redisTemplate.hasKey(key);
    }

    @Override
    public TokenPair refresh(Long userId, String deviceId, String refreshToken) {

        String key = "login:" + userId + ":" + deviceId;

        String storedToken = redisTemplate.opsForValue().get(key);
        if (storedToken == null) {
            throw new RuntimeException("登录已失效");
        }
        if (!storedToken.equals(refreshToken)) {
            throw new RuntimeException("非法刷新请求");
        }

        /**
         * 生成新的访问令牌。
         * 使用JWT工具类创建一个新的访问令牌，包含用户ID、设备ID和角色信息。
         *
         * @param userId 用户唯一标识符
         * @param deviceId 设备唯一标识符
         * @param roles 用户角色列表
         * @return String 新生成的JWT访问令牌字符串
         */
        String newAccessToken = JwtUtil.generateAccessToken(userId, deviceId, List.of("roles"));


        TokenPair pair = new TokenPair();
        pair.setAccessToken(newAccessToken);
        pair.setRefreshToken(refreshToken);

        return pair;
    }
}
