package com.persilab.angrygregapp;

import android.app.Application;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.persilab.angrygregapp.database.BigDecimalConverter;
import com.persilab.angrygregapp.domain.Constants;
import com.persilab.angrygregapp.domain.entity.Models;
import io.fabric.sdk.android.Fabric;
import io.requery.Persistable;
import io.requery.android.DefaultMapping;
import io.requery.android.sqlite.DatabaseSource;
import io.requery.rx.RxSupport;
import io.requery.rx.SingleEntityStore;
import io.requery.sql.Configuration;
import io.requery.sql.EntityDataStore;
import io.requery.sql.TableCreationMode;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

import java.math.BigDecimal;

/**
 * Created by Rufim on 03.07.2015.
 */
public class App extends Application {

    private static App singleton;

    public static App getInstance() {
        return singleton;
    }

    private SingleEntityStore<Persistable> rxDataStore;
    private EntityDataStore<Persistable> dataStore;

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        Fabric.with(this, new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder()
                        .disabled(BuildConfig.DEBUG)
                        .build()).build(), new Crashlytics());
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(Constants.Assets.ROBOTO_FONT_PATH)
                .setFontAttrId(R.attr.fontPath)
                .build());
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }


    public EntityDataStore<Persistable> getDataStore() {
        if (dataStore == null) {
            // override onUpgrade to handle migrating to a new version
            DatabaseSource source = new DatabaseSource(this, Models.DEFAULT, 1);
            if (BuildConfig.DEBUG) {
                // use this in development mode to drop and recreate the tables on every upgrade
                source.setTableCreationMode(TableCreationMode.DROP_CREATE);
                source.setLoggingEnabled(true);
            }

            Configuration configuration = source.getConfiguration();
            ((DefaultMapping) configuration.getMapping()).addConverter(new BigDecimalConverter(), BigDecimal.class);
            dataStore = new EntityDataStore<Persistable>(configuration);

            rxDataStore = RxSupport.toReactiveStore(
                    new EntityDataStore<Persistable>(configuration));
        }
        return dataStore;
    }

    public SingleEntityStore<Persistable> getRxDataStore() {
        getDataStore();
        return rxDataStore;
    }
}
