package com.darksky.weather.today.weatherforecast.newlightdarkweather.Util;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.darksky.weather.today.weatherforecast.newlightdarkweather.R;

public class Const {
    public static String KEY = "";

    public final static String SHARED_PREFS = "NewLightDarkWeatherApp";
    public final static String PARAM_VALID_LATITUDE = "latitude";
    public final static String PARAM_VALID_LONGITUDE = "longitude";
    public final static String PARAM_VALID_TEMPARATURE_UNIT = "temparature_unit";
    public final static String PARAM_VALID_KEY = "valid_key";
    public final static String PARAM_VALID_CURRENT_KEY = "current_key";
    public final static String PARAM_VALID_TIME_FORMATE = "time_formate";
    public final static String PARAM_VALID_NOTIFICATION_ON_OFF = "notification_on_off";
    public final static String PARAM_VALID_NIGHT_DAY = "valid_day_night";
    public final static String PARAM_VALID_RATING = "rating_done";
    public final static String PARAM_VALID_WIDGET_ALARM = "valid_widget_Alarm";

    public final static String IS_DEAFAULT_LOCATION = "is_default_location";
    public final static String PARAM_VALID_THEME_TYPE = "valid_theme_type";


    public static int[] weatherIcon = {
            R.drawable.ic_1,
            R.drawable.ic_234,
            R.drawable.ic_234,
            R.drawable.ic_234,
            R.drawable.ic_5,
            R.drawable.ic_5,
            R.drawable.ic_6,
            R.drawable.ic_7,
            R.drawable.ic_8,
            R.drawable.ic_8,
            R.drawable.ic_11,
            R.drawable.ic_12_18,
            R.drawable.ic_13_14,
            R.drawable.ic_13_14,
            R.drawable.ic_15,
            R.drawable.ic_16_17,
            R.drawable.ic_16_17,
            R.drawable.ic_12_18,
            R.drawable.ic_19_22,
            R.drawable.ic_20_21,
            R.drawable.ic_20_21,
            R.drawable.ic_19_22,
            R.drawable.ic_23,
            R.drawable.ic_24,
            R.drawable.ic_25,
            R.drawable.ic_26,
            R.drawable.ic_26,
            R.drawable.ic_26,
            R.drawable.ic_29,
            R.drawable.ic_30,
            R.drawable.ic_31,
            R.drawable.ic_32,
            R.drawable.ic_33,
            R.drawable.ic_34,
            R.drawable.ic_35,
            R.drawable.ic_36,
            R.drawable.ic_37,
            R.drawable.ic_38,
            R.drawable.ic_39,
            R.drawable.ic_40,
            R.drawable.ic_41,
            R.drawable.ic_42,
            R.drawable.ic_43,
            R.drawable.ic_44
    };

    public static int getMaxValue(int[] numbers) {
        int maxValue = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            if (numbers[i] > maxValue) {
                maxValue = numbers[i];
            }
        }
        return maxValue;
    }

    public static int getMinValue(int[] numbers) {
        int minValue = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            if (numbers[i] < minValue) {
                minValue = numbers[i];
            }
        }
        return minValue;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
