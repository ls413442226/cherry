package com.cherry.domain.auth.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)

public class UserVo {
    //用户id
    private Long id;
    //用户名
    private String username;
    //用户手机号
    private String mobile;
    //用户密码
    private String password;
    //用户设备id
    private String deviceId;
    //用户状态
    private Integer status;
    //用户角色
    private List<String> roles;
    //用户创建时间
    private Date created;
    //用户最后登录时间
    private Date updated;
}
