package com.darksky.weather.today.weatherforecast.newlightdarkweather.application;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDexApplication;

import com.darksky.weather.today.weatherforecast.newlightdarkweather.sharedpreference.Preference;


public class MainApplication extends MultiDexApplication {

    private static Context context;

    public static Context requireContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    @Override
    protected void attachBaseContext(Context base) {
        Context context = setupTheme(base);
        super.attachBaseContext(context);
    }

    public Context setupTheme(Context context) {

        Resources res = context.getResources();
        int mode = res.getConfiguration().uiMode;

        switch (Preference.getTheme(context)) {
            case "Dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                mode = Configuration.UI_MODE_NIGHT_YES;
                break;
            case "Light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                mode = Configuration.UI_MODE_NIGHT_NO;
                break;
        }


       /* Configuration config = new Configuration(res.getConfiguration());
        config.uiMode = mode;
        if (Build.VERSION.SDK_INT >= 17) {
            context = context.createConfigurationContext(config);
        } else {
            res.updateConfiguration(config, res.getDisplayMetrics());
        }*/
        return context;
    }

}
