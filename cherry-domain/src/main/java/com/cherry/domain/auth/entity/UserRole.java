package com.cherry.domain.auth.entity;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户角色关系实体
 * 对应表：sys_user_role
 * @author Aaliyah
 */
@Data
public class UserRole implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 角色ID */
    private Long roleId;

    /** 创建时间 */
    private LocalDateTime createdAt;
}

