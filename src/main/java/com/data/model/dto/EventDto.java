package com.data.model.dto;

import com.data.model.DeadlineStatus;
import com.github.eloyzone.jalalicalendar.JalaliDate;
import com.github.eloyzone.jalalicalendar.JalaliDateFormatter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {

    private Integer id;
    private Integer day;
    private Integer month;
    private Integer year;
    private VisionDto vision;
    private DeadlineStatus deadlineStatus;

    public String primerStringDate() {
        JalaliDateFormatter formatter = new JalaliDateFormatter("dd mm yyyy");
        JalaliDate jalaliDate = new JalaliDate(year, month, day);
        return jalaliDate.format(formatter);
    }

    public JalaliDate primerJalaliDate() {
        return new JalaliDate(year, month, day);
    }
}
