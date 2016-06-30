package com.persilab.angrygregapp.domain.event;


import com.persilab.angrygregapp.domain.entity.Token;

/**
 * Created by 0shad on 01.03.2016.
 */
public class TokenUpdateEvent extends ResponseEvent<Token> {
    public TokenUpdateEvent(Status status, Token response) {
        super(status, response);
    }
}
