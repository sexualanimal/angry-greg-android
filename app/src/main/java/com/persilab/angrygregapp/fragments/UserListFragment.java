package com.persilab.angrygregapp.fragments;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.persilab.angrygregapp.R;
import com.persilab.angrygregapp.adapter.ItemListAdapter;
import com.persilab.angrygregapp.adapter.LazyItemListAdapter;
import com.persilab.angrygregapp.domain.entity.User;
import com.persilab.angrygregapp.job.TokenUpdateJob;
import com.persilab.angrygregapp.lister.DataSource;
import com.persilab.angrygregapp.lister.ListDataSource;
import com.persilab.angrygregapp.net.RestClient;
import com.persilab.angrygregapp.util.GuiUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by 0shad on 21.06.2016.
 */
public class UserListFragment extends ListFragment<User>{

    FloatingActionButton button;

    private List<User> users = new ArrayList<>();

    public static UserListFragment show(BaseFragment fragment) {
        return show(fragment, UserListFragment.class);
    }

    public UserListFragment() {
        enableFiltering = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(R.string.users);
        RelativeLayout root = (RelativeLayout) super.onCreateView(inflater, container, savedInstanceState);
        button = (FloatingActionButton) inflater.inflate(R.layout.list_floating_button, root, false);
        root.addView(button);
        button.setOnClickListener(v -> QRScannerFragment.show(UserListFragment.this));
        return  root;
    }

    @Override
    protected ItemListAdapter<User> getAdapter() {
        return new UserListAdapter();
    }

    @Override
    public void refreshData(boolean showProgress) {
        users.clear();
        super.refreshData(showProgress);
    }

    @Override
    protected DataSource<User> getDataSource() throws Exception {
        return (skip, size) -> {
            if(users.isEmpty()) {
                 users = RestClient
                         .serviceApi()
                         .accounts(TokenUpdateJob.getToken().getAccessToken(), null, null).execute().body();

            }
            return Stream.of(users).skip(skip).limit(size).collect(Collectors.toList());
        };
    }

    private class UserListAdapter extends LazyItemListAdapter<User> {

        public UserListAdapter() {
            super(R.layout.item_user);
        }

        @Override
        public void onBindHolder(ViewHolder holder, @Nullable User item) {
            ViewGroup root = (ViewGroup) holder.getItemView();
            GuiUtils.setText(root, R.id.item_user_name, item.getName());
            if(item.getAmountOfPoints() == 0 && item.getAmountOfFreeCoffe() == 0) {
                GuiUtils.setText(root, R.id.item_user_points, R.string.nopoints, 0);
            } else if(item.getAmountOfFreeCoffe() == 0) {
                GuiUtils.setText(root, R.id.item_user_points, R.string.points, item.getAmountOfPoints());
            } else {
                GuiUtils.setText(root, R.id.item_user_points, R.string.points_cups, item.getAmountOfPoints(), item.getAmountOfFreeCoffe());
            }
        }

        @Override
        public boolean onClick(View view, @Nullable User item) {
            if(item.getAmountOfFreeCoffe() == 1) {
                FreeCoffeeFragment.show(UserListFragment.this);
            } else {
                UserFragment.show(UserListFragment.this, item);
            }
            return true;
        }
    }
}
