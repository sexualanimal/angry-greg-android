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
import com.persilab.angrygregapp.domain.entity.json.JsonError;
import com.persilab.angrygregapp.domain.event.AddRateEvent;
import com.persilab.angrygregapp.domain.event.FragmentAttachedEvent;
import com.persilab.angrygregapp.domain.event.NetworkEvent;
import com.persilab.angrygregapp.domain.event.PostLoadEvent;
import com.persilab.angrygregapp.domain.event.TokenUpdateEvent;
import com.persilab.angrygregapp.domain.event.UserDeletedEvent;
import com.persilab.angrygregapp.domain.event.UserFoundEvent;
import com.persilab.angrygregapp.fragments.LoginFragment;
import com.persilab.angrygregapp.fragments.LogoFragment;
import com.persilab.angrygregapp.net.RestClient;
import com.persilab.angrygregapp.util.GuiUtils;

import net.vrallev.android.cat.Cat;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import retrofit2.Response;

import static com.persilab.angrygregapp.domain.Constants.Net.RESET_TOKEN;


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
        System.out.println(networkEvent.message.toString());
        String path = networkEvent.request.url().encodedPath().substring(5);
        String method = networkEvent.request.method();
        if (networkEvent.status == NetworkEvent.Status.FAILURE) {
            Cat.e(networkEvent.message.toString());
            if (networkEvent.message instanceof JsonError) {
                JsonError error = (JsonError) networkEvent.message;
                if (path.contains(RestClient.AUTH)) {
                    postEvent(new TokenUpdateEvent(networkEvent.status, null));
                    return;
                }
                if (path.matches(RestClient.ACCOUNTS + "/[a-z0-9]+")) {
                    if (method.equals("GET")) {
                        postEvent(new UserFoundEvent(networkEvent.status, null));
                        return;
                    }
                    if (method.equals("DELETE")) {
                        postEvent(new UserDeletedEvent(networkEvent.status, null));
                        return;
                    }
                }
            }
            if (((Response) networkEvent.message).code() == 403) {
                postEvent(new TokenUpdateEvent(networkEvent.status, null));
                return;
            }
//            ErrorFragment.show((BaseFragment) getCurrentFragment(), R.string.error); //think about add another errors
        } else {
            if (networkEvent.message instanceof Response) {
                Response response = (Response) networkEvent.message;
                if (response.body() instanceof Token) {
                    postEvent(new TokenUpdateEvent(networkEvent.status, (Token) response.body()));
                    return;
                }
                if (path.contains("points")) {
                    postEvent(new AddRateEvent(networkEvent.status, (UserNeedCoffee) response.body()));
                    return;
                }
                if (path.matches(RestClient.ACCOUNTS + "/[a-z0-9]+")) {
                    if (method.equals("GET")) {
                        postEvent(new UserFoundEvent(networkEvent.status, (User) response.body()));
                        return;
                    }
                    if (method.equals("DELETE")) {
                        postEvent(new UserDeletedEvent(networkEvent.status, path.substring(path.indexOf('/') + 1)));
                        return;
                    }
                    if (method.equals("PUT")) {
                        GuiUtils.runInUI(this, var -> GuiUtils.toast(MainActivity.this, R.string.profile_save_success));
                        return;
                    }
                }
                if (path.matches(RestClient.ACCOUNTS)) {
                    if (method.equals("POST")) {
                        GuiUtils.runInUI(this, var -> GuiUtils.toast(MainActivity.this, R.string.profile_save_success));
                        return;
                    }
                }
                if (path.contains("accounts") && !(((Response) networkEvent.message).body() instanceof User)) {
                    postEvent(new PostLoadEvent((List<User>) ((Response) networkEvent.message).body()));
                    return;
                }
            }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
