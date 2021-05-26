package com.cherry.consumer.controller;

import com.cherry.consumer.service.UserService;
import com.cherry.domain.vo.ErrorResult;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/Api")
public class UserController {

    @Autowired
    private UserService userService;


    //发送验证码
    @PostMapping("/sendCode")
    public ResponseEntity sendCode(@Param("mobile") String mobile){
        userService.sendCode(mobile);
        return ResponseEntity.ok().body(ErrorResult.error());
    }

    //图片验证
    @GetMapping("/Captcha")
    public ResponseEntity  verifyCode(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        userService.verifyCode(req,resp);
        return ResponseEntity.ok(null);
    }

    //校验
    @PostMapping("/save")
    public ResponseEntity saveUser(Map map){
        userService.saveUser(map);
        return ResponseEntity.ok().body(ErrorResult.loginError());
    }

}
