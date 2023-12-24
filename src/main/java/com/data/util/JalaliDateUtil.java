package com.data.util;

import com.data.model.JalaliDateModel;
import com.github.eloyzone.jalalicalendar.DateConverter;
import com.github.eloyzone.jalalicalendar.JalaliDate;
import com.github.eloyzone.jalalicalendar.JalaliDateFormatter;

import java.time.LocalDate;

public abstract class JalaliDateUtil {

    public static String convertJalaliToString(JalaliDate jalaliDate) {
        return jalaliDate.format(getStandardDateFormat());
    }

    public static JalaliDate convertStringToJalali(String stringJalaliDate) {
        String[] jalaliArray = stringJalaliDate.split(" ");
        int year = Integer.parseInt(jalaliArray[2]);
        int month = Integer.parseInt(jalaliArray[1]);
        int day = Integer.parseInt(jalaliArray[0]);
        return new JalaliDate(year, month, day);
    }

    public static JalaliDate convertLocalToJalali(LocalDate localDate) {
        DateConverter dateConverter = new DateConverter();
        return dateConverter.gregorianToJalali(localDate.getYear(), localDate.getMonth(), localDate.getDayOfMonth());
    }

    private static JalaliDateFormatter getStandardDateFormat() {
        return new JalaliDateFormatter("dd mm yyyy");
    }

    public static JalaliDateModel getModel(JalaliDate jalaliDate) {
        return getModel(convertJalaliToString(jalaliDate));
    }

    public static JalaliDateModel getModel(String stringJalaliDate) {
        String[] jalaliArray = stringJalaliDate.split(" ");
        int year = Integer.parseInt(jalaliArray[2]);
        int month = Integer.parseInt(jalaliArray[1]);
        int day = Integer.parseInt(jalaliArray[0]);
        return new JalaliDateModel(year, month, day);
    }

    public static JalaliDateModel now() {
        return getModel(convertLocalToJalali(LocalDate.now()));
    }
}
