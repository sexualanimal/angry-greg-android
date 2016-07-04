package com.persilab.angrygregapp.domain.event;

import com.persilab.angrygregapp.domain.entity.User;

/**
 * Created by Dmitry on 04.07.2016.
 */
public class QRUserFoundEvent extends ResponseEvent<User>{
    public QRUserFoundEvent(Status status, User response) {
        super(status, response);
    }
}
