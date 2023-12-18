package com.data.controller;

import com.data.model.DeadlineStatus;
import com.data.model.Event;
import com.data.model.ui.EventModel;
import com.data.service.DiaryService;
import com.data.service.EventService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class MainController {

    private final DiaryService diaryService;
    private final EventService eventService;

    @PostMapping("/create")
    public ResponseEntity<Boolean> createEvent(@RequestBody Event event) {
        return ResponseEntity.ok(eventService.save(event));
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<Boolean> removeEvent(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(eventService.removeById(id));
    }

    @GetMapping("/getEvents")
    public ResponseEntity<List<EventModel>> getEvents(@RequestParam(name = "status") DeadlineStatus status) {
        List<EventModel> events = eventService.getAll();
        if (!status.name().equals(DeadlineStatus.ALL.name())) {
            events = events
                    .stream()
                    .filter(event -> event.getDeadlineStatus().name().equals(status.name()))
                    .toList();
        }
        events.forEach(
                this::doCalculate
        );
        return ResponseEntity.ok(events);
    }

    @GetMapping("/goneDays")
    @Hidden
    public ResponseEntity<Long> getGoneDays(@RequestParam String firstDate, @RequestParam String secondDate) {
        return ResponseEntity.ok(diaryService.calculateGoneDays(firstDate, secondDate));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<EventModel> getEventById(@PathVariable("id") Integer id) {
        EventModel eventModel = new ModelMapper().map(eventService.getById(id), EventModel.class);
        doCalculate(eventModel);
        return ResponseEntity.ok(eventModel);
    }

    private void doCalculate(EventModel eventModel) {
        eventModel.setGroupingNumber(diaryService.calculateByKey("groupingNumber", eventModel.getId()));
        eventModel.setGroupingDay(diaryService.calculateByKey("groupingDay", eventModel.getId()));
        eventModel.setDaysToEndGrouping(diaryService.calculateByKey("daysToEndGrouping", eventModel.getId()));
        eventModel.setDaysToEndVision(diaryService.calculateByKey("daysToEndVision", eventModel.getId()));
        eventModel.setGoneDays(diaryService.calculateByKey("goneDays", eventModel.getId()));
    }
}
