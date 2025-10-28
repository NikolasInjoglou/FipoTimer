package com.ninj.philipotimer.model;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Session {
    private final LocalDateTime start;
    private final LocalDateTime stop;
    private final long durationSec;

    public Session(LocalDateTime start, LocalDateTime stop, long durationSec) {
        this.start = start;
        this.stop = stop;
        this.durationSec = durationSec;
    }

}
