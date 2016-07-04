package com.persilab.angrygregapp.fragments;

import android.os.Bundle;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.persilab.angrygregapp.R;
import com.persilab.angrygregapp.activity.MainActivity;
import com.persilab.angrygregapp.dialog.AddPointsDialog;
import com.persilab.angrygregapp.domain.Constants;
import com.persilab.angrygregapp.domain.entity.User;
import com.persilab.angrygregapp.domain.event.AddRateEvent;
import com.persilab.angrygregapp.net.RestClient;
import com.persilab.angrygregapp.util.FragmentBuilder;
import com.persilab.angrygregapp.util.GuiUtils;
import com.whinc.widget.ratingbar.RatingBar;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by 0shad on 21.06.2016.
 */
public class AddPointsUserFragment extends BaseFragment {

    @Bind(R.id.edit_user_add)
    TextView editUserAdd;

    public static void show(BaseFragment baseFragment, User user) {
        FragmentBuilder builder = new FragmentBuilder(baseFragment.getFragmentManager());
        builder.putArg(Constants.ArgsName.USER, user);
        baseFragment.getMainActivity().replaceFragment(AddPointsUserFragment.class, builder);
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
        menu.clear();
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_user, container, false);
        bind(rootView);
        user = (User) getArguments().getSerializable(Constants.ArgsName.USER);
        getActivity().setTitle("");
        setHasOptionsMenu(true);
        updateUi(user);
        return rootView;

    }

    @Subscribe
    public void onEven(AddRateEvent event) {
        user = event.message;
        GuiUtils.runInUI(getContext(), var -> updateUi(user));
    }

    private void updateUi(User user) {
        if (user != null) {
            if(user.getAmountOfFreeCoffe() == 0) {
                GuiUtils.setText(userPoints, R.string.edit_user_points, user.getAmountOfPoints());
            } else {
                GuiUtils.setText(userPoints, R.string.edit_user_points_and_cups, user.getAmountOfPoints(), user.getAmountOfFreeCoffe());
            }
            ratingBar.setCount(user.getAmountOfPoints());
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
         AddPointsDialog.show(getFragmentManager(), user);
    }
}
