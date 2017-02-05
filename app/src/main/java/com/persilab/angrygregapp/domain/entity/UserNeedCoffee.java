package com.persilab.angrygregapp.domain.entity;

import com.persilab.angrygregapp.domain.entity.json.JsonEntity;

import java.io.Serializable;

/**
 * Created by 0shad on 20.06.2016.
 */

public class UserNeedCoffee extends JsonEntity implements Serializable {
    private int needFreeCoffee;
    private User account;

    public int getNeedFreeCoffee() {
        return needFreeCoffee;
    }

    public void setNeedFreeCoffee(int needFreeCoffee) {
        this.needFreeCoffee = needFreeCoffee;
    }

    public User getAccount() {
        return account;
    }

    public void setAccount(User account) {
        this.account = account;
    }
}
