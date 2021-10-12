package com.darksky.weather.today.weatherforecast.newlightdarkweather;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.darksky.weather.today.weatherforecast.newlightdarkweather.databinding.ActivityAboutUsBinding;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class AboutUsActivity extends AppCompatActivity {

    ActivityAboutUsBinding activityAboutUsBinding;
    MyClickHandlers myClickHandlers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        activityAboutUsBinding = DataBindingUtil.setContentView(AboutUsActivity.this, R.layout.activity_about_us);
        myClickHandlers = new MyClickHandlers(AboutUsActivity.this);
        activityAboutUsBinding.setOnBackClick(myClickHandlers);

        int nightModeFlags = getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;

        if (nightModeFlags != Configuration.UI_MODE_NIGHT_YES) {
            activityAboutUsBinding.imgGrediant.setVisibility(View.GONE);
        } else {
            activityAboutUsBinding.imgGrediant.setVisibility(View.VISIBLE);
        }

        Glide
                .with(AboutUsActivity.this)
                .load(R.drawable.icon)
                .transition(withCrossFade())
                .transition(new DrawableTransitionOptions().crossFade(700))
                .into(activityAboutUsBinding.imgLogo);

        activityAboutUsBinding.tvVersion.setText("Version: " + BuildConfig.VERSION_NAME);

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
}