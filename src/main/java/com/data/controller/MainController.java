package com.data.controller;

import com.data.model.Event;
import com.data.model.StatusMode;
import com.data.model.dto.EventDto;
import com.data.service.DiaryService;
import com.data.service.EventService;
import com.data.util.JalaliDateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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

    @PostMapping("/remove")
    public ResponseEntity<Boolean> removeEvent(@RequestParam Integer id) throws IOException {
        return ResponseEntity.ok(eventService.removeById(id));
    }

    @GetMapping("/getEvents")
    public ResponseEntity<List<EventDto>> getEvents(@RequestParam StatusMode status) {
        if (status == StatusMode.ALL) {
            return ResponseEntity.ok(eventService.getAll());
        } else {
            return ResponseEntity.ok(eventService.getAll().stream().filter(event -> event.getDeadlineStatus().name().equals(status.name())).toList());
        }
    }

    @GetMapping("/goneDays")
    public ResponseEntity<Long> getGoneDays(@RequestParam String firstDate, @RequestParam String secondDate) {
        return ResponseEntity.ok(diaryService.calculateGoneDays(firstDate, secondDate));
    }

    @GetMapping("/calculate")
    public ResponseEntity<Map<String, Integer>> calculate(@RequestParam Integer eventId) throws IOException {
        Map<String, Integer> calculatedMap = diaryService.calculateGrouping(eventId);
        return ResponseEntity.ok(calculatedMap);
    }
}
