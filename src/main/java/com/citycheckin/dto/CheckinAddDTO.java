package com.citycheckin.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CheckinAddDTO {
    private Integer districtId;
    /** food / scenic */
    private String type;
    private Integer foodTypeId;
    private Integer scenicSpotId;
    private String title;
    private String content;
    private String images;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
}
