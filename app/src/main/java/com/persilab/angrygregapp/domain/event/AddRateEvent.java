package com.persilab.angrygregapp.domain.event;

import com.persilab.angrygregapp.domain.entity.User;
import com.persilab.angrygregapp.domain.entity.UserNeedCoffee;

/**
 * Created by 0shad on 28.06.2016.
 */
public class AddRateEvent extends ResponseEvent<UserNeedCoffee> {
    public AddRateEvent(Status status, UserNeedCoffee response) {
        super(status, response);
    }
}
