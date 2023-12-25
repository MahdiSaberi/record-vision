package com.data.model.dto;

import com.data.model.DeadlineStatus;
import com.data.model.Vision;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EventUpdateDto {
    private int id;
    private String name;

    private Integer day;
    private Integer month;
    private Integer year;

    private Vision vision;
    @Schema(hidden = true)
    private DeadlineStatus deadlineStatus;
}
