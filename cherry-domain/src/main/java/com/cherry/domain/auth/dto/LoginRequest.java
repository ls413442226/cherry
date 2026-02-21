package com.cherry.domain.auth.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Aaliyah
 */
@Data
public class LoginRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String username;

    private String password;

    private String deviceId;

    private String deviceType;

    private String deviceName;

}
