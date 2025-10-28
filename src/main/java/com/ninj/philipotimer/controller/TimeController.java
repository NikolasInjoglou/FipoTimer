package com.ninj.philipotimer.controller;


import com.ninj.philipotimer.model.TimeStatus;
import com.ninj.philipotimer.service.TimeService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/time")
@CrossOrigin
public class TimeController {

    private final TimeService service;
    public TimeController(TimeService service) { this.service = service; }

    @GetMapping("/status")
    public TimeStatus getStatus() { return service.getStatus(); }

    @GetMapping("/config")
    public Map<String, Long> getConfig() {
        return Map.of("totalGame", service.getTotalGame(), "totalCartoon", service.getTotalCartoon());
    }

    @PostMapping("/update")
    public void updateTime(@RequestParam String type, @RequestParam long seconds) {
        if ("game".equalsIgnoreCase(type)) service.addGameTime(seconds);
        if ("cartoon".equalsIgnoreCase(type)) service.addCartoonTime(seconds);
    }

    @PostMapping("/start")
    public void startTimer(@RequestParam String type) { service.start(type); }

    @PostMapping("/stop")
    public void stopTimer(@RequestParam String type) { service.stop(type); }
}