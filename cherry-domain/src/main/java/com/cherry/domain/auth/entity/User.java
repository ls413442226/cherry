package com.cherry.domain.auth.entity;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统用户实体
 * 对应表：sys_user
 * @author Aaliyah
 */
@Data
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 用户编号（对外唯一标识） */
    private String userNo;

    /** 登录账号（唯一） */
    private String username;

    /** 昵称（可重复） */
    private String displayName;

    /** 加密后的密码 */
    private String password;

    /** 邮箱（唯一） */
    private String email;

    /** 手机号（唯一） */
    private String phone;

    /** 头像URL地址 */
    private String avatar;

    /** 性别：0未知 1男 2女 */
    private Integer gender;

    /** 账号状态：1正常 2冻结 3禁用 */
    private Integer status;

    /** 连续登录失败次数 */
    private Integer loginFailCount;

    /** 最后登录IP地址 */
    private String lastLoginIp;

    /** 最后登录时间 */
    private LocalDateTime lastLoginTime;

    /** 注册来源（WEB ANDROID IOS） */
    private String registerSource;

    /** 逻辑删除标记：0未删除 1已删除 */
    private Integer isDeleted;

    /** 乐观锁版本号 */
    private Integer version;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}


