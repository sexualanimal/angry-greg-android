package com.persilab.angrygregapp.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.view.*;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.persilab.angrygregapp.App;
import com.persilab.angrygregapp.R;
import com.persilab.angrygregapp.adapter.ItemListAdapter;
import com.persilab.angrygregapp.adapter.LazyItemListAdapter;
import com.persilab.angrygregapp.domain.entity.User;
import com.persilab.angrygregapp.domain.event.AddRateEvent;
import com.persilab.angrygregapp.lister.DataSource;
import com.persilab.angrygregapp.net.RestClient;
import com.persilab.angrygregapp.util.GuiUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 0shad on 21.06.2016.
 */
public class UserListFragment extends ListFragment<User> {

    FloatingActionButton button;

    private List<User> users = new ArrayList<>();

    private boolean editMode = false;

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
        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (editMode) {
            menu.clear();
            inflater.inflate(R.menu.editlist, menu);
            ActionBar actionBar = getMainActivity().getCurrentActionBar();
            actionBar.setCustomView(R.layout.back_button);
            actionBar.getCustomView().findViewById(R.id.button_back).setOnClickListener(v -> exitEditMode());
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        } else {
            inflater.inflate(R.menu.viewlist, menu);
            inflater.inflate(R.menu.exit, menu);
            super.onCreateOptionsMenu(menu, inflater);
        }
    }

    @Override
    public boolean allowBackPress() {
        if(editMode) {
            exitEditMode();
            return false;
        } else {
            return super.allowBackPress();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.user_list_delete:
                List<User> needDelete = Stream.of(adapter.getItems()).filter(User::isDelete).collect(Collectors.toList());
                for (User user : needDelete) {
                    RestClient.serviceApi().deleteAccount(App.getActualToken().getAccessToken(),user.getId()).enqueue();
                }
                adapter.getItems().removeAll(needDelete);
                adapter.notifyDataSetChanged();
                break;
            case R.id.user_list_add:
                ProfileFragment.show(this, new User());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void exitEditMode() {
        editMode = !editMode;
        ActionBar actionBar = getMainActivity().getCurrentActionBar();
        actionBar.setDisplayShowCustomEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        getActivity().invalidateOptionsMenu();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected ItemListAdapter<User> newAdapter() {
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
            if (users.isEmpty()) {
                users = RestClient
                        .serviceApi()
                        .accounts(App.getActualToken().getAccessToken(), 50).execute().body();

            }
            return Stream.of(users).skip(skip).limit(size).collect(Collectors.toList());
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        users.clear();
        clearData();
        super.onStop();
    }

    @Subscribe
    public void onEvent(AddRateEvent event) {
        int index = getUserById(event.message.getAccount().getId());
        if(index > 0) {
            adapter.getItems().set(index, event.message.getAccount());
            GuiUtils.runInUI(getContext(), (var) -> adapter.notifyItemChanged(index));
        }
    }

    private int getUserById(final int id) {
        for (int i = 0; i < adapter.getItems().size(); i++) {
            if(adapter.getItems().get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }

    private class UserListAdapter extends LazyItemListAdapter<User> {

        public UserListAdapter() {
            super(R.layout.item_user);
        }

        @Override
        public void onBindHolder(ViewHolder holder, @Nullable User item) {
            ViewGroup root = (ViewGroup) holder.getItemView();
            GuiUtils.setText(root, R.id.item_user_name, item.getName());
            if (item.getAmountOfPoints() == 0 && item.getAmountOfFreeCoffe() == 0) {
                GuiUtils.setText(root, R.id.item_user_points, R.string.nopoints, 0);
            } else if (item.getAmountOfFreeCoffe() == 0) {
                GuiUtils.setText(root, R.id.item_user_points, R.string.points, item.getAmountOfPoints());
            } else {
                GuiUtils.setText(root, R.id.item_user_points, R.string.points_cups, item.getAmountOfPoints(), item.getAmountOfFreeCoffe());
            }
            CheckBox checkBox = holder.getView(R.id.item_user_checkbox);
            if (editMode) {
                button.setVisibility(View.INVISIBLE);
                checkBox.setVisibility(View.VISIBLE);
                checkBox.setChecked(false);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        ViewHolder holderChecked = (ViewHolder) buttonView.getTag();
                        items.get(holderChecked.getLayoutPosition()).setDelete(isChecked);
                    }
                });
            } else {
                button.setVisibility(View.VISIBLE);
                checkBox.setVisibility(View.GONE);
                checkBox.setChecked(false);
                item.setDelete(false);
            }
        }

        @Override
        public boolean onClick(View view, @Nullable User item) {
            if(!editMode) {
                AddPointsUserFragment.show(UserListFragment.this, item);
            }
            return true;
        }

        @Override
        public boolean onLongClick(View view, User item) {
            exitEditMode();
            return true;
        }

    }

}
