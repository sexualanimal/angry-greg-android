package com.persilab.angrygregapp.domain.entity;

import com.persilab.angrygregapp.domain.Validatable;
import com.persilab.angrygregapp.domain.entity.json.JsonEntity;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by 0shad on 20.06.2016.
 */

public class Token extends JsonEntity implements Serializable, Validatable {

    private String accessToken;
    private String refreshToken;
    private Date accessExpires;
    private Date refreshExpires;
    private User account;

    @Override
    public boolean isValid() {
        return accessToken != null && refreshToken != null && !isAccessExpired() && !isRefreshExpired();
    }

    public boolean isAccessExpired() {
        return accessExpires == null || accessExpires.getTime() - currentTime() - 5000 < 0;
    }

    public boolean isRefreshExpired() {
        return refreshExpires == null || refreshExpires.getTime() - currentTime() - 5000 < 0;
    }

    private static long currentTime() {
        Calendar time = Calendar.getInstance();
        time.add(Calendar.MILLISECOND, -time.getTimeZone().getOffset(time.getTimeInMillis()));
        return time.getTime().getTime();
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Date getAccessExpires() {
        return accessExpires;
    }

    public void setAccessExpires(Date accessExpires) {
        this.accessExpires = accessExpires;
    }

    public Date getRefreshExpires() {
        return refreshExpires;
    }

    public void setRefreshExpires(Date refreshExpires) {
        this.refreshExpires = refreshExpires;
    }

    public User getAccount() {
        return account;
    }

    public void setAccount(User account) {
        this.account = account;
    }
}
