package com.cherry.consumer.controller;

import com.cherry.consumer.service.UserService;
import com.cherry.domain.vo.ErrorResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Api(value="V1")
@RestController
@RequestMapping("/Api")
public class UserController {

    @Autowired
    private UserService userService;


    //发送验证码
    @ApiOperation(value="发送验证码")
    @PostMapping("/sendCode")
    public ResponseEntity sendCode(@RequestBody Map map){
        String mobile = (String) map.get("phone");
        return userService.sendCode(mobile);
    }

    //图片验证
    @ApiOperation(value="验证码图片")
    @GetMapping("/Captcha")
    public ResponseEntity  verifyCode(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        userService.verifyCode(req,resp);
        return ResponseEntity.ok(null);
    }

    //校验
    @ApiOperation(value="保存账户信息")
    @PostMapping("/save")
    public ResponseEntity saveUser(@RequestBody Map map) throws Exception {
        return userService.saveUser(map);
    }

}
