package com.diandian.coolco.emilie.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.diandian.coolco.emilie.R;
import com.heinrichreimersoftware.materialdrawer.DrawerActivity;
import com.heinrichreimersoftware.materialdrawer.DrawerView;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerHeaderItem;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerItem;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerProfile;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;

public class MainActivity extends DrawerActivity implements View.OnClickListener {

//    @InjectView(R.id.tv_go2camera)
    private TextView go2cameraTextView;
//    @InjectView(R.id.tv_go2gallery)
    private TextView go2galleryTextView;

    private DrawerView drawer;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        setContentView(R.layout.activity_main);

        initDrawer();
        initView();
    }

    private void initDrawer() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer = (DrawerView) findViewById(R.id.drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);


        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        ) {

            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }
        };

        drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.color_primary_dark));
        drawerLayout.setDrawerListener(drawerToggle);
        drawerLayout.closeDrawer(drawer);

//        int drawerItemIconColor = Color.parseColor("#1D6462");

        drawer.addItem(new DrawerHeaderItem().setTitle("我的部落"));
        drawer.addDivider();

        drawer.addItem(new DrawerItem()
                        .setImage(new IconDrawable(this, Iconify.IconValue.md_search).actionBarSize().colorRes(R.color.drawer_item_icon))
                        .setTextPrimary("搜索")
        );
        drawer.addDivider();

        drawer.addItem(new DrawerItem()
                        .setImage(new IconDrawable(this, Iconify.IconValue.md_favorite_outline).actionBarSize().colorRes(R.color.drawer_item_icon))
                        .setTextPrimary("我的收藏")
                        .setTextSecondary("收藏我之所爱")
        );
        drawer.addDivider();

        drawer.addItem(new DrawerItem()
                        .setImage(new IconDrawable(this, Iconify.IconValue.md_bookmark_outline).actionBarSize().colorRes(R.color.drawer_item_icon))
                        .setTextPrimary("历史搜索")
        );


        drawer.addItem(new DrawerHeaderItem().setTitle("美丽天地"));
        drawer.addDivider();

        drawer.addItem(new DrawerItem()
                        .setImage(new IconDrawable(this, Iconify.IconValue.md_explore).actionBarSize().colorRes(R.color.drawer_item_icon))
                        .setTextPrimary("猜你喜欢")
                        .setTextSecondary("为你贴心推荐")
        );

        drawer.addDivider();

        drawer.addItem(new DrawerItem()
                        .setImage(new IconDrawable(this, Iconify.IconValue.md_whatshot).actionBarSize().colorRes(R.color.drawer_item_icon))
                        .setTextPrimary("潮流趋势")
                        .setTextSecondary("就是要你好看")
        );

//        drawer.addDivider();

//        drawer.addItem(new DrawerItem()
//                        .setImage(new IconDrawable(this, Iconify.IconValue.md_settings).actionBarSize().colorRes(R.color.drawer_item_icon))
//                        .setTextPrimary("设置")
//        );

//
//        drawer.addItem(new DrawerItem()
//                        .setTextPrimary("会开飞机的鱼")
//        );
//
//        drawer.addItem(new DrawerItem()
//                        .setRoundedImage((BitmapDrawable) getResources().getDrawable(R.drawable.test), DrawerItem.SMALL_AVATAR)
//                        .setTextPrimary("small avatar")
//                        .setTextSecondary("three line ..................................", DrawerItem.THREE_LINE)
//        );

        drawer.selectItem(1);
        drawer.setOnItemClickListener(new DrawerItem.OnItemClickListener() {
            @Override
            public void onClick(DrawerItem item, long id, int position) {
                drawer.selectItem(position);
                Toast.makeText(MainActivity.this, "Clicked item #" + position, Toast.LENGTH_SHORT).show();
            }
        });


        drawer.addFixedItem(new DrawerItem()
                        .setImage(new IconDrawable(this, Iconify.IconValue.md_person_outline).actionBarSize().colorRes(R.color.drawer_item_icon))
//                        .setRoundedImage((BitmapDrawable) getResources().getDrawable(R.drawable.test), DrawerItem.SMALL_AVATAR)
                        .setTextPrimary("账户")
        );

        drawer.addFixedItem(new DrawerItem()
                        .setImage(new IconDrawable(this, Iconify.IconValue.md_settings).actionBarSize().colorRes(R.color.drawer_item_icon))
                        .setTextPrimary("设置")
        );

        drawer.setOnFixedItemClickListener(new DrawerItem.OnItemClickListener() {
            @Override
            public void onClick(DrawerItem item, long id, int position) {
                drawer.selectFixedItem(position);
                Toast.makeText(MainActivity.this, "Clicked fixed item #" + position, Toast.LENGTH_SHORT).show();
            }
        });


        drawer.addProfile(new DrawerProfile()
                        .setId(1)
                        .setRoundedAvatar((BitmapDrawable) getResources().getDrawable(R.drawable.test))
                        .setBackground(getResources().getDrawable(R.drawable.avatar_bg))
                        .setName("amelie")
                        .setDescription("All is well that ends.")
        );

        drawer.addProfile(new DrawerProfile()
                        .setId(2)
                        .setRoundedAvatar((BitmapDrawable) getResources().getDrawable(R.drawable.test))
                        .setBackground(getResources().getDrawable(R.drawable.avatar_bg))
                        .setName("amelie")
                        .setDescription("没有熊掌可以吃鱼")
        );

        drawer.addProfile(new DrawerProfile()
                        .setId(3)
                        .setRoundedAvatar((BitmapDrawable) getResources().getDrawable(R.drawable.test))
                        .setBackground(getResources().getDrawable(R.drawable.avatar_bg))
                        .setName("amelie")
                        .setDescription("没有熊掌可以吃鱼")
        );


        drawer.setOnProfileClickListener(new DrawerProfile.OnProfileClickListener() {
            @Override
            public void onClick(DrawerProfile profile, long id) {
                Toast.makeText(MainActivity.this, "Clicked profile *" + id, Toast.LENGTH_SHORT).show();
            }
        });
        drawer.setOnProfileSwitchListener(new DrawerProfile.OnProfileSwitchListener() {
            @Override
            public void onSwitch(DrawerProfile oldProfile, long oldId, DrawerProfile newProfile, long newId) {
                Toast.makeText(MainActivity.this, "Switched from profile *" + oldId + " to profile *" + newId, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void openDrawerFrameLayout(View view) {
//        Intent intent = new Intent(this, MainActivity2.class);
//        startActivity(intent);
    }

    public void openDrawerActivity(View view) {
//        Intent intent = new Intent(this, MainActivity3.class);
//        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
//            case R.id.action_github:
//                String url = "https://github.com/HeinrichReimer/material-drawer";
//                Intent i = new Intent(Intent.ACTION_VIEW);
//                i.setData(Uri.parse(url));
//                startActivity(i);
//                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }


    private void initView() {
        go2cameraTextView = (TextView) findViewById(R.id.tv_go2camera);
        go2galleryTextView = (TextView) findViewById(R.id.tv_go2gallery);
        int iconSizeInDp = 80;
        IconDrawable cameraIconDrawable = new IconDrawable(this, Iconify.IconValue.md_photo_camera).colorRes(R.color.main_entry).sizeDp(iconSizeInDp);
        IconDrawable galleryIconDrawable = new IconDrawable(this, Iconify.IconValue.md_photo).colorRes(R.color.main_entry).sizeDp(iconSizeInDp);
        go2cameraTextView.setCompoundDrawables(null, cameraIconDrawable, null, null);
        go2galleryTextView.setCompoundDrawables(null, galleryIconDrawable, null, null);
        go2cameraTextView.setOnClickListener(this);
        go2galleryTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_go2camera:
                startSrcImgObtainActivity();
                break;
            case R.id.tv_go2gallery:
                break;
        }
    }

    private void startSrcImgObtainActivity() {
        Intent intent = new Intent(this, SrcImgObtainActivity.class);
        startActivity(intent);
    }

}
