package com.diandian.coolco.emilie.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.drawable.ClipDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.diandian.coolco.emilie.R;
import com.diandian.coolco.emilie.utility.SuperToastUtil;

import roboguice.inject.InjectView;

public class WebActivity extends BaseActivity {

    @InjectView(R.id.wb)
    private WebView webview;
    @InjectView(R.id.iv_progress)
    private ImageView progressImageView;

    private ClipDrawable progressClipDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_web);

        init();
    }

    private void init() {
        progressClipDrawable = (ClipDrawable) progressImageView.getDrawable();

        webview.getSettings().setJavaScriptEnabled(true);

        final Activity activity = this;
        webview.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
//                activity.setProgress(progress * 1000);
                if (progressClipDrawable != null) {
                    progressClipDrawable.setLevel(progress * 100);
                }
                if (progress == 100) {
                    progressImageView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                getSupportActionBar().setTitle(title);
            }
        });
        webview.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });

        webview.loadUrl("http://myron.sinaapp.com/list");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_web, menu);
        initMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_copy_url_to_clipboard:
                copyUrl2clipboard();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void copyUrl2clipboard() {
        final String url = webview.getUrl();
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("simple text", url);
        clipboard.setPrimaryClip(clip);
        SuperToastUtil.showToast(this, "复制链接成功");
    }
}
