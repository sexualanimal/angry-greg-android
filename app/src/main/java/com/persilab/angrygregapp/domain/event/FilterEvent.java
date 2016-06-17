package com.persilab.angrygregapp.domain.event;

/**
 * Created by 0shad on 25.10.2015.
 */
public class FilterEvent implements Event {

    public final boolean excluding;
    public String query;


    public FilterEvent(String query) {
        this.query = query;
        this.excluding = false;
    }
}
