package com.ninj.philipotimer.service;

import com.ninj.philipotimer.model.Session;
import com.ninj.philipotimer.model.TimeStatus;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class TimeService {

    @Getter
    @Value("${time.game.total}")
    private long totalGame;

    @Getter
    @Value("${time.cartoon.total}")
    private long totalCartoon;

    private long usedGame = 0;
    private long usedCartoon = 0;

    private Long gameStart = null;
    private Long cartoonStart = null;
    private Long gameStop = null;
    private Long cartoonStop = null;
    private long lastGameDurationSec = 0;
    private long lastCartoonDurationSec = 0;
    private final List<Session> gameSessions = new ArrayList<>();
    private final List<Session> cartoonSessions = new ArrayList<>();


    @PostConstruct
    public void init() {
    }

    public synchronized void start(String type) {
        long now = System.currentTimeMillis();

        if ("game".equalsIgnoreCase(type) && gameStart == null && usedGame < totalGame) {
            updateRunningTimer("game");
            gameStart = now;
            gameStop = null;
            lastGameDurationSec = 0;
        }

        if ("cartoon".equalsIgnoreCase(type) && cartoonStart == null && usedCartoon < totalCartoon) {
            updateRunningTimer("cartoon");
            cartoonStart = now;
            cartoonStop = null;
            lastCartoonDurationSec = 0;
        }
    }

    public synchronized void stop(String type) {
        long now = System.currentTimeMillis();
        ZoneId zone = ZoneId.systemDefault();

        if ("game".equalsIgnoreCase(type) && gameStart != null) {
            long elapsed = (now - gameStart) / 1000;
            usedGame = Math.min(usedGame + elapsed, totalGame);
            lastGameDurationSec = elapsed;
            gameStop = now;
            gameSessions.add(new Session(
                    LocalDateTime.ofInstant(Instant.ofEpochMilli(gameStart), zone),
                    LocalDateTime.ofInstant(Instant.ofEpochMilli(gameStop), zone),
                    elapsed
            ));
            gameStart = null;
        }

        if ("cartoon".equalsIgnoreCase(type) && cartoonStart != null) {
            long elapsed = (now - cartoonStart) / 1000;
            usedCartoon = Math.min(usedCartoon + elapsed, totalCartoon);
            lastCartoonDurationSec = elapsed;
            cartoonStop = now;
            cartoonSessions.add(new Session(
                    LocalDateTime.ofInstant(Instant.ofEpochMilli(cartoonStart), zone),
                    LocalDateTime.ofInstant(Instant.ofEpochMilli(cartoonStop), zone),
                    elapsed
            ));
            cartoonStart = null;
        }
    }

    /** ------------------ FIXED TIMER UPDATES ------------------ **/
    private void updateRunningTimer(String type) {
        long now = System.currentTimeMillis();

        if ("game".equalsIgnoreCase(type) && gameStart != null) {
            long elapsed = (now - gameStart) / 1000;
            usedGame = Math.min(usedGame + elapsed, totalGame);
            gameStart = now;
            if (usedGame >= totalGame) gameStart = null;
        }

        if ("cartoon".equalsIgnoreCase(type) && cartoonStart != null) {
            long elapsed = (now - cartoonStart) / 1000;
            usedCartoon = Math.min(usedCartoon + elapsed, totalCartoon);
            cartoonStart = now;
            if (usedCartoon >= totalCartoon) cartoonStart = null;
        }
    }

    public synchronized TimeStatus getStatus() {
        updateRunningTimer("game");
        updateRunningTimer("cartoon");

        ZoneId zone = ZoneId.systemDefault();
        TimeStatus s = new TimeStatus();
        s.setUsedGame(usedGame);
        s.setTotalGame(totalGame);
        s.setUsedCartoon(usedCartoon);
        s.setTotalCartoon(totalCartoon);
        s.setGameRunning(gameStart != null);
        s.setCartoonRunning(cartoonStart != null);
        s.setLastGameDurationSec(lastGameDurationSec);
        s.setLastCartoonDurationSec(lastCartoonDurationSec);
        s.setGameSessions(new ArrayList<>(gameSessions));
        s.setCartoonSessions(new ArrayList<>(cartoonSessions));

        if (gameStart != null)
            s.setGameStartTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(gameStart), zone));
        if (cartoonStart != null)
            s.setCartoonStartTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(cartoonStart), zone));
        if (gameStop != null)
            s.setGameStopTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(gameStop), zone));
        if (cartoonStop != null)
            s.setCartoonStopTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(cartoonStop), zone));

        return s;
    }

    public synchronized void addGameTime(long seconds) {
        updateRunningTimers();
        usedGame = Math.min(usedGame + seconds, totalGame);
    }

    public synchronized void addCartoonTime(long seconds) {
        updateRunningTimers();
        usedCartoon = Math.min(usedCartoon + seconds, totalCartoon);
    }

    private void updateRunningTimers() {
        long now = System.currentTimeMillis();
        if (gameStart != null) {
            long elapsed = (now - gameStart) / 1000;
            usedGame = Math.min(usedGame + elapsed, totalGame);
            gameStart = now;
            if (usedGame >= totalGame) gameStart = null;
        }
        if (cartoonStart != null) {
            long elapsed = (now - cartoonStart) / 1000;
            usedCartoon = Math.min(usedCartoon + elapsed, totalCartoon);
            cartoonStart = now;
            if (usedCartoon >= totalCartoon) cartoonStart = null;
        }
    }

    @Scheduled(cron = "0 0 0 * * MON")
    public synchronized void resetWeekly() {
        usedGame = 0;
        usedCartoon = 0;
        gameStart = null;
        cartoonStart = null;
        gameStop = null;
        cartoonStop = null;
        lastGameDurationSec = 0;
        lastCartoonDurationSec = 0;
        gameSessions.clear();
        cartoonSessions.clear();
    }


}