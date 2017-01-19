package com.persilab.angrygregapp.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.persilab.angrygregapp.R;

public class LogoFragment extends BaseFragment {

    private static final String PHONE = "phone";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_logo, container, false);
        bind(rootView);

        return rootView;
    }


}
