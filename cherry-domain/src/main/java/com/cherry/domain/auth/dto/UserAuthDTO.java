package com.cherry.domain.auth.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Aaliyah
 */
@Data
public class UserAuthDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 用户ID */
    private Long userId;

    /** 用户名 */
    private String username;

    /** 密码（BCrypt） */
    private String password;

    /** 角色编码 */
    private String roleCode;

    /** 角色名 */
    private String roleName;
}
