package com.persilab.angrygregapp.fragments;


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
import com.persilab.angrygregapp.domain.event.SendTimerEvent;

import butterknife.Bind;

import static com.persilab.angrygregapp.domain.Constants.ArgsName.BUNDLE;
import static com.persilab.angrygregapp.domain.Constants.ArgsName.IS_WAIT;
import static com.persilab.angrygregapp.domain.Constants.ArgsName.TIMER;

public class RememberPasswordFragment extends BaseFragment {

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

    public static RememberPasswordFragment show(BaseFragment fragment, Bundle bundle) {
        return show(fragment, RememberPasswordFragment.class, BUNDLE, bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_remember_password, container, false);
        bind(rootView);

        isWait = getArguments().getBundle(BUNDLE).getBoolean(IS_WAIT);
        timerCount = getArguments().getBundle(BUNDLE).getInt(TIMER);

        waitTxt1 = getString(R.string.wait_code1);
        waitTxt2 = getString(R.string.wait_code2);

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
                phoneLayout.setVisibility(View.VISIBLE);
                passwordLayout.setVisibility(View.GONE);
            }
        };

        sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (enterPhone.getText().length() > 0) {
                    phoneLayout.setVisibility(View.GONE);
                    passwordLayout.setVisibility(View.VISIBLE);
                    isWait = true;
                    countDownTimer.start();
                } else {
                    Toast.makeText(getActivity(), R.string.login_phone_error, Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (isWait) {
            phoneLayout.setVisibility(View.GONE);
            passwordLayout.setVisibility(View.VISIBLE);
            countDownTimer.start();
        }

        submitCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (enterPassword.getText().length()>0) {
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
//        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        countDownTimer.cancel();
        handler.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        postEvent(new SendTimerEvent(timerCount, isWait));
                    }
                }, 500);
//        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
