package com.persilab.angrygregapp.fragments;

import android.os.Bundle;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.persilab.angrygregapp.R;
import com.persilab.angrygregapp.dialog.PickerDialog;
import com.persilab.angrygregapp.domain.Constants;
import com.persilab.angrygregapp.domain.entity.User;
import com.persilab.angrygregapp.domain.event.AddRateEvent;
import com.persilab.angrygregapp.util.GuiUtils;
import com.whinc.widget.ratingbar.RatingBar;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by 0shad on 21.06.2016.
 */
public class EditUserFragment extends BaseFragment {


    @Bind(R.id.edit_user_add)
    TextView editUserAdd;

    public static EditUserFragment show(BaseFragment baseFragment, User user) {
        return show(baseFragment, EditUserFragment.class, Constants.ArgsName.USER, user);
    }

    @Bind(R.id.edit_user_points)
    TextView userPoints;
    @Bind(R.id.edit_user_rating)
    RatingBar ratingBar;
    @Bind(R.id.edit_user_tick)
    ImageView tick;

    User user;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.empty, menu);
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_user, container, false);
        bind(rootView);
        getActivity().setTitle("");
        setHasOptionsMenu(true);
        user = (User) getArguments().getSerializable(Constants.ArgsName.USER);

        if (user != null) {
            ratingBar.setCount(user.getRate());
            GuiUtils.setText(userPoints, R.string.edit_user_points, user.getRate());
        }

        ButterKnife.bind(this, rootView);
        return rootView;

    }

    @Subscribe
    public void onEventMainThread(AddRateEvent event) {
        Integer rate = event.rate + user.getRate();
        if (rate > 10) {
            rate = 10;
        }
        user.setRate(rate);
        GuiUtils.setText(userPoints, R.string.edit_user_points, user.getRate());
        ratingBar.setCount(rate);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.edit_user_add)
    public void onClick() {
         PickerDialog.show(getFragmentManager());
    }
}
