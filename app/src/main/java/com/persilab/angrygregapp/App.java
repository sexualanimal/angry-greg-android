package com.persilab.angrygregapp;

import android.app.Application;

import com.persilab.angrygregapp.domain.Constants;
import com.persilab.angrygregapp.domain.entity.Token;

import io.requery.Persistable;
import io.requery.rx.SingleEntityStore;
import io.requery.sql.EntityDataStore;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Rufim on 03.07.2015.
 */
public class App extends Application {

    private static App singleton;
    private static Token actualToken;

    public static App getInstance() {
        return singleton;
    }

    private SingleEntityStore<Persistable> rxDataStore;
    private EntityDataStore<Persistable> dataStore;

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(Constants.Assets.ROBOTO_FONT_PATH)
                .setFontAttrId(R.attr.fontPath)
                .build());
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    public static Token getActualToken() {
        return actualToken;
    }

    public static void setActualToken(Token actualToken) {
        App.actualToken = actualToken;
    }
}
