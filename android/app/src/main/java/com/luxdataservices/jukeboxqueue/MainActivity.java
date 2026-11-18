package com.luxdataservices.jukeboxqueue;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Full-screen kiosk wrapper for the queue display hosted on GitHub Pages.
 * All of the display itself lives in index.html, so pushing to GitHub updates
 * the TV without reinstalling this app.
 */
public class MainActivity extends Activity {

    private static final String SITE_URL = "https://luxdataservices.github.io/karafun-queue-view/";
    private static final long RETRY_MS = 10_000;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private WebView web;
    private boolean showingError = false;
    private long lastBackPress = 0;

    private final Runnable retry = new Runnable() {
        @Override public void run() { loadSite(); }
    };

    // Reload once a day in the early morning so the TV always runs the latest version.
    private final Runnable dailyReload = new Runnable() {
        @Override public void run() {
            loadSite();
            scheduleDailyReload();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_FULLSCREEN);

        web = new WebView(this);
        web.setBackgroundColor(Color.parseColor("#1c0870"));
        web.setFocusable(true);
        web.setFocusableInTouchMode(true);

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);          // keeps the saved KaraFun code
        s.setDatabaseEnabled(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setCacheMode(WebSettings.LOAD_DEFAULT);
        s.setUseWideViewPort(true);
        s.setLoadWithOverviewMode(true);

        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false; // stay inside the app
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError err) {
                if (Build.VERSION.SDK_INT >= 21 && req.isForMainFrame()) showOffline();
            }

            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int code, String desc, String failingUrl) {
                if (failingUrl != null && failingUrl.startsWith(SITE_URL)) showOffline();
            }
        });

        setContentView(web);
        hideSystemUi();
        loadSite();
        scheduleDailyReload();
    }

    private void loadSite() {
        handler.removeCallbacks(retry);
        showingError = false;
        web.loadUrl(SITE_URL);
        web.requestFocus();
    }

    private void showOffline() {
        if (showingError) return;
        showingError = true;
        String html = "<html><body style='margin:0;height:100vh;display:flex;align-items:center;"
                + "justify-content:center;background:linear-gradient(135deg,#1c0870,#5e1c95);"
                + "color:#fff;font-family:sans-serif;font-size:5vh;text-align:center'>"
                + "<div>Waiting for Wi-Fi…<br><span style='font-size:3vh;opacity:.7'>"
                + "Retrying every 10 seconds</span></div></body></html>";
        web.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
        handler.postDelayed(retry, RETRY_MS);
    }

    private void scheduleDailyReload() {
        handler.removeCallbacks(dailyReload);
        Calendar next = Calendar.getInstance();
        next.set(Calendar.HOUR_OF_DAY, 5);
        next.set(Calendar.MINUTE, 0);
        next.set(Calendar.SECOND, 0);
        if (next.getTimeInMillis() <= System.currentTimeMillis()) next.add(Calendar.DAY_OF_MONTH, 1);
        handler.postDelayed(dailyReload, next.getTimeInMillis() - System.currentTimeMillis());
    }

    private void hideSystemUi() {
        View decor = getWindow().getDecorView();
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) hideSystemUi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        web.onResume();
        hideSystemUi();
    }

    @Override
    protected void onPause() {
        web.onPause();
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Back: press twice to leave the app, so a stray press doesn't close the display.
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long now = System.currentTimeMillis();
            if (now - lastBackPress < 2500) {
                finish();
            } else {
                lastBackPress = now;
                Toast.makeText(this, "Press Back again to exit", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        // Menu button: reload the page.
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            loadSite();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        if (web != null) web.destroy();
        super.onDestroy();
    }
}
