package com.data.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VisionDto {
    private Integer day;
    private Integer month;
    private Integer year;
    private Integer grouping;
}
