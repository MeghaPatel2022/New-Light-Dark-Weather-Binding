package com.darksky.weather.today.weatherforecast.newlightdarkweather;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.darksky.weather.today.weatherforecast.newlightdarkweather.Util.ConnectionDetector;
import com.darksky.weather.today.weatherforecast.newlightdarkweather.adapter.ViewPagerAdapter;
import com.darksky.weather.today.weatherforecast.newlightdarkweather.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;

public class MainActivity extends AppCompatActivity {

    public ActivityMainBinding mainBinding;

    Boolean isInternetPresent = false;
    ConnectionDetector cd;

    ViewPagerAdapter viewPagerAdapter;
    private boolean isSet = false;

    private RefreshViewPagerReceiver refreshViewPagerReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        mainBinding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);

        RefreshViewPagerReceiver refreshViewPagerReceiver = new RefreshViewPagerReceiver();
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(refreshViewPagerReceiver,
                new IntentFilter("REFRESH_VIEW"));

        int nightModeFlags = getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;

        if (nightModeFlags != Configuration.UI_MODE_NIGHT_YES) {
            mainBinding.imgGrediant.setVisibility(View.GONE);
        } else {
            mainBinding.imgGrediant.setVisibility(View.VISIBLE);
        }

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            if (!isSet) {
                mainBinding.rlLoading.setVisibility(View.GONE);
                mainBinding.rlMain.setVisibility(View.VISIBLE);
                setViewPagerAdapter();
                setIconSize(0);
            }
        } else {
            mainBinding.ivNoInternetConnection.setVisibility(View.VISIBLE);
        }

        if (!checkLocationPermission())
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public boolean checkLocationPermission() {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    private void setViewPagerAdapter() {
        isSet = true;
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mainBinding.viewPager.setAdapter(viewPagerAdapter);

        mainBinding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                switch (i) {
                    case 0:
                        setIconSize(0);
                        mainBinding.bottomNavigation.getMenu().findItem(R.id.navigation_home).setChecked(true);
                        break;
                    case 1:
                        setIconSize(1);
                        mainBinding.bottomNavigation.getMenu().findItem(R.id.navigation_location_search).setChecked(true);
                        break;
                    case 2:
                        setIconSize(2);
                        mainBinding.bottomNavigation.getMenu().findItem(R.id.navigation_day_15).setChecked(true);
                        break;
                    case 3:
                        setIconSize(3);
                        mainBinding.bottomNavigation.getMenu().findItem(R.id.navigation_setting).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        mainBinding.bottomNavigation.setOnNavigationItemSelectedListener(
                item -> {
                    switch (item.getItemId()) {
                        case R.id.navigation_home:
                            setIconSize(0);
                            mainBinding.viewPager.setCurrentItem(0);
                            break;
                        case R.id.navigation_location_search:
                            setIconSize(1);
                            mainBinding.viewPager.setCurrentItem(1);
                            break;
                        case R.id.navigation_day_15:
                            setIconSize(2);
                            mainBinding.viewPager.setCurrentItem(2);
                            break;
                        case R.id.navigation_setting:
                            setIconSize(3);
                            mainBinding.viewPager.setCurrentItem(3);
                            break;
                    }
                    return false;
                });
        mainBinding.viewPager.setOffscreenPageLimit(4);
    }

    @SuppressLint("RestrictedApi")
    private void setIconSize(int position) {
        for (int i = 0; i <= 3; i++) {
            BottomNavigationMenuView menuView = (BottomNavigationMenuView)
                    mainBinding.bottomNavigation.getChildAt(0);
            BottomNavigationItemView navigationItemView = (BottomNavigationItemView) menuView.getChildAt(i);

            final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            if (i == position) {
                navigationItemView.setIconSize((int)
                        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80,
                                displayMetrics));
                navigationItemView.setIconTintList(null);

                navigationItemView.setSelected(true);
            } else {
                navigationItemView.setIconSize((int)
                        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25,
                                displayMetrics));

                navigationItemView.setSelected(false);
            }
        }
    }

    private class RefreshViewPagerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mainBinding.rlLoading.setVisibility(View.VISIBLE);
            new Handler().postDelayed(() -> {
                mainBinding.rlLoading.setVisibility(View.GONE);
                mainBinding.viewPager.setCurrentItem(0, true);
            }, 2000);
        }
    }
}