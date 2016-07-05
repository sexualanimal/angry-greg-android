package com.persilab.angrygregapp.domain.event;

import com.persilab.angrygregapp.domain.entity.User;
import okhttp3.Request;

/**
 * Created by Dmitry on 05.07.2016.
 */
public class UserDeletedEvent extends  ResponseEvent<String> {
    public UserDeletedEvent(Status status, String response) {
        super(status, response);
    }
}
