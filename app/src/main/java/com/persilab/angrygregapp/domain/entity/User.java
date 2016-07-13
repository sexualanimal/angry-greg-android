package com.persilab.angrygregapp.domain.entity;

import android.os.SystemClock;
import com.persilab.angrygregapp.adapter.ItemListAdapter;
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
    String name = "Anonymous";
    String phone;
    String password;
    Date birthday;
    Boolean is_admin = true;
    Integer amountOfFreeCoffe = 0;
    Integer amountOfPoints = 0;

    transient boolean delete = false;

    @Override
    public boolean find(ItemListAdapter.FilterEvent event) {
        return name.toLowerCase().contains(event.query.toLowerCase()) || phone.toLowerCase().contains(event.query.toLowerCase());
    }
}
