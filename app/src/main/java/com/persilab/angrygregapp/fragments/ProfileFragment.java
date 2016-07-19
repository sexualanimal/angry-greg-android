package com.persilab.angrygregapp.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.util.Pair;
import android.view.*;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import com.persilab.angrygregapp.R;
import com.persilab.angrygregapp.domain.Constants;
import com.persilab.angrygregapp.domain.entity.User;
import com.persilab.angrygregapp.net.RestClient;
import com.persilab.angrygregapp.util.GuiUtils;
import com.persilab.angrygregapp.util.TextUtils;
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
    @Bind(R.id.profile_password)
    EditText password;
    @Bind(R.id.profile_points)
    EditText profilePoints;
    @Bind(R.id.profile_new_user_fields)
    LinearLayout layoutNewUser;
    @Bind(R.id.profile_birthdate_layout)
    LinearLayout layoutBirthdate;

    private User user;

    public static ProfileFragment show(BaseFragment fragment, User user) {
        return show(fragment, ProfileFragment.class, Constants.ArgsName.USER, user);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.saveuser, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile_save:
                save();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        bind(rootView);
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.profile_edit);
        user = (User) getArguments().getSerializable(Constants.ArgsName.USER);
        if (user.getId() == null) {
            user.setName(null);
            getActivity().setTitle(R.string.profile_edit);
            layoutNewUser.setVisibility(View.VISIBLE);
        }
        putInfo();
        name.addTextChangedListener(new TextChangedWatcher() {
            @Override
            public void textChanged(Editable s) {
                user.setName(s.toString());
            }
        });
        phone.addTextChangedListener(new TextChangedWatcher() {
            @Override
            public void textChanged(Editable s) {
                user.setPhone(s.toString());
            }
        });
        password.addTextChangedListener(new TextChangedWatcher() {
            @Override
            public void textChanged(Editable s) {
                user.setPassword(s.toString());
            }
        });
        profilePoints.addTextChangedListener(new TextChangedWatcher() {
            @Override
            public void textChanged(Editable s) {
                user.setAmountOfPoints(TextUtils.extractInt(s.toString(), 0));
            }
        });
        layoutBirthdate.setOnClickListener(v -> datePickerDialog.show());
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
            } else {
                calendar.roll(Calendar.YEAR, -18);
            }
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if (user.getBirthday() != null) {
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
                    GuiUtils.setText(birthdate, R.string.profile_birthdate_pattern, newDay, newMonth + 1, newYear);
                }, year, month, day);
    }

    public void save() {
        if (user.getBirthday() != null) {
            String date = new SimpleDateFormat(Constants.Pattern.DATA_ISO_8601_24H_FULL_FORMAT).format(user.getBirthday());
            if(user.getId() != null) {
                RestClient.serviceApi().changeAccount(user.getId(), user.getName(), user.getPhone(), date).enqueue();
            } else {
                RestClient.serviceApi().createAccount(user.getName(), user.getPhone(), user.getPassword(), date).enqueue();
            }
        } else {
            if (user.getId() != null) {
                RestClient.serviceApi().changeAccount(user.getId(), user.getName(), user.getPhone(), null).enqueue();
            } else {
                RestClient.serviceApi().createAccount(user.getName(), user.getPhone(), user.getPassword(), null).enqueue();
            }
        }
    }
}

