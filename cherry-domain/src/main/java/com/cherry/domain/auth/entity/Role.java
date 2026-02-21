package com.cherry.domain.auth.entity;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 角色实体
 * 对应表：sys_role
 * @author Aaliyah
 */
@Data
public class Role implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 角色ID */
    private Long id;

    /** 角色编码（唯一） */
    private String roleCode;

    /** 角色名称 */
    private String roleName;

    /** 角色描述 */
    private String description;

    /** 状态：1启用 0禁用 */
    private Integer status;

    /** 逻辑删除标记 */
    private Integer isDeleted;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}


