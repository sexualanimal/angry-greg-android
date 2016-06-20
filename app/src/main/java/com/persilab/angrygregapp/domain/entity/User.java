package com.persilab.angrygregapp.domain.entity;

import android.os.SystemClock;
import com.persilab.angrygregapp.domain.Findable;

import com.persilab.angrygregapp.fragments.ListFragment;
import com.persilab.angrygregapp.util.SystemUtils;
import io.requery.Entity;
import io.requery.Key;
import lombok.Data;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by 0shad on 20.06.2016.
 */
@Entity
@Data
public class User implements Serializable, Findable {

    private static long point = System.currentTimeMillis();

    @Key
    String id;
    String name;
    String phone;
    Date birthDate;
    Integer rate;

    public User() {
        SystemClock.sleep(100);
        name = "Ivanov Ivan " + (System.currentTimeMillis() - point);
        phone = "+79998883232";
        Calendar calendar = Calendar.getInstance();
        calendar.roll(Calendar.YEAR, -18);
        birthDate = calendar.getTime();
        rate = new Random(point).nextInt(11);
    }

    @Override
    public boolean find(Object query) {
        if(query instanceof ListFragment.FilterEvent) {
            ListFragment.FilterEvent event = (ListFragment.FilterEvent) query;
            return name.toLowerCase().contains(event.query.toString().toLowerCase());
        }
        return false;
    }
}
