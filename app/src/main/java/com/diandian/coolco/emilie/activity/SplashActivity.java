package com.diandian.coolco.emilie.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.diandian.coolco.emilie.R;
import com.diandian.coolco.emilie.utility.Dimension;
import com.diandian.coolco.emilie.widget.FireworkText;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;

public class SplashActivity extends Activity {

    private static final long DURATION = 1200;

    private ImageView logoImageView;
    private TextView appNameTextView;
    private TextView copyrightTextView;

    private ObjectAnimator logoAlphatAnimator;
    private FireworkText appNameFireworkText;
    private FireworkText copyrightFireworkText;
    private Handler handler;
    private ObjectAnimator copyrightAlphaAnimator;
    private AnimatorSet logoAnimatorSet;
    private ObjectAnimator logoTranslateAnimator;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setBackgroundDrawable(null);

        setContentView(R.layout.activity_splash);

        init();
    }

    private void init() {
        logoImageView = (ImageView) findViewById(R.id.iv_logo);
        appNameTextView = (TextView) findViewById(R.id.tv_app_name);
        copyrightTextView = (TextView) findViewById(R.id.tv_copyright);

        logoImageView.setImageDrawable(new IconDrawable(this, Iconify.IconValue.md_cloud).sizeDp(80).color(Color.parseColor("#aaf9f9f9")));

        logoAlphatAnimator = ObjectAnimator.ofFloat(logoImageView, "alpha", 0, 1);
        logoAlphatAnimator.setDuration(DURATION);
        logoAlphatAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                startMainActivity();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        logoTranslateAnimator = ObjectAnimator.ofFloat(logoImageView, "translationY", -Dimension.dp2px(this, 100), 0);
        logoTranslateAnimator.setInterpolator(new BounceInterpolator());
        logoTranslateAnimator.setDuration(DURATION);

        logoAnimatorSet = new AnimatorSet();
        logoAnimatorSet.playTogether(logoTranslateAnimator, logoAlphatAnimator);

        copyrightAlphaAnimator = ObjectAnimator.ofFloat(copyrightTextView, "alpha", 0, 1);
        copyrightAlphaAnimator.setDuration(DURATION);

        appNameFireworkText = new FireworkText(appNameTextView);
        appNameFireworkText.setDuration(DURATION);
//        copyrightFireworkText = new FireworkText(copyrightTextView);
//        copyrightFireworkText.setDuration(DURATION);
    }


    @Override
    protected void onResume() {
        super.onResume();
        handler = new Handler(new Handler.Callback() {

            @Override
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case 0:
                        startAnimation();
                        break;

                    default:
                        break;
                }
                return false;
            }
        });
        handler.sendEmptyMessageDelayed(0, 300);
    }

    private void startAnimation(){
        logoImageView.setVisibility(View.VISIBLE);
        appNameTextView.setVisibility(View.VISIBLE);
        copyrightTextView.setVisibility(View.VISIBLE);
//        logoAlphatAnimator.start();
        logoAnimatorSet.start();
//        logoTranslateAnimator.start();
        copyrightAlphaAnimator.start();
        appNameFireworkText.startAnimation();
//        copyrightFireworkText.startAnimation();
    }


    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
        return true;
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
}
