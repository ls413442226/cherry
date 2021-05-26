package com.cherry.domain.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class User implements Serializable {

    //用户id
    private Long id;
    //用户手机号
    private String mobile;
    //用户密码
    private String password;
    //用户创建时间
    private Date created;
    //用户最后登录时间
    private Date updated;


}
