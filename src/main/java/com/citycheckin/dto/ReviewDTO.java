package com.citycheckin.dto;

import lombok.Data;

@Data
public class ReviewDTO {
    private Integer checkinId;
    /** 1-通过 2-拒绝 */
    private Integer status;
    private String rejectReason;
}
