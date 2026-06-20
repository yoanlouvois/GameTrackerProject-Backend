package com.et4.gametrackerproject.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ChangerMdpUserDto {

    private Integer id;

    private String password;

    private String confirmPassword;


}
