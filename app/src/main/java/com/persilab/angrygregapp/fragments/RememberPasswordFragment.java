package com.persilab.angrygregapp.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.persilab.angrygregapp.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;

import static com.persilab.angrygregapp.domain.Constants.ArgsName.IS_WAIT;
import static com.persilab.angrygregapp.domain.Constants.ArgsName.PHONE;
import static com.persilab.angrygregapp.domain.Constants.ArgsName.TIMER;
import static com.persilab.angrygregapp.domain.Constants.Pattern.PHONE_CHECK_REGEX;

public class RememberPasswordFragment extends BaseFragment {

    private SharedPreferences prefs;
    @Bind(R.id.remember_send)
    TextView sendCode;
    @Bind(R.id.remember_enter)
    TextView submitCode;
    @Bind(R.id.remember_wait)
    TextView waitNewSms;
    @Bind(R.id.remember_password)
    EditText enterPassword;
    @Bind(R.id.remember_phone)
    EditText enterPhone;
    @Bind(R.id.remember_password_layout)
    LinearLayout passwordLayout;
    @Bind(R.id.remember_phone_layout)
    LinearLayout phoneLayout;
    CountDownTimer countDownTimer;
    String waitTxt1;
    String waitTxt2;
    boolean isWait;
    int timerCount;
    final Handler handler = new Handler();
    Pattern patternCheck = Pattern.compile(PHONE_CHECK_REGEX);
    Runnable timerRunnable;

    public static RememberPasswordFragment show(BaseFragment fragment) {
        return show(fragment, RememberPasswordFragment.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_remember_password, container, false);
        bind(rootView);


        prefs = getActivity().getPreferences(Context.MODE_PRIVATE);

        String phone = prefs.getString(PHONE, "");
        if (!phone.isEmpty()) {
            enterPhone.setText(phone);
        }

        waitTxt1 = getString(R.string.wait_code1);
        waitTxt2 = getString(R.string.wait_code2);

        sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Matcher matcherCheck = patternCheck.matcher(enterPhone.getText());
                if (matcherCheck.matches()) {
                    phoneLayout.setVisibility(View.GONE);
                    passwordLayout.setVisibility(View.VISIBLE);
                    isWait = true;
                    countDownTimer.start();
                } else {
                    Toast.makeText(getActivity(), R.string.login_phone_error, Toast.LENGTH_SHORT).show();
                }
            }
        });

        submitCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (enterPassword.getText().length() > 0) {
                    //add password request
                } else {
                    Toast.makeText(getActivity(), getString(R.string.empty_code), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        isWait = prefs.getBoolean(IS_WAIT, false);
        if (isWait) {
            timerCount = prefs.getInt(TIMER, 90);
        } else {
            timerCount = 90;
        }

        timerRunnable = new Runnable() {
            @Override
            public void run() {
                countDownTimer = new CountDownTimer((timerCount + 1) * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        timerCount--;
                        waitNewSms.setText(waitTxt1 + " " + Math.round(millisUntilFinished / 1000) + " " + waitTxt2);
                    }

                    @Override
                    public void onFinish() {
                        timerCount = 90;
                        isWait = false;
                        timerRunnable.run();
                        phoneLayout.setVisibility(View.VISIBLE);
                        passwordLayout.setVisibility(View.GONE);
                    }
                };
            }
        };
        timerRunnable.run();

        if (isWait) {
            phoneLayout.setVisibility(View.GONE);
            passwordLayout.setVisibility(View.VISIBLE);
            countDownTimer.start();
        }
    }

    @Override
    public void onStop() {
        countDownTimer.cancel();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(IS_WAIT, isWait);
        editor.putInt(TIMER, timerCount);
        editor.commit();
        super.onStop();
    }
}
