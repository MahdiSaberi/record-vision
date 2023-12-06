package com.data.service;

import com.data.model.Event;
import com.data.model.JalaliDateModel;
import com.data.model.Vision;
import com.data.util.JalaliDateUtil;
import com.github.eloyzone.jalalicalendar.JalaliDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;

@Service
public class DiaryService {

    private final EventService eventService;

    @Autowired
    public DiaryService(EventService eventService) {
        this.eventService = eventService;
    }

    public Integer[] calculateGrouping(Integer eventId) throws IOException {
        Event event = eventService.getById(eventId);
        Vision vision = event.getVision();
        Integer grouping = vision.getGrouping();
        JalaliDate startDate = event.primerJalaliDate();
        JalaliDate stopDate = JalaliDateUtil.convertLocalToJalali(LocalDate.now());
        Integer goneDays = subtractionDates(startDate, stopDate);
        int numberOfGrouping = (goneDays / grouping) + 1;
        int dayOfGrouping = goneDays % grouping;
        int daysToEndOfGrouping = grouping - dayOfGrouping;
        int remainingDays = calculateRemainingDays(vision.targetJalaliDate());
        return new Integer[]{numberOfGrouping, dayOfGrouping, daysToEndOfGrouping, remainingDays};
    }

    public int calculateRemainingDays(JalaliDate targetDate) {
        return subtractionDates(JalaliDateUtil.convertLocalToJalali(LocalDate.now()), targetDate);
    }

    public long calculateGoneDays(String firstDate, String secondDate) {
        JalaliDate first = JalaliDateUtil.convertStringToJalali(firstDate);
        JalaliDate second = JalaliDateUtil.convertStringToJalali(secondDate);
        return subtractionDates(first, second);
    }

    public String getStringNowDate() {
        return JalaliDateUtil.convertJalaliToString(getJalaliNowDate());
    }

    public JalaliDate getJalaliNowDate() {
        return JalaliDateUtil.convertLocalToJalali(LocalDate.now());
    }

    private Integer subtractionDates(JalaliDate startJalaliDate, JalaliDate secondJalaliDate) {
        JalaliDateModel startJalaliDateModel = JalaliDateUtil.getModel(startJalaliDate);
        JalaliDateModel stopJalaliDateModel = JalaliDateUtil.getModel(secondJalaliDate);
        //iterate
        int startDay = startJalaliDateModel.getDay();
        int startMonth = startJalaliDateModel.getMonth();
        int startYear = startJalaliDateModel.getYear();

        int stopDay = stopJalaliDateModel.getDay();
        int stopMonth = stopJalaliDateModel.getMonth();
        int stopYear = stopJalaliDateModel.getYear();
        int goneDays = 0;
        do {
            startDay++;
            goneDays++;
            if ((startDay == 32 && (startMonth == 1 || startMonth == 2 || startMonth == 3 || startMonth == 4 || startMonth == 5 || startMonth == 6))
                    || startDay == 31 && (startMonth == 7 || startMonth == 8 || startMonth == 9 || startMonth == 10 || startMonth == 11 ||
                    (startMonth == 12 && new JalaliDate(startYear, 1, 1).isLeapYear()))) {
                startDay = 1;
                if (startMonth != 12) {
                    startMonth++;
                } else {
                    startMonth = 1;
                    startYear++;
                }
            } else if (startDay == 30 && (startMonth == 12 && !new JalaliDate(startYear, 1, 1).isLeapYear())) {
                startDay = 1;
                startMonth = 1;
                startYear++;
            }
        } while (startDay != stopDay || startMonth != stopMonth || startYear != stopYear);
        return goneDays;
    }
}