package com.persilab.angrygregapp.domain.event;

/**
 * Created by 0shad on 28.06.2016.
 */
public class AddRateEvent implements Event {
    public final Integer rate;

    public AddRateEvent(Integer rate) {
        this.rate = rate;
    }
}
