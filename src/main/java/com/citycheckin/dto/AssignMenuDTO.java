package com.citycheckin.dto;

import lombok.Data;

import java.util.List;

@Data
public class AssignMenuDTO {
    private Integer roleId;
    private List<Integer> menuIds;
}
