package com.persilab.angrygregapp.domain.entity;

import com.persilab.angrygregapp.adapter.ItemListAdapter;
import com.persilab.angrygregapp.domain.Findable;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.requery.Key;

public class User implements Serializable, Findable {

    private static long point = System.currentTimeMillis();

    @Key
    int id = 0;
    String name;
    String phone;
    String password;
    String birthday;
    int points;
    int free_coffee;
    int is_admin;

    transient boolean delete = false;

    @Override
    public boolean find(ItemListAdapter.FilterEvent event) {
        return name.toLowerCase().contains(event.query.toLowerCase()) || phone.toLowerCase().contains(event.query.toLowerCase());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBirthday() {
        return birthday;
    }

    public Date getBirthdayDate() {
        if (this.birthday == null) {
            return null;
        }
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date result = new Date();
        try {
            result = format.parse(birthday);
        } catch (ParseException e) {
        }
        return result;
    }

    public void setBirthdayDate(Date birthday) {
        this.birthday = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(birthday);
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public Boolean getIs_admin() {
        return is_admin == 1;
    }

    public void setIs_admin(Boolean is_admin) {
        if (is_admin) {
            this.is_admin = 1;
        } else {
            this.is_admin = 0;
        }
    }

    public int getAmountOfFreeCoffe() {
        return free_coffee;
    }

    public void setAmountOfFreeCoffe(int free_coffee) {
        this.free_coffee = free_coffee;
    }

    public int getAmountOfPoints() {
        return points;
    }

    public void setAmountOfPoints(int points) {
        this.points = points;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

}
