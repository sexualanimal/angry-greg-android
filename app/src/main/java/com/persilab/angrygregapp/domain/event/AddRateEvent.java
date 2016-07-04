package com.persilab.angrygregapp.domain.event;

import com.persilab.angrygregapp.domain.entity.User;

/**
 * Created by 0shad on 28.06.2016.
 */
public class AddRateEvent extends ResponseEvent<User> {
    public AddRateEvent(Status status, User response) {
        super(status, response);
    }
}
