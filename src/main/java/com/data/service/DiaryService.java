package com.data.service;

import com.data.model.DeadlineStatus;
import com.data.model.Event;
import com.data.model.JalaliDateModel;
import com.data.model.Vision;
import com.data.model.dto.EventDto;
import com.data.model.dto.VisionDto;
import com.data.util.JalaliDateUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.eloyzone.jalalicalendar.JalaliDate;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
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
        JalaliDate nowDate = JalaliDateUtil.convertLocalToJalali(LocalDate.now());
        Integer goneDays = subtractionDates(startDate, nowDate);
        if(grouping != 0) {
            int numberOfGrouping = (goneDays / grouping) + 1;
            int dayOfGrouping = goneDays % grouping;

            int daysToEndOfGrouping = grouping - dayOfGrouping;
            int remainingDays = calculateRemainingDays(vision.targetJalaliDate());
            return new Integer[]{numberOfGrouping, dayOfGrouping, daysToEndOfGrouping, remainingDays};
        }else {
            int numberOfGrouping = 1;
            int dayOfGrouping = 0;
            int daysToEndOfGrouping = 0;
            int remainingDays = calculateRemainingDays(vision.targetJalaliDate());
            return new Integer[]{numberOfGrouping, dayOfGrouping, daysToEndOfGrouping, remainingDays};
        }
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

    @Scheduled(fixedRate = 60000)
    public void checkDeadline() {
        try {
            log.info("Checking Deadlines");
            List<EventDto> events = eventService.getAll();
            List<Event> newList = new ArrayList<>();
            for (EventDto event : events) {
                JalaliDateModel primerDate = JalaliDateUtil.getModel(event.primerJalaliDate());
                JalaliDateModel nowDate = JalaliDateUtil.getModel(JalaliDateUtil.convertLocalToJalali(LocalDate.now()));
                JalaliDateModel targetDate = JalaliDateUtil.getModel(event.getVision().targetJalaliDate());
                if (event.getDeadlineStatus() == DeadlineStatus.UNFINISHED) {
                    if ((nowDate.getDay() >= targetDate.getDay()
                            && nowDate.getMonth() >= targetDate.getMonth()
                            && nowDate.getYear() >= targetDate.getYear()) ||
                            (targetDate.getDay() <= primerDate.getDay()
                                    && targetDate.getMonth() <= primerDate.getMonth()
                                    && targetDate.getYear() <= primerDate.getYear())) {

                        VisionDto visionDto = event.getVision();
                        Vision vision = new Vision(visionDto.getDay(), visionDto.getMonth(), visionDto.getYear(), visionDto.getGrouping());
                        newList.add(new Event(event.getId(),
                                event.getDay(),
                                event.getMonth(),
                                event.getYear(),
                                vision,
                                DeadlineStatus.ARRIVED));
                    } else {
                        ModelMapper modelMapper = new ModelMapper();
                        Event unfinishedEvent = modelMapper.map(event, Event.class);
                        newList.add(unfinishedEvent);
                    }
                } else {
                    ModelMapper modelMapper = new ModelMapper();
                    Event arrivedEvent = modelMapper.map(event, Event.class);
                    newList.add(arrivedEvent);
                }
            }
            reloadDataStorage(newList);
        } catch (Exception exception) {
            log.info("Database is busy; It's okay =)");
        }
    }

    private boolean reloadDataStorage(List<Event> events) throws IOException {
        File tempFile = new File("temp_viewDatabase.json");
        File dbFile = eventService.getDbDirectory().toFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
        for (Event event : events) {
            String json = new ObjectMapper().writeValueAsString(event);
            writer.write(json);
            writer.newLine();
        }
        writer.close();
        Files.deleteIfExists(eventService.getDbDirectory());
        return tempFile.renameTo(dbFile);
    }
}
