package com.persilab.angrygregapp.domain.event;

/**
 * Created by kozak on 07.02.2017.
 */

public class SendTimerEvent implements Event {
    public int timerCount;
    public boolean isWait;

    public SendTimerEvent(int timerCount, boolean isWait) {
        this.timerCount = timerCount;
        this.isWait = isWait;
    }
}
