package com.citycheckin.dto;

import lombok.Data;

import java.util.List;

@Data
public class AssignRoleDTO {
    private Integer userId;
    private List<Integer> roleIds;
}
