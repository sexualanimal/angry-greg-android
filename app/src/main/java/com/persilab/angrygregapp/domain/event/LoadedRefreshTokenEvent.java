package com.persilab.angrygregapp.domain.event;

import com.persilab.angrygregapp.domain.entity.Token;

/**
 * Created by kozak on 07.02.2017.
 */

public class LoadedRefreshTokenEvent implements Event {
    public Token token;

    public LoadedRefreshTokenEvent(Token token) {
        this.token = token;
    }
}
