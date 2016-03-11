package com.diandian.coolco.emilie.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import java.lang.reflect.InvocationTargetException;

import de.greenrobot.event.EventBus;
import roboguice.fragment.RoboFragment;

public class BaseFragment extends RoboFragment {

    private static final String ARG_TITLE = "title";

    public static BaseFragment newInstance(Class<? extends BaseFragment> clazz, String title) {
        BaseFragment fragment = null;
        try {
            fragment = clazz.getDeclaredConstructor().newInstance();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String title = getArguments().getString(ARG_TITLE);
//            getActivity().setTitle(title);
            if (getActivity() instanceof ActionBarActivity) {
                if (((ActionBarActivity) getActivity()).getSupportActionBar() != null) {
                    ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(title);
                }
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        registerEventBus();
    }

    @Override
    public void onDetach() {
        unregisterEventBus();
        super.onDetach();
    }

    public void onEvent(String empty) {

    }

    private void registerEventBus() {
        EventBus.getDefault().register(this);
    }

    private void unregisterEventBus() {
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        RefWatcher refWatcher = MyApplication.getRefWatcher(getActivity());
//        refWatcher.watch(this);
    }
}
