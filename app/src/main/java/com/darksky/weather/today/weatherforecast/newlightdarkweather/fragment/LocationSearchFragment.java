package com.darksky.weather.today.weatherforecast.newlightdarkweather.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.darksky.weather.today.weatherforecast.newlightdarkweather.R;
import com.darksky.weather.today.weatherforecast.newlightdarkweather.adapter.LocationListAdapter;
import com.darksky.weather.today.weatherforecast.newlightdarkweather.databinding.FragmentLocationSearchBinding;
import com.darksky.weather.today.weatherforecast.newlightdarkweather.model.locationdata.AddLocationData;
import com.darksky.weather.today.weatherforecast.newlightdarkweather.sharedpreference.Preference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocationSearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationSearchFragment extends Fragment {

    FragmentLocationSearchBinding locationSearchBinding;
    LocationListAdapter locationListAdapter;

    Type type = new TypeToken<List<AddLocationData>>() {
    }.getType();

    ArrayList<AddLocationData> addLocationDataArrayList = new ArrayList<>();

    private RefreshReceiver refreshReceiver;
    private RefreshReceiver1 refreshReceiver1;

    public LocationSearchFragment() {
        // Required empty public constructor
    }

    public static LocationSearchFragment newInstance(String param1, String param2) {
        LocationSearchFragment fragment = new LocationSearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        locationSearchBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_location_search, container, false);

        refreshReceiver = new RefreshReceiver();
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(refreshReceiver,
                new IntentFilter("REFRESH_LOCATION"));

        refreshReceiver1 = new RefreshReceiver1();
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(refreshReceiver1,
                new IntentFilter("REFRESH_LOC"));

        int nightModeFlags = getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;

        if (nightModeFlags != Configuration.UI_MODE_NIGHT_YES) {
            locationSearchBinding.imgGrediant.setVisibility(View.GONE);
        } else {
            locationSearchBinding.imgGrediant.setVisibility(View.VISIBLE);
        }

        if (!Preference.getDefaultLocationListInfo(requireContext()).equals("")) {
            AddLocationData addLocationData = new AddLocationData();
            addLocationDataArrayList.add(0, addLocationData);
            addLocationDataArrayList.addAll(new Gson().fromJson(Preference.getDefaultLocationListInfo(requireContext()), type));
        } else {
            addLocationDataArrayList = new ArrayList<>();
            AddLocationData addLocationData = new AddLocationData();
            addLocationDataArrayList.add(0, addLocationData);
        }
        Log.e("LLL_Size1: ", String.valueOf(addLocationDataArrayList.size()));
        locationSearchBinding.rvLocation.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        locationListAdapter = new LocationListAdapter(requireActivity(), addLocationDataArrayList);
        locationSearchBinding.rvLocation.setAdapter(locationListAdapter);

        return locationSearchBinding.getRoot();
    }

    private class RefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            /*addLocationDataArrayList = new ArrayList<>();

            if (!Preference.getDefaultLocationListInfo(requireContext()).equals("")) {
                AddLocationData addLocationData = new AddLocationData();
                addLocationDataArrayList.add(0, addLocationData);
                addLocationDataArrayList.addAll(new Gson().fromJson(Preference.getDefaultLocationListInfo(requireContext()), type));
                Log.e("LLL_Size: ", String.valueOf(addLocationDataArrayList.size()));
            } else {
                addLocationDataArrayList = new ArrayList<>();
                AddLocationData addLocationData = new AddLocationData();
                addLocationDataArrayList.add(0, addLocationData);
            }

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    locationListAdapter.notifyDataSetChanged();
                }
            });
*/

            addLocationDataArrayList = new ArrayList<>();

            if (!Preference.getDefaultLocationListInfo(requireContext()).equals("")) {
                AddLocationData addLocationData = new AddLocationData();
                addLocationDataArrayList.add(0, addLocationData);
                addLocationDataArrayList.addAll(new Gson().fromJson(Preference.getDefaultLocationListInfo(requireContext()), type));
            } else {
                addLocationDataArrayList = new ArrayList<>();
                AddLocationData addLocationData = new AddLocationData();
                addLocationDataArrayList.add(0, addLocationData);
            }
            Log.e("LLL_Size1: ", String.valueOf(addLocationDataArrayList.size()));
            locationSearchBinding.rvLocation.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
            locationListAdapter = new LocationListAdapter(getActivity(), addLocationDataArrayList);
            locationSearchBinding.rvLocation.setAdapter(locationListAdapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private class RefreshReceiver1 extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            locationSearchBinding.rlLoading.setVisibility(View.VISIBLE);
            new Handler().postDelayed(() -> {
                locationSearchBinding.rlLoading.setVisibility(View.GONE);
            }, 3000);
        }
    }
}