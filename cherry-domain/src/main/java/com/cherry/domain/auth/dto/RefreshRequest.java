package com.cherry.domain.auth.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Aaliyah
 */
@Data
public class RefreshRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long userId;
    private String deviceId;
    private String refreshToken;

}
