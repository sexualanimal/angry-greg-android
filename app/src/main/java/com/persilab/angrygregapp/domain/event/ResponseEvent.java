package com.persilab.angrygregapp.domain.event;

import okhttp3.Request;

/**
 * Created by 0shad on 01.03.2016.
 */
public abstract class ResponseEvent<R> extends MessageEvent<R> implements Event {

    public final Status status;

    public enum Status {
        FAILURE, SUCCESS
    }

    public ResponseEvent(Status status, R response) {
        super(response);
        this.status = status;
    }

}
