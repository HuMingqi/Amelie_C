package com.diandian.coolco.emilie.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.diandian.coolco.emilie.R;
import com.diandian.coolco.emilie.fragment.AccountFragment;
import com.diandian.coolco.emilie.fragment.BaseFragment;
import com.diandian.coolco.emilie.fragment.BlankFragment;
import com.diandian.coolco.emilie.fragment.CollectionFragment;
import com.diandian.coolco.emilie.fragment.SearchFragment;
import com.diandian.coolco.emilie.fragment.NavigationDrawerFragment;
import com.diandian.coolco.emilie.fragment.SettingFragment;
import com.diandian.coolco.emilie.utility.SuperToastUtil;

public class HomeActivity extends BaseActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment drawerFragment;
    private final static String SEARCH_FRAGMENT_TAG = "search";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        drawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);//drawerlayout is homelayout

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextAppearance(this, R.style.ToolBarTitleTextAppearance);
        setSupportActionBar(toolbar);

        // Set up the drawer.
        drawerFragment.setUp(R.id.navigation_drawer, drawerLayout, toolbar);

        //load search fragment in homepage,look it in fragment_search.xml
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, BaseFragment.newInstance(SearchFragment.class, "搜索"), SEARCH_FRAGMENT_TAG)
                .commit();
    }

    @Override
    public void onNavigationDrawerItemSelected(int sectionViewId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (sectionViewId) {
            case R.id.rl_search:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, BaseFragment.newInstance(SearchFragment.class, "搜索"), SEARCH_FRAGMENT_TAG)
                        .commit();
                break;
            case R.id.rl_collection:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, BaseFragment.newInstance(CollectionFragment.class, "我的收藏"))
                        .commit();
                break;
            case R.id.rl_account:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, BaseFragment.newInstance(AccountFragment.class, "用户信息"))
                        .commit();
                break;
            case R.id.rl_setting:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, BaseFragment.newInstance(SettingFragment.class, "设置"))
                        .commit();
                break;
            case R.id.rl_history:
            case R.id.rl_recommend:
            case R.id.rl_hot:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, BaseFragment.newInstance(BlankFragment.class, "待完善"))
                        .commit();

                break;
        }

    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!drawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.menu_home, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long backpressed;

    @Override
    public void onBackPressed() {
        if (drawerFragment.isDrawerOpen()) {
            super.onBackPressed();
        } else {
            if (getSupportFragmentManager().findFragmentByTag(SEARCH_FRAGMENT_TAG) == null) {
                onNavigationDrawerItemSelected(R.id.rl_search);
            } else {
                if (backpressed + TIME_INTERVAL > System.currentTimeMillis()) {
                    super.onBackPressed();
                    return;
                } else {
                    SuperToastUtil.showToast(this, "再按一次返回键退出");
                }
                backpressed = System.currentTimeMillis();
            }
        }
    }
}
