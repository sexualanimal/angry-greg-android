package com.persilab.angrygregapp;

import android.app.Application;
import android.content.res.Configuration;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.persilab.angrygregapp.BuildConfig;
import com.persilab.angrygregapp.R;
import io.fabric.sdk.android.Fabric;
import com.persilab.angrygregapp.domain.Constants;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Rufim on 03.07.2015.
 */
public class App extends Application {

    private static App singleton;

    public static App getInstance() {
        return singleton;
    }

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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

}
