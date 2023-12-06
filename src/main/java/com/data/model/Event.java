package com.data.model;

import com.github.eloyzone.jalalicalendar.DateConverter;
import com.github.eloyzone.jalalicalendar.JalaliDate;
import com.github.eloyzone.jalalicalendar.JalaliDateFormatter;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Jacksonized
public class Event {

    private int id;
    // primer date
    private Integer day;
    private Integer month;
    private Integer year;

    private Vision vision;

    public String primerStringDate() {
        JalaliDateFormatter formatter = new JalaliDateFormatter("dd MM yyyy");
        JalaliDate jalaliDate;
        if ((day == null && month == null && year == null) || (day == 0 && month == 0 && year == 0)) {
            DateConverter dateConverter = new DateConverter();
            jalaliDate = dateConverter.gregorianToJalali(LocalDate.now().getYear(), LocalDate.now().getMonth(), LocalDate.now().getDayOfMonth());
        } else {
            jalaliDate = new JalaliDate(year, month, day);
        }
        return jalaliDate.format(formatter);
    }

    public JalaliDate primerJalaliDate() {
        JalaliDate jalaliDate;
        if ((day == null && month == null && year == null) || (day == 0 && month == 0 && year == 0)) {
            DateConverter dateConverter = new DateConverter();
            jalaliDate = dateConverter.gregorianToJalali(LocalDate.now().getYear(), LocalDate.now().getMonth(), LocalDate.now().getDayOfMonth());
        } else {
            jalaliDate = new JalaliDate(year, month, day);
        }
        return jalaliDate;
    }
}
