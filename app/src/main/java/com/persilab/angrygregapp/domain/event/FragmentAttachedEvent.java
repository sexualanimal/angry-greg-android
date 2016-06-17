package com.persilab.angrygregapp.domain.event;

import android.support.v4.app.Fragment;

/**
 * Created by 0shad on 21.07.2015.
 */
public class FragmentAttachedEvent implements Event {

    public final Fragment fragment;

    public FragmentAttachedEvent(Fragment fragment) {
        this.fragment = fragment;
    }
}
