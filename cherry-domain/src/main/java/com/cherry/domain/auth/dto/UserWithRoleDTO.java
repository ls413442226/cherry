package com.cherry.domain.auth.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Aaliyah
 */
@Data
public class UserWithRoleDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 用户ID */
    private Long userId;

    /** 用户名 */
    private String username;

    /** 密码 */
    private String password;

    /** 角色ID */
    private Long roleId;

    /** 角色编码 */
    private String roleCode;

    /** 角色名称 */
    private String roleName;

    /** 用户状态 */
    private Integer status;
}
