package com.data.model;

import com.github.eloyzone.jalalicalendar.JalaliDate;
import com.github.eloyzone.jalalicalendar.JalaliDateFormatter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Vision {
    // target date
    private Integer day = 0;
    private Integer month = 0;
    private Integer year = 0;
    // batch
    private Integer grouping;

    public String targetStringDate() {
        JalaliDateFormatter formatter = new JalaliDateFormatter("dd mm yyyy");
        JalaliDate jalaliDate = new JalaliDate(year, month, day);
        return jalaliDate.format(formatter);
    }

    public JalaliDate targetJalaliDate() {
        return new JalaliDate(year, month, day);
    }
}
