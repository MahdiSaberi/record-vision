package com.data.controller;

import com.data.model.DeadlineStatus;
import com.data.model.Event;
import com.data.model.dto.EventUpdateDto;
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
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@CrossOrigin
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
        diaryService.checkDeadline();
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

    @PutMapping("/update")
    public ResponseEntity<Boolean> update(@RequestBody EventUpdateDto eventUpdateDto) {
        DeadlineStatus deadlineStatus = eventService.getById(eventUpdateDto.getId()).getDeadlineStatus();
        eventUpdateDto.setDeadlineStatus(deadlineStatus);
        Event event = new ModelMapper().map(eventUpdateDto,Event.class);
        return ResponseEntity.ok(eventService.update(event));
    }

    private void doCalculate(EventModel eventModel) {
        Map<String, Integer> calculatedMap = diaryService.calculate(eventModel.getId());
        eventModel.setGroupingNumber(calculatedMap.get("groupingNumber"));
        eventModel.setGroupingDay(calculatedMap.get("groupingDay"));
        eventModel.setDaysToEndGrouping(calculatedMap.get("daysToEndGrouping"));
        eventModel.setDaysToEndVision(calculatedMap.get("daysToEndVision"));
        eventModel.setGoneDays(calculatedMap.get("goneDays"));
    }
}
