package com.persilab.angrygregapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.persilab.angrygregapp.R;
import com.persilab.angrygregapp.dialog.AddPointsDialog;
import com.persilab.angrygregapp.domain.Constants;
import com.persilab.angrygregapp.domain.entity.User;
import com.persilab.angrygregapp.domain.event.AddRateEvent;
import com.persilab.angrygregapp.util.GuiUtils;
import com.whinc.widget.ratingbar.RatingBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by 0shad on 21.06.2016.
 */
public class AddPointsUserFragment extends BaseFragment {

    @Bind(R.id.edit_user_add)
    TextView editUserAdd;

    public static AddPointsUserFragment show(BaseFragment baseFragment, User user) {
        return show(baseFragment, AddPointsUserFragment.class, Constants.ArgsName.USER, user);
    }

    @Bind(R.id.edit_user_rating)
    RatingBar ratingBar;
    @Bind(R.id.edit_user_tick)
    ImageView tick;
    @Bind(R.id.points_name)
    TextView uName;
    @Bind(R.id.points_phone)
    TextView uPhone;
    @Bind(R.id.edit_user_remove_coffee)
    TextView removeCoffee;

    User user;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.addpoints, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.addpoints_edit) {
            ProfileFragment.show(AddPointsUserFragment.this, user);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_points_user, container, false);
        bind(rootView);
        if (user == null) {
            user = (User) getArguments().getSerializable(Constants.ArgsName.USER);
        }
        getActivity().setTitle("");
        setHasOptionsMenu(true);
        updateUi(user);
        removeCoffee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPointsDialog.show(getFragmentManager(), user, true);
            }
        });
        return rootView;
    }

    @Override
    public boolean allowBackPress() {
        getMainActivity().replaceFragment(UserListFragment.class);
        return false;
    }

    @Subscribe
    public void onEvent(AddRateEvent event) {
        user = event.message.getAccount();
        GuiUtils.runInUI(getContext(), var -> updateUi(user));
    }

    private void updateUi(User user) {
        if (user != null) {
            ratingBar.setCount(user.getAmountOfPoints());
            uName.setText(user.getName());
            uPhone.setText(user.getPhone());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @OnClick(R.id.edit_user_add)
    public void onClick() {
        AddPointsDialog.show(getFragmentManager(), user, false);
    }
}
