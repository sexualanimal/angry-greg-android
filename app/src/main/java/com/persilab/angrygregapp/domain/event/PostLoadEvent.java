package com.persilab.angrygregapp.domain.event;

import com.persilab.angrygregapp.domain.entity.User;

import java.util.List;

/**
 * Created by kozak on 07.02.2017.
 */

public class PostLoadEvent implements Event {
    public List<User> userList;

    public PostLoadEvent(List<User> userList) {
        this.userList = userList;
    }
}
