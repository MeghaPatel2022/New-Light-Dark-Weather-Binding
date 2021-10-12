package com.darksky.weather.today.weatherforecast.newlightdarkweather.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.darksky.weather.today.weatherforecast.newlightdarkweather.fragment.DayFragment;
import com.darksky.weather.today.weatherforecast.newlightdarkweather.fragment.HomeFragment;
import com.darksky.weather.today.weatherforecast.newlightdarkweather.fragment.LocationSearchFragment;
import com.darksky.weather.today.weatherforecast.newlightdarkweather.fragment.SettingFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private static final int NUM_ITEMS = 4;

    public ViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: // Fragment # 0 - This will show FirstFragment
                return new HomeFragment();
            case 1: // Fragment # 0 - This will show FirstFragment different title
                return new LocationSearchFragment();
            case 2: // Fragment # 1 - This will show SecondFragment
                return new DayFragment();
            case 3:
                return new SettingFragment();
            default:
                return null;
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
