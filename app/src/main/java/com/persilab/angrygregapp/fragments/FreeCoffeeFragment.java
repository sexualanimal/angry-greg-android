package com.persilab.angrygregapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.persilab.angrygregapp.R;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by 0shad on 23.06.2016.
 */
public class FreeCoffeeFragment extends BaseFragment {

    protected static FreeCoffeeFragment show(BaseFragment fragment) {
        return show(fragment, FreeCoffeeFragment.class);
    }

    @Bind(R.id.free_coffee_ok)
    TextView gotIt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_free_coffee, container, false);
        bind(rootView);
        return rootView;
    }

    @OnClick(R.id.free_coffee_ok)
    public void gotIt() {
        getFragmentManager().popBackStack();
    }
}
