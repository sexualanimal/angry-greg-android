package com.persilab.angrygregapp.fragments;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.persilab.angrygregapp.R;
import com.persilab.angrygregapp.adapter.ItemListAdapter;
import com.persilab.angrygregapp.adapter.LazyItemListAdapter;
import com.persilab.angrygregapp.domain.entity.User;
import com.persilab.angrygregapp.lister.DataSource;
import com.persilab.angrygregapp.lister.ListDataSource;
import com.persilab.angrygregapp.util.GuiUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by 0shad on 21.06.2016.
 */
public class UserListFragment extends ListFragment<User>{


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
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected ItemListAdapter<User> getAdapter() {
        return new UserListAdapter();
    }

    @Override
    protected DataSource<User> getDataSource() throws Exception {
        return new DataSource<User>() {
            @Override
            public List<User> getItems(int skip, int size) throws IOException {
                if(users.isEmpty()) {
                    for (int i = 0; i < 60; i++) {
                        users.add(new User());
                    }
                }
                return Stream.of(users).skip(skip).limit(size).collect(Collectors.toList());
            }
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
            if(item.getRate() == 0) {
                GuiUtils.setText(root, R.id.item_user_points, R.string.nopoints, 0);
            } else {
                GuiUtils.setText(root, R.id.item_user_points, R.string.points, item.getRate());
            }
        }

        @Override
        public boolean onClick(View view, @Nullable User item) {
            if(item.getRate() == 10) {
                FreeCoffeeFragment.show(UserListFragment.this);
            } else {
                UserFragment.show(UserListFragment.this, item);
            }
            return true;
        }
    }
}
