package com.persilab.angrygregapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import com.persilab.angrygregapp.R;
import com.persilab.angrygregapp.activity.BaseActivity;
import com.persilab.angrygregapp.domain.Constants;
import com.persilab.angrygregapp.domain.event.Event;
import com.persilab.angrygregapp.domain.event.FragmentAttachedEvent;
import com.persilab.angrygregapp.util.FragmentBuilder;
import com.persilab.angrygregapp.util.GuiUtils;
import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Field;

/**
 * A placeholder fragment containing a simple view.
 */
public class BaseFragment extends Fragment implements BaseActivity.BackCallback {

    /**
     * Returns a new instance of this fragment
     */
    public static <F extends BaseFragment> F newInstance(Class<F> fragmentClass, Bundle args) {
        return FragmentBuilder.newInstance(fragmentClass, args);
    }

    public static <F extends BaseFragment> F newInstance(Class<F> fragmentClass) {
        return FragmentBuilder.newInstance(fragmentClass);
    }

    // Базовые методы
    protected static <F extends BaseFragment> F show(FragmentBuilder builder, @IdRes int container, Class<F> fragmentClass) {
        return builder.newFragment().replaceFragment(container, fragmentClass);
    }

    protected static <F extends BaseFragment> F show(FragmentManager manager, @IdRes int container, Class<F> fragmentClass, String key, Object obj) {
        return new FragmentBuilder(manager).newFragment().putArg(key, obj).replaceFragment(container, fragmentClass);
    }

    protected static <F extends BaseFragment> F show(BaseFragment fragment, Class<F> fragmentClass, String key, Object obj) {
        return new FragmentBuilder(fragment.getFragmentManager()).newFragment().addToBackStack().putArg(key, obj).replaceFragment(fragment, fragmentClass);
    }

    protected static <F extends BaseFragment> F show(BaseFragment fragment, Class<F> fragmentClass) {
        return new FragmentBuilder(fragment.getFragmentManager()).newFragment().addToBackStack().replaceFragment(fragment, fragmentClass);
    }

    // Подобными методами должны вызыватся наследуемые фрагмент(их нужно реализовывать для кажного фрагмента заново)
    protected static BaseFragment show(FragmentBuilder builder, @IdRes int container) {
        return show(builder, container, BaseFragment.class);
    }

    protected static BaseFragment show(FragmentManager manager, @IdRes int container, String message) {
        return show(manager, container, BaseFragment.class, Constants.ArgsName.MESSAGE, message);
    }

    protected static BaseFragment show(BaseFragment fragment, String message) {
        return show(fragment, BaseFragment.class, Constants.ArgsName.MESSAGE, message);
    }

    protected boolean retainInstance = true;

    private TextView messageView;


    public BaseFragment() {
    }

    /**
     * This method will only be called once when the retained
     * Fragment is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retain this fragment across configuration changes.
        setRetainInstance(retainInstance);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_base, container, false);
        bind(rootView);
        String message = getArguments().getString(Constants.ArgsName.MESSAGE, "В разработке...");
        GuiUtils.setText(rootView.findViewById(R.id.test_message), message);
        return rootView;
    }

    public BaseFragment show(FragmentManager manager, @IdRes int container, String key, Object obj) {
        return show(manager, container, this.getClass(), key, obj);
    }

    public int getContainerId() {
        return ((ViewGroup) getView().getParent()).getId();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        postEvent(new FragmentAttachedEvent(this));
    }

    public boolean allowBackPress() {
       return true;
    }

    protected void postEvent(Event event) {
        EventBus.getDefault().post(event);
    }

    public void bind(View view) {
        ButterKnife.bind(this, view);
    }

    public void unbind() {
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbind();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // Android support bug https://code.google.com/p/android/issues/detail?id=42601
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
