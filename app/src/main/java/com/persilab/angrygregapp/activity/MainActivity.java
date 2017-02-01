package com.persilab.angrygregapp.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import com.persilab.angrygregapp.R;
import com.persilab.angrygregapp.database.SnappyHelper;
import com.persilab.angrygregapp.database.SuggestionProvider;
import com.persilab.angrygregapp.domain.Constants;
import com.persilab.angrygregapp.domain.entity.Token;
import com.persilab.angrygregapp.domain.entity.User;
import com.persilab.angrygregapp.domain.entity.json.JsonError;
import com.persilab.angrygregapp.domain.event.*;
import com.persilab.angrygregapp.fragments.BaseFragment;
import com.persilab.angrygregapp.fragments.ErrorFragment;
import com.persilab.angrygregapp.fragments.LoginFragment;
//import com.persilab.angrygregapp.job.TokenUpdateJob;
import com.persilab.angrygregapp.net.RestClient;
import com.persilab.angrygregapp.util.FragmentBuilder;
import com.persilab.angrygregapp.util.GuiUtils;
import com.snappydb.SnappydbException;
import net.vrallev.android.cat.Cat;
import org.greenrobot.eventbus.Subscribe;
import retrofit2.Response;


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
            replaceFragment(LoginFragment.class);
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
            case R.id.action_exit :
                SnappyHelper helper = new SnappyHelper(this, "logout");
                Token token = null;
                try {
                    token = helper.getSerializable(Token.class);
                    token.setAccessExpires(null);
                    helper.storeSerializable(token);
                } catch (SnappydbException e) {
                    Cat.e(e);
                } finally {
                    helper.close();
                }
                exit = true;
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void onEvent(NetworkEvent networkEvent) {
        System.out.println(networkEvent.message.toString());
        if(networkEvent.request == null) {
            ErrorFragment.show((BaseFragment) getCurrentFragment(), R.string.error_network);
            return;
        }
        String path = networkEvent.request.url().encodedPath().substring(5);
        String method = networkEvent.request.method();
        if (networkEvent.status == NetworkEvent.Status.FAILURE) {
            Cat.e(networkEvent.message.toString());
            if(networkEvent.message instanceof JsonError) {
                JsonError error = (JsonError) networkEvent.message;
                if(path.contains(RestClient.AUTH)) {
                    postEvent(new TokenUpdateEvent(networkEvent.status, null));
                    return;
                }
                if(path.matches(RestClient.ACCOUNTS + "/[a-z0-9]+")) {
                    if(method.equals("GET")) {
                        postEvent(new UserFoundEvent(networkEvent.status, null));
                    }
                    if(method.equals("DELETE")) {
                        postEvent(new UserDeletedEvent(networkEvent.status, null));
                    }
                }
            }
            ErrorFragment.show((BaseFragment) getCurrentFragment(), R.string.error);
        } else {
            if (networkEvent.message instanceof Response) {
                Response response = (Response) networkEvent.message;
                if (response.body() instanceof Token) {
                    postEvent(new TokenUpdateEvent(networkEvent.status, (Token) response.body()));
                }
                if (path.contains("points")) {
                    postEvent(new AddRateEvent(networkEvent.status, (User) response.body()));
                }
                if(path.matches(RestClient.ACCOUNTS + "/[a-z0-9]+")) {
                    if(method.equals("GET")) {
                        postEvent(new UserFoundEvent(networkEvent.status, (User) response.body()));
                    }
                    if(method.equals("DELETE")) {
                        postEvent(new UserDeletedEvent(networkEvent.status, path.substring(path.indexOf('/') + 1)));
                    }
                    if (method.equals("PUT")) {
                        GuiUtils.runInUI(this, var -> GuiUtils.toast(MainActivity.this, R.string.profile_save_success));
                    }
                }
                if(path.matches(RestClient.ACCOUNTS)) {
                    if (method.equals("POST")) {
                        GuiUtils.runInUI(this, var -> GuiUtils.toast(MainActivity.this, R.string.profile_save_success));
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(exit) {
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
