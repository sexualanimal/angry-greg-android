package com.persilab.angrygregapp.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.whinc.widget.ratingbar.RatingBar;
import android.widget.TextView;
import butterknife.Bind;
import com.persilab.angrygregapp.R;
import com.persilab.angrygregapp.domain.Constants;
import com.persilab.angrygregapp.domain.entity.User;
import com.persilab.angrygregapp.util.GuiUtils;
import net.glxn.qrgen.android.QRCode;

/**
 * Created by 0shad on 21.06.2016.
 */
public class UserFragment extends BaseFragment {


    public static UserFragment show(BaseFragment baseFragment, User user) {
        return show(baseFragment, UserFragment.class, Constants.ArgsName.USER, user);
    }

    @Bind(R.id.user_points)
    TextView userPoints;
    @Bind(R.id.user_rating)
    RatingBar ratingBar;
    @Bind(R.id.user_card_qr)
    ImageView cardQr;


    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_user, container, false);

        bind(rootView);

        getActivity().setTitle(R.string.user);

        User user = (User) getArguments().getSerializable(Constants.ArgsName.USER);

        if (user != null) {
            int offColor = Color.TRANSPARENT;
            int onColor = Color.BLACK;
            int width = GuiUtils.getScreenSize(getContext()).y;
            cardQr.setImageBitmap(QRCode.from(user.getId()).withCharset("UTF-8").withColor(onColor, offColor).withSize(width, width).bitmap());
            if(user.getAmountOfFreeCoffe() == 0)  {
                GuiUtils.setText(userPoints, R.string.user_points, user.getAmountOfPoints(), ratingBar.getMaxCount());
            } else {
                GuiUtils.setText(userPoints, R.string.user_points_and_cups, user.getAmountOfPoints(), user.getAmountOfFreeCoffe(), ratingBar.getMaxCount());
            }
            ratingBar.setCount(user.getAmountOfPoints());

        }

        return rootView;

    }

}
