package com.persilab.angrygregapp.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.persilab.angrygregapp.App;
import com.persilab.angrygregapp.R;
import com.persilab.angrygregapp.domain.Constants;
import com.persilab.angrygregapp.domain.entity.User;
import com.persilab.angrygregapp.net.RestClient;
import com.persilab.angrygregapp.util.GuiUtils;
import com.persilab.angrygregapp.view.watcher.TextChangedWatcher;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;

import static com.persilab.angrygregapp.domain.Constants.Pattern.PHONE_CHECK_REGEX;

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
    @Bind(R.id.profile_new_user_fields)
    LinearLayout layoutNewUser;
    @Bind(R.id.profile_birthdate_layout)
    LinearLayout layoutBirthdate;
    @Bind(R.id.profile_is_admin)
    Switch switchIsAdmin;

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
        if (!(user.getId() > 0)) {
            user.setName(null);
            user.setPassword(null);
            user.setIs_admin(false);
            getActivity().setTitle(R.string.profile_create);
            layoutNewUser.setVisibility(View.VISIBLE);
        } else {
            switchIsAdmin.setChecked(user.getIs_admin());
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
        switchIsAdmin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                user.setIs_admin(isChecked);
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
                calendar.setTime(user.getBirthdayDate());
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
                    user.setBirthdayDate(cal.getTime());
                    GuiUtils.setText(birthdate, R.string.profile_birthdate_pattern, newDay, newMonth + 1, newYear);
                }, year, month, day);
    }

    public void save() {
        Pattern patternCheck = Pattern.compile(PHONE_CHECK_REGEX);
        Matcher matcherCheck = patternCheck.matcher(phone.getText());
        if (matcherCheck.matches()) {
            int isAdmin;
            if (user.getIs_admin()) {
                isAdmin = 1;
            } else {
                isAdmin = 0;
            }
            if (user.getId() > 0) {
                RestClient.serviceApi().changeAccount(App.getActualToken().getAccessToken(), user.getId(), user.getName(), user.getPhone(), user.getBirthday(), null, isAdmin, user.getPassword()).enqueue();
            } else {
                RestClient.serviceApi().createAccount(App.getActualToken().getAccessToken(), user.getName(), user.getPhone(), user.getBirthday(), null, isAdmin, user.getPassword()).enqueue();
            }
        } else {
            Toast.makeText(getActivity(), R.string.login_phone_error, Toast.LENGTH_SHORT).show();
        }
    }
}

