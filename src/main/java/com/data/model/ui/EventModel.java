package com.data.model.ui;

import com.data.model.DeadlineStatus;
import com.data.model.dto.VisionDto;
import com.github.eloyzone.jalalicalendar.JalaliDate;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventModel {
    @Schema(hidden = true)
    private int id;
    private String name;
    // primer date
    private Integer day;
    private Integer month;
    private Integer year;
    @Schema(hidden = true)
    private DeadlineStatus deadlineStatus;
    private VisionDto vision;
    private int groupingNumber;
    private int groupingDay;
    private int daysToEndGrouping;
    private int daysToEndVision;
    private int goneDays;

    public JalaliDate primerJalaliDate() {
        return new JalaliDate(year, month, day);
    }
}
