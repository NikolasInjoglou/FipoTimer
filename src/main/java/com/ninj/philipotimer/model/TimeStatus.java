package com.ninj.philipotimer.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TimeStatus {
    private long usedGame;
    private long totalGame;
    private long usedCartoon;
    private long totalCartoon;
    private boolean gameRunning;
    private boolean cartoonRunning;
    private LocalDateTime gameStartTime;
    private LocalDateTime cartoonStartTime;
    private LocalDateTime gameStopTime;
    private LocalDateTime cartoonStopTime;
    private long lastGameDurationSec;
    private long lastCartoonDurationSec;
    private List<Session> gameSessions;
    private List<Session> cartoonSessions;
}