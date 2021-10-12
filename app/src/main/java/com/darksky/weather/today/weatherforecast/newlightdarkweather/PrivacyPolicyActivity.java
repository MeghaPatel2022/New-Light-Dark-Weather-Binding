package com.darksky.weather.today.weatherforecast.newlightdarkweather;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.darksky.weather.today.weatherforecast.newlightdarkweather.databinding.ActivityPrivacyPolicyBinding;


public class PrivacyPolicyActivity extends AppCompatActivity {

    ActivityPrivacyPolicyBinding privacyPolicyBinding;
    MyClickHandlers myClickHandlers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        privacyPolicyBinding = DataBindingUtil.setContentView(PrivacyPolicyActivity.this, R.layout.activity_privacy_policy);
        myClickHandlers = new MyClickHandlers(PrivacyPolicyActivity.this);
        privacyPolicyBinding.setBackBtnClick(myClickHandlers);

        privacyPolicyBinding.ivWebview.setWebViewClient(new MyWebViewClient());
        openURL();

    }

    private void openURL() {
        privacyPolicyBinding.ivWebview.loadUrl(getResources().getString(R.string.privacy_policy));
        privacyPolicyBinding.ivWebview.requestFocus();
    }

    public class MyClickHandlers {
        Context context;

        public MyClickHandlers(Context context) {
            this.context = context;
        }

        public void onBackBtnClicked(View view) {
            onBackPressed();
        }

    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}