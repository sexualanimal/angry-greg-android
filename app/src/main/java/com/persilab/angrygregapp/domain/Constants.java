package com.persilab.angrygregapp.domain;

import com.persilab.angrygregapp.BuildConfig;

/**
 * Created by Rufim on 07.01.2015.
 */
public class Constants {

    public static class Assets {
        public static final String ROBOTO_FONT_PATH = "fonts/roboto/Roboto-Regular.ttf";
        public static final String DROID_SANS_FONT_PATH = "fonts/droidsans/DroidSans.ttf";
        public static final String ROBOTO_Capture_it = "fonts/Capture-it/Capture_it.ttf";
    }

    public static class ArgsName {
        public static final String LAST_FRAGMENT_TAG = "last_fragment_tag";
        public static final String LAST_FRAGMENT = "last_fragment";
        public static final String FRAGMENT_CLASS = "fragment_class";
        public static final String FRAGMENT_ARGS = "fragment_args";
        public static final String INDEX = "index";
        public static final String LOGOUT = "logout";
        public static final String MESSAGE = "message";
        public static final String USER = "user";
        public static final String BUNDLE = "bundle";
        public static final String TIMER = "timer";
        public static final String IS_WAIT = "is_wait";
        public static final String USER_ID = "user_id";
        public static final String RESOURCE_ID = "resource_id";
        public static final String CONFIG_CHANGE = "config_change";
    }

    public static class Net {
        public static final String BASE_SCHEME = BuildConfig.BASE_SCHEME;
        public static final String RESET_TOKEN = "RESET_TOKEN";
        public static final String BASE_HOST = BuildConfig.BASE_HOST;
        public static final String BASE_DOMAIN = BASE_SCHEME + "://" + BASE_HOST + "/";
        public static final String USER_AGENT = "Mozilla";
    }

    public static class Cache {
        public static final String CACHE_NAME = "html_cache";
        public static final String CACHE_ASYNC = "async_html_cache";
        public static final int RAM_MAX_SIZE = 1024 * 1024 * 20;
        public static final int DISK_MAX_SIZE = 1024 * 1024 * 50;
    }

    public static class App {
        public static final int VERSION = BuildConfig.VERSION_CODE;
        public static final String VERSION_NAME = BuildConfig.VERSION_NAME;
        public static final String DATABASE_NAME = BuildConfig.APPLICATION_ID + ".db";
        public static final int DATABASE_VERSION = 1;
    }

    public static class Pattern {
        public static final String TIME_PATTERN = "HH:mm";
        public static final String DATA_PATTERN = "dd-MM-yyyy";
        public static final String REVERSE_DATA_PATTERN = "MM-dd-yyyy";
        public static final String DATA_TIME_PATTERN = "dd-MM-yyyy HH:mm";
        public static final String DATA_ISO_8601_24H_FULL_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        public static final String DATA_ISO_8601_SHORT_FORMAT = "yyyy-MM-dd";
        public static final String PHONE_CHECK_REGEX = "^(\\+7|8)?\\d{10}$";
        public static final String PHONE_SEND_REGEX = "(?<=(\\+7|8|^))\\d{10}$";
    }

}
