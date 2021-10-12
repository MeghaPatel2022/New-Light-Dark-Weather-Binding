package com.darksky.weather.today.weatherforecast.newlightdarkweather.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.darksky.weather.today.weatherforecast.newlightdarkweather.R;
import com.darksky.weather.today.weatherforecast.newlightdarkweather.adapter.Weather15Days;
import com.darksky.weather.today.weatherforecast.newlightdarkweather.databinding.FragmentDayBinding;
import com.darksky.weather.today.weatherforecast.newlightdarkweather.model.days15.DailyForecastsItem;
import com.darksky.weather.today.weatherforecast.newlightdarkweather.model.days15.Days15Response;
import com.darksky.weather.today.weatherforecast.newlightdarkweather.sharedpreference.Preference;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DayFragment extends Fragment {

    private final List<DailyForecastsItem> dailyForecastsItemList = new ArrayList<>();
    private FragmentDayBinding dayBinding;
    private Weather15Days weather15Days;

    private RefreshReceiver refreshReceiver;

    public DayFragment() {
        // Required empty public constructor
    }

    public static DayFragment newInstance(String param1, String param2) {
        DayFragment fragment = new DayFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        dayBinding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_day, container, false);

        int nightModeFlags = getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;

        if (nightModeFlags != Configuration.UI_MODE_NIGHT_YES) {
            dayBinding.imgGrediant.setVisibility(View.GONE);
        } else {
            dayBinding.imgGrediant.setVisibility(View.VISIBLE);
        }

        refreshReceiver = new RefreshReceiver();
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(refreshReceiver,
                new IntentFilter("REFRESH"));

        dayBinding.rv15Days.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));


        if (!Preference.getKey(requireContext()).equals("")) {
            getDay15Data(Preference.getKey(requireContext()));
        }

        return dayBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void getDay15Data(String KEY) {
        Log.wtf("LLL_day15: ", KEY);
        boolean isMatrix = true;
        if (!Preference.getUnit(requireContext()).equalsIgnoreCase("Metric")) {
            isMatrix = false;
        }

        AndroidNetworking.get("https://api.accuweather.com/forecasts/v1/daily/15day/" + KEY + ".json")
                .addQueryParameter("apikey", "srRLeAmTroxPinDG8Aus3Ikl6tLGJd94")
                .addQueryParameter("language", "en-gb")
                .addQueryParameter("details", "true")
                .addQueryParameter("metric", String.valueOf(isMatrix))
                .setPriority(Priority.IMMEDIATE)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dailyForecastsItemList.clear();
                        if (weather15Days != null)
                            weather15Days.clear();
                        Days15Response days15Response = new Gson().fromJson(response.toString(), Days15Response.class);

                        for (int i = 1; i < 8; i++) {
                            dailyForecastsItemList.add(days15Response.getDailyForecasts().get(i));
                        }

                        // set 15Days data to adapter
                        List<Object> objects = new ArrayList<>();
                        objects.addAll(days15Response.getDailyForecasts());

                        weather15Days = new Weather15Days(objects, objects.size(), getActivity());
                        dayBinding.rv15Days.setAdapter(weather15Days);

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("LLL_Days15_Error11: ", anError.getErrorBody());
                    }
                });
    }

    private class RefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (isAdded()) {
                    getDay15Data(intent.getStringExtra("key"));
                }
            }
        }
    }

}