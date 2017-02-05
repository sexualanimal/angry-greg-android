package com.persilab.angrygregapp.domain.entity;

import com.persilab.angrygregapp.domain.Validatable;
import com.persilab.angrygregapp.domain.entity.json.JsonEntity;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 0shad on 20.06.2016.
 */

public class Token extends JsonEntity implements Serializable, Validatable {

    private String accessToken;
    private String refreshToken;
    private String accessExpires;
    private String refreshExpires;
    private User account;

    @Override
    public boolean isValid() {
        return accessToken != null && refreshToken != null && !isAccessExpired() && !isRefreshExpired();
    }

    public boolean isAccessExpired() {
        return accessExpires == null || Integer.parseInt(accessExpires) - currentTime() - 5000 < 0;
    }

    public boolean isRefreshExpired() {
        return refreshExpires == null || Integer.parseInt(refreshExpires) - currentTime() - 5000 < 0;
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
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date result = new Date();
        try {
            result = format.parse(this.accessExpires);
        } catch (ParseException e) {
        }
        return result;
    }

    public void setAccessExpires(Date accessExpires) {
        this.accessExpires = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(accessExpires);
    }

    public Date getRefreshExpires() {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date result = new Date();
        try {
            result = format.parse(this.refreshExpires);
        } catch (ParseException e) {
        }
        return result;
    }

    public void setRefreshExpires(Date refreshExpires) {
        this.refreshExpires = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(refreshExpires);
    }

    public User getAccount() {
        return account;
    }

    public void setAccount(User account) {
        this.account = account;
    }
}
