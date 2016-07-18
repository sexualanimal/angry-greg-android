package com.persilab.angrygregapp.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.Bind;
import butterknife.OnClick;

import com.persilab.angrygregapp.R;
import com.persilab.angrygregapp.domain.Constants;
import com.persilab.angrygregapp.domain.entity.User;
import com.persilab.angrygregapp.net.RestClient;
import com.persilab.angrygregapp.util.GuiUtils;
import com.persilab.angrygregapp.view.watcher.TextChangedWatcher;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 0shad on 04.03.2016.
 */
public class ProfileFragment extends BaseFragment {

    @Bind(R.id.profile_image)
    ImageView image;
    @Bind(R.id.profile_birthdate)
    TextView birthdate;
    DatePickerDialog datePickerDialog;
    @Bind(R.id.profile_name)
    EditText name;
    @Bind(R.id.profile_phone)
    EditText phone;
    @Bind(R.id.profile_save)
    Button save;
    @Bind(R.id.profile_save_user)
    CardView saveUser;

    private User user;

    public static ProfileFragment show(BaseFragment fragment, User user) {
        return show(fragment, ProfileFragment.class, Constants.ArgsName.USER, user);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        bind(rootView);
        getActivity().setTitle(R.string.profile);
        user = (User) getArguments().getSerializable(Constants.ArgsName.USER);
        putInfo();
        saveUser.setVisibility(View.GONE);
        name.addTextChangedListener(new TextChangedWatcher() {
                                        @Override
                                        public void textChanged(Editable s) {
                                            user.setName(s.toString());
                                            saveUser.setVisibility(View.VISIBLE);
                                        }
                                    });
        phone.addTextChangedListener(new TextChangedWatcher() {
                                         @Override
                                         public void textChanged(Editable s) {
                                             user.setPhone(s.toString());
                                             saveUser.setVisibility(View.VISIBLE);
                                         }
                                     });
        birthdate.setOnClickListener(v -> datePickerDialog.show());
        return rootView;
    }

    public void putInfo() {
        Map<Pair<Integer, String>, List<String>> itemsList = new LinkedHashMap<>();
        Calendar calendar = Calendar.getInstance();
        if (user != null) {
            phone.setText(user.getPhone());
            name.setText(user.getName());
            if (user.getBirthday() != null) {
                calendar.setTime(user.getBirthday());
            }
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if(user.getBirthday() != null) {
            GuiUtils.setText(birthdate, R.string.profile_birthdate_pattern, day, month + 1, year);
        } else {
            GuiUtils.setText(birthdate, "");
        }
        datePickerDialog = new DatePickerDialog(getContext(),
                (view, newYear, newMonth, newDay) -> {
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.YEAR, newYear);
                    cal.set(Calendar.MONTH, newMonth);
                    cal.set(Calendar.DAY_OF_MONTH, newDay);
                    user.setBirthday(cal.getTime());
                    saveUser.setVisibility(View.VISIBLE);
                    GuiUtils.setText(birthdate, R.string.profile_birthdate_pattern, newDay, newMonth + 1, newYear);
                }, year, month, day);
    }

    @OnClick(R.id.profile_save)
    public void onClick() {
        if(user.getBirthday() != null) {
            String date = new SimpleDateFormat(Constants.Pattern.DATA_ISO_8601_24H_FULL_FORMAT).format(user.getBirthday());
            RestClient.serviceApi().changeAccount(user.getId(), user.getName(), user.getPhone(), date).enqueue();
        } else {
            RestClient.serviceApi().changeAccount(user.getId(), user.getName(), user.getPhone(), null).enqueue();
        }

        saveUser.setVisibility(View.GONE);
    }

}

