package com.persilab.angrygregapp.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;

import com.persilab.angrygregapp.App;
import com.persilab.angrygregapp.R;
import com.persilab.angrygregapp.database.SuggestionProvider;
import com.persilab.angrygregapp.domain.Constants;
import com.persilab.angrygregapp.domain.entity.Token;
import com.persilab.angrygregapp.domain.entity.User;
import com.persilab.angrygregapp.domain.entity.UserNeedCoffee;
import com.persilab.angrygregapp.domain.event.AddRateEvent;
import com.persilab.angrygregapp.domain.event.FragmentAttachedEvent;
import com.persilab.angrygregapp.domain.event.GoToLoginEvent;
import com.persilab.angrygregapp.domain.event.NetworkEvent;
import com.persilab.angrygregapp.domain.event.PostLoadEvent;
import com.persilab.angrygregapp.domain.event.TokenUpdateEvent;
import com.persilab.angrygregapp.domain.event.UserDeletedEvent;
import com.persilab.angrygregapp.domain.event.UserFoundEvent;
import com.persilab.angrygregapp.fragments.BaseFragment;
import com.persilab.angrygregapp.fragments.ErrorFragment;
import com.persilab.angrygregapp.fragments.LoginFragment;
import com.persilab.angrygregapp.fragments.LogoFragment;
import com.persilab.angrygregapp.fragments.UserListFragment;
import com.persilab.angrygregapp.net.RestClient;
import com.persilab.angrygregapp.util.FragmentBuilder;
import com.persilab.angrygregapp.util.GuiUtils;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import retrofit2.Response;

import static com.persilab.angrygregapp.domain.Constants.Net.RESET_TOKEN;
import static com.persilab.angrygregapp.net.RestClient.ACCOUNTS;


public class MainActivity extends BaseActivity {

    private CharSequence title;
    boolean exit = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Востанавливаем фрагмент при смене ориентации экрана.
        Fragment sectionFragment = getLastFragment(savedInstanceState);
        if (sectionFragment != null) {
            restoreFragment(sectionFragment);
        } else {
            replaceFragment(LogoFragment.class);
        }
        getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    protected void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    SuggestionProvider.AUTHORITY, SuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);
            // SearchFragment.show(getCurrentFragment(), query); // Implement SearchFragment
        }
    }

    @Override
    protected boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }

    @Override
    protected void onDrawerClosed(View drawerView) {

    }

    @Override
    protected void onDrawerOpened(View drawerView) {

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_exit:
                App.setActualToken(null);
                SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.remove(RESET_TOKEN);
                editor.commit();
                replaceFragment(LoginFragment.class);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void onEvent(NetworkEvent networkEvent) {
        if (networkEvent.request != null) {
            String method = networkEvent.request.method();
            Response response = (Response) networkEvent.message;
            int code = ((Response) networkEvent.message).code();

            if (networkEvent.status == NetworkEvent.Status.FAILURE) {
                if (code == 400) {
                    if (networkEvent.request.url().toString().contains(ACCOUNTS)) {
                        GuiUtils.runInUI(this, var -> GuiUtils.toast(MainActivity.this, R.string.user_already));
                        return;
                    }
                }
                if (code == 401) {
                    System.out.println("401: Unautorized");
                    return;
                }
                if (code == 403) {
                    if (networkEvent.request.url().toString().contains(RestClient.AUTH + "/access")) {
                        postEvent(new TokenUpdateEvent(networkEvent.status, null));
                        return;
                    }
                    if (networkEvent.request.url().toString().contains(RestClient.AUTH + "/refresh")) {
                        System.out.println("403: Refresh token invalid");
                        return;
                    }
                }
                if (code == 404) {
                    System.out.println("404: User not found");
                    return;
                }
            } else {
                if (response.body() instanceof Token) {
                    postEvent(new TokenUpdateEvent(networkEvent.status, (Token) response.body()));
                    return;
                }
                if (response.body() instanceof UserNeedCoffee) {
                    postEvent(new AddRateEvent(networkEvent.status, (UserNeedCoffee) response.body()));
                    return;
                }
                if (response.body() instanceof List) {
                    postEvent(new PostLoadEvent((List<User>) ((Response) networkEvent.message).body()));
                    return;
                }
                if (response.body() instanceof User) {
                    if (method.equals("POST")) {
                        FragmentBuilder builder = new FragmentBuilder(getSupportFragmentManager());
                        builder.putArg(Constants.ArgsName.USER, App.getActualToken().getAccount());
                        replaceFragment(UserListFragment.class, builder);
                        GuiUtils.runInUI(this, var -> GuiUtils.toast(MainActivity.this, R.string.profile_save_success));
                        return;
                    }
                    if (method.equals("GET")) {
                        postEvent(new UserFoundEvent(networkEvent.status, (User) response.body()));
                        return;
                    }
                    if (method.equals("PUT")) {
                        GuiUtils.runInUI(this, var -> GuiUtils.toast(MainActivity.this, R.string.profile_save_success));
                        return;
                    }
                }
                if (response.body().equals("ok")) {
                    postEvent(new UserDeletedEvent(networkEvent.status, null));
                    return;
                }
            }
        } else {
            ErrorFragment.show((BaseFragment) getCurrentFragment(), R.string.error_network);
            return;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (exit) {
            System.exit(0);
        }
    }

    @Subscribe
    public void onEvent(FragmentAttachedEvent fragmentAttached) {
        title = fragmentAttached.fragment.getArguments().getString(Constants.ArgsName.LOGOUT);
    }

    @Subscribe
    public void onEvent(GoToLoginEvent goToLoginEvent) {
        replaceFragment(LoginFragment.class);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
