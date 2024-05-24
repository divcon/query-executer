package com.samsungcloud;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "time")
public class TimeConfig {
    private String startTime;
    private long minuteInterval;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public long getMinuteInterval() {
        return minuteInterval;
    }

    public void setMinuteInterval(long minuteInterval) {
        this.minuteInterval = minuteInterval;
    }
}
