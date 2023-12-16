package com.data.model.ui;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VisionModel {
    // target date
    private Integer day = 0;
    private Integer month = 0;
    private Integer year = 0;
    // batch
    private Integer grouping;
}
