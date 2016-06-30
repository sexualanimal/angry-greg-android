package com.persilab.angrygregapp.domain.event;

/**
 * Created by 0shad on 26.02.2016.
 */
public class NetworkEvent<R> extends ResponseEvent<R> {
    public NetworkEvent(Status status, R response) {
        super(status, response);
    }
}
