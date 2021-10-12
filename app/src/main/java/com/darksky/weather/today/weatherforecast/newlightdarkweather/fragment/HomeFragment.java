package com.darksky.weather.today.weatherforecast.newlightdarkweather.fragment;

import android.Manifest;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.CellLocation;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.darksky.weather.today.weatherforecast.newlightdarkweather.R;
import com.darksky.weather.today.weatherforecast.newlightdarkweather.Util.ConnectionDetector;
import com.darksky.weather.today.weatherforecast.newlightdarkweather.Util.Const;
import com.darksky.weather.today.weatherforecast.newlightdarkweather.Util.WeatherWidget;
import com.darksky.weather.today.weatherforecast.newlightdarkweather.adapter.DailyTempIconAdapter;
import com.darksky.weather.today.weatherforecast.newlightdarkweather.adapter.DailyWindAdapter;
import com.darksky.weather.today.weatherforecast.newlightdarkweather.adapter.HourlyWindAdapter;
import com.darksky.weather.today.weatherforecast.newlightdarkweather.databinding.FragmentHomeBinding;
import com.darksky.weather.today.weatherforecast.newlightdarkweather.model.currentdata.CurrentDataResponseItem;
import com.darksky.weather.today.weatherforecast.newlightdarkweather.model.days15.DailyForecastsItem;
import com.darksky.weather.today.weatherforecast.newlightdarkweather.model.days15.Days15Response;
import com.darksky.weather.today.weatherforecast.newlightdarkweather.model.hours24.Hours24ResponseItem;
import com.darksky.weather.today.weatherforecast.newlightdarkweather.model.locationdata.AddLocationData;
import com.darksky.weather.today.weatherforecast.newlightdarkweather.service.AlarmReceiver;
import com.darksky.weather.today.weatherforecast.newlightdarkweather.sharedpreference.Preference;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.EasyPermissions;

import static android.content.Context.LOCATION_SERVICE;

public class HomeFragment extends BaseFragment implements OnChartValueSelectedListener, LocationListener {

    public LocationManager mLocManager;
    String[] weekOfDay = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"};
    FragmentHomeBinding homeBinding;
    Type type = new TypeToken<List<AddLocationData>>() {
    }.getType();
    boolean isDay15Called = false;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    String mainKey = "";
    Typeface fontFace;
    private List<DailyForecastsItem> dailyForecastsItemList = new ArrayList<>();
    private ArrayList<Hours24ResponseItem> hours24ResponseItems = new ArrayList<>();
    private String city = "", area = "", country = "", address = "";

    private HourlyWindAdapter hourlyWindAdapter;
    private DailyTempIconAdapter tempIconAdapter;
    private DailyWindAdapter dailyWindAdapter;

    private RefreshReceiver refreshReceiver;


    public HomeFragment() {
        // Required empty public constructor
    }

    public static void updateAllWidgets(Context context, Intent intentWidget) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        intentWidget.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context.getPackageName(), WeatherWidget.class.getName()));
        intentWidget.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);
        context.sendBroadcast(intentWidget);
    }

    @Override
    void permissionGranted() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        homeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        AndroidNetworking.initialize(requireContext());
        cd = new ConnectionDetector(requireContext());
        isInternetPresent = cd.isConnectingToInternet();

        homeBinding.setMatrixType(Preference.getUnit(requireContext()));

        refreshReceiver = new RefreshReceiver();
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(refreshReceiver,
                new IntentFilter("REFRESH_HOME"));

        fontFace = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular);

        int nightModeFlags = getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;

        if (nightModeFlags != Configuration.UI_MODE_NIGHT_YES) {
            homeBinding.imgGrediant.setVisibility(View.GONE);
            homeBinding.imgGrediant1.setVisibility(View.GONE);
        } else {
            homeBinding.imgGrediant.setVisibility(View.VISIBLE);
            homeBinding.imgGrediant1.setVisibility(View.VISIBLE);
        }

        homeBinding.tvUnit.setText((char) 0x00B0 + "C");

        homeBinding.rvHourlyWind.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
        homeBinding.rvDailyWind.setLayoutManager(new GridLayoutManager(requireContext(), 7));
        homeBinding.rvDailyTemp.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));

        return homeBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isAdded()) {
            boolean isGranted = EasyPermissions.hasPermissions(requireActivity(), perms);
            if (isGranted) {
                Log.e("LLL_Set: ", "set");
                if (isInternetPresent) {
                    mLocManager = (LocationManager) requireContext().getSystemService(LOCATION_SERVICE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (requireContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && requireContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        } else {
                            mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                                    this);

                            mLocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,
                                    0, this);

                            locationUpdate();
                        }
                    }

                    setChartStyle();

                }
            } else {
                EasyPermissions.requestPermissions(this, getString(R.string.vw_rationale_storage),
                        RC_READ_EXTERNAL_STORAGE, perms);
            }
        }
    }

    private void locationUpdate() {
        CellLocation.requestLocationUpdate();
    }

    private void setPrecipitationChartData() {

        ArrayList<Entry> values = new ArrayList<>();
        LineDataSet set1;

        for (int i = 0; i < hours24ResponseItems.size(); i++) {
            float val = (float) Math.round(Double.parseDouble(String.valueOf(hours24ResponseItems.get(i).getPrecipitationProbability())));
            values.add(new Entry(i, val));
        }


        // create a dataset and give it a type
        set1 = new LineDataSet(values, "Precipitation Chance (%)");

        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set1.setCubicIntensity(0.1f);
        set1.setDrawFilled(true);
        set1.setDrawCircles(true);
        set1.setLineWidth(1.8f);
        set1.setCircleRadius(1f);
        set1.setCircleColor(getResources().getColor(R.color.chart_color2));
        set1.setHighLightColor(getResources().getColor(R.color.chart_color2));
        set1.setColor(getResources().getColor(R.color.chart_color2));
        set1.setFillColor(getResources().getColor(R.color.chart_color2));
        set1.setFillAlpha(50);
        set1.setDrawHorizontalHighlightIndicator(true);
        set1.setDrawValues(true);

        set1.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return homeBinding.precipitationChart1.getAxisLeft().getAxisMinimum();
            }
        });

        // create a data object with the data sets
        LineData data = new LineData(set1);
        data.setValueTypeface(fontFace);
        data.setValueTextSize(7f);
        data.setDrawValues(false);

        XAxis xAxis = homeBinding.precipitationChart1.getXAxis();
        xAxis.setTextSize(9f);
        xAxis.setTextColor(getResources().getColor(R.color.text_color));
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);

        final ArrayList<String> xAxisLabel = new ArrayList<>();
        for (int i = 0; i < hours24ResponseItems.size(); i++) {
            Hours24ResponseItem hours24ResponseItem = hours24ResponseItems.get(i);
            xAxisLabel.add("" + Math.round(Double.parseDouble(String.valueOf(hours24ResponseItem.getPrecipitationProbability()))) + "%");
        }
        homeBinding.precipitationChart1.getXAxis().setTextSize(9f);
        homeBinding.precipitationChart1.getXAxis().setTextColor(getResources().getColor(R.color.text_color));
        homeBinding.precipitationChart1.getLegend().setTextColor(getResources().getColor(R.color.text_color));
        homeBinding.precipitationChart1.getXAxis().setTypeface(fontFace);
        homeBinding.precipitationChart1.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));

        // set data
        homeBinding.precipitationChart1.setData(data);

        homeBinding.precipitationChart1.animateXY(2000, 2000);
    }

    private void setDailyPrecipitationChartData() {

        ArrayList<Entry> values = new ArrayList<>();
        LineDataSet set1;

        for (int i = 0; i < dailyForecastsItemList.size(); i++) {

            int day; // if day = 0; Day else Night
            DailyForecastsItem days15Response = dailyForecastsItemList.get(i);

            DateTime dtsunset = new DateTime(days15Response.getSun().getSet());
            DateTime dtsunrise = new DateTime(days15Response.getSun().getRise());

            int currentHours = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

            if (currentHours > dtsunrise.getHourOfDay() &&
                    currentHours < dtsunset.getHourOfDay()) {
                day = 0;
            } else {
                day = 1;
            }
            float val;
            if (day == 0) {
                val = (float) Math.round(Double.parseDouble(String.valueOf(days15Response.getDay().getPrecipitationProbability())));
            } else {
                val = (float) Math.round(Double.parseDouble(String.valueOf(days15Response.getNight().getPrecipitationProbability())));
            }
            values.add(new Entry(i, val));
        }


        // create a dataset and give it a type
        set1 = new LineDataSet(values, "Precipitation Chance (%)");

        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set1.setCubicIntensity(0.1f);
        set1.setDrawFilled(true);
        set1.setDrawCircles(true);
        set1.setLineWidth(1.8f);
        set1.setCircleRadius(1f);
        set1.setCircleColor(getResources().getColor(R.color.chart_color2));
        set1.setHighLightColor(getResources().getColor(R.color.chart_color2));
        set1.setColor(getResources().getColor(R.color.chart_color2));
        set1.setFillColor(getResources().getColor(R.color.chart_color2));
        set1.setFillAlpha(50);
        set1.setDrawHorizontalHighlightIndicator(true);
        set1.setDrawValues(true);

        set1.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return homeBinding.dailyPrecipitationChart.getAxisLeft().getAxisMinimum();
            }
        });

        // create a data object with the data sets
        LineData data = new LineData(set1);
        data.setValueTypeface(fontFace);
        data.setValueTextSize(7f);
        data.setDrawValues(false);

        XAxis xAxis = homeBinding.dailyPrecipitationChart.getXAxis();
        xAxis.setTextSize(9f);
        xAxis.setTextColor(getResources().getColor(R.color.text_color));
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);

        final ArrayList<String> xAxisLabel = new ArrayList<>();
        for (int i = 0; i < dailyForecastsItemList.size(); i++) {

            int day; // if day = 0; Day else Night
            DailyForecastsItem days15Response = dailyForecastsItemList.get(i);

            DateTime dtsunset = new DateTime(days15Response.getSun().getSet());
            DateTime dtsunrise = new DateTime(days15Response.getSun().getRise());

            int currentHours = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

            if (currentHours > dtsunrise.getHourOfDay() &&
                    currentHours < dtsunset.getHourOfDay()) {
                day = 0;
            } else {
                day = 1;
            }
            if (day == 0) {
                xAxisLabel.add("" + Math.round(Double.parseDouble(String.valueOf(days15Response.getDay().getPrecipitationProbability()))) + "%");
            } else {
                xAxisLabel.add("" + Math.round(Double.parseDouble(String.valueOf(days15Response.getNight().getPrecipitationProbability()))) + "%");
            }


        }
        homeBinding.dailyPrecipitationChart.getXAxis().setTextSize(9f);
        homeBinding.dailyPrecipitationChart.getXAxis().setTextColor(getResources().getColor(R.color.text_color));
        homeBinding.dailyPrecipitationChart.getLegend().setTextColor(getResources().getColor(R.color.text_color));
        homeBinding.dailyPrecipitationChart.getXAxis().setTypeface(fontFace);
        homeBinding.dailyPrecipitationChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));

        // set data
        homeBinding.dailyPrecipitationChart.setData(data);

        homeBinding.dailyPrecipitationChart.animateXY(2000, 2000);
    }

    private void setTempChartData() {

        ArrayList<Entry> values = new ArrayList<>();
        LineDataSet set1;

        for (int i = 0; i < hours24ResponseItems.size(); i++) {
            float val = (float) Math.round(Double.parseDouble(String.valueOf(hours24ResponseItems.get(i).getTemperature().getValue())));
            values.add(new Entry(i, val));
        }

        // create a dataset and give it a type
        set1 = new LineDataSet(values, "Temperature");

        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set1.setCubicIntensity(0.1f);
        set1.setDrawFilled(true);
        set1.setDrawCircles(true);
        set1.setLineWidth(1.8f);
        set1.setCircleRadius(1f);
        set1.setCircleColor(getResources().getColor(R.color.chart_color1));
        set1.setHighLightColor(getResources().getColor(R.color.chart_color1));
        set1.setColor(getResources().getColor(R.color.chart_color1));
        set1.setFillColor(getResources().getColor(R.color.chart_color1));

        set1.setFillAlpha(50);
        set1.setDrawHorizontalHighlightIndicator(true);
        set1.setDrawValues(true);
        set1.setFillFormatter((dataSet, dataProvider) -> homeBinding.tempHourlyChart1.getAxisLeft().getAxisMinimum());

        // create a data object with the data sets
        LineData data = new LineData(set1);
        data.setValueTypeface(fontFace);
        data.setValueTextSize(7f);
        data.setDrawValues(false);

        XAxis xAxis = homeBinding.tempHourlyChart1.getXAxis();
        xAxis.setTextSize(9f);
        xAxis.setTextColor(getResources().getColor(R.color.text_color));
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);

        final ArrayList<String> xAxisLabel = new ArrayList<>();
        for (int i = 0; i < hours24ResponseItems.size(); i++) {
            Hours24ResponseItem hours24ResponseItem = hours24ResponseItems.get(i);
            xAxisLabel.add("" + Math.round(Double.parseDouble(String.valueOf(hours24ResponseItem.getTemperature().getValue()))) + " " + (char) 0x00B0 + hours24ResponseItem.getTemperature().getUnit());
        }
        homeBinding.tempHourlyChart1.getXAxis().setTextSize(9f);
        homeBinding.tempHourlyChart1.getXAxis().setTextColor(getResources().getColor(R.color.text_color));
        homeBinding.tempHourlyChart1.getLegend().setTextColor(getResources().getColor(R.color.text_color));
        homeBinding.tempHourlyChart1.getXAxis().setTypeface(fontFace);
        homeBinding.tempHourlyChart1.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));

        // set data
        homeBinding.tempHourlyChart1.setData(data);

        homeBinding.tempHourlyChart1.animateXY(2000, 2000);
    }

    private void setChartStyle() {

        homeBinding.tempHourlyChart1.invalidate();
        homeBinding.precipitationChart1.invalidate();
        homeBinding.dailyPrecipitationChart.invalidate();

        homeBinding.tempHourlyChart1.setPinchZoom(false);
        homeBinding.precipitationChart1.setPinchZoom(false);
        homeBinding.dailyPrecipitationChart.setPinchZoom(false);

        // get the legend (only possible after setting data)
        Legend l = homeBinding.tempHourlyChart1.getLegend();
        homeBinding.tempHourlyChart1.getDescription().setEnabled(false);

        Legend l1 = homeBinding.precipitationChart1.getLegend();
        homeBinding.precipitationChart1.getDescription().setEnabled(false);

        Legend l2 = homeBinding.dailyPrecipitationChart.getLegend();
        homeBinding.dailyPrecipitationChart.getDescription().setEnabled(false);

        // modify the legend of temp...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextSize(11f);
        l.setTextColor(Color.WHITE);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        // modify the legend of precipitation ...
        l1.setForm(Legend.LegendForm.CIRCLE);
        l1.setTextSize(11f);
        l1.setTextColor(Color.WHITE);
        l1.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l1.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l1.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l1.setDrawInside(false);

        // modify the legend of dailyPrecipitation ...
        l2.setForm(Legend.LegendForm.LINE);
        l2.setTextSize(11f);
        l2.setTextColor(Color.WHITE);
        l2.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l2.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l2.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l2.setDrawInside(false);

        // temp ...
        YAxis y = homeBinding.tempHourlyChart1.getAxisLeft();
        y.setLabelCount(24, false);
        y.setTextColor(Color.WHITE);
        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        y.setDrawGridLines(false);
        y.setAxisLineColor(Color.TRANSPARENT);

        // precipitation ...
        YAxis y1 = homeBinding.precipitationChart1.getAxisLeft();
        y1.setLabelCount(24, false);
        y1.setTextColor(Color.WHITE);
        y1.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        y1.setDrawGridLines(false);
        y1.setAxisLineColor(Color.TRANSPARENT);

        // dailyPrecipitation ...
        YAxis y2 = homeBinding.dailyPrecipitationChart.getAxisLeft();
        y2.setLabelCount(7, true);
        y2.setTextColor(Color.WHITE);
        y2.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        y2.setDrawGridLines(false);
        y2.setAxisLineColor(Color.TRANSPARENT);

        // temp ...
        XAxis xAxis = homeBinding.tempHourlyChart1.getXAxis();
        xAxis.setLabelCount(24, false);
        xAxis.setTextSize(11f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setAxisLineColor(Color.TRANSPARENT);

        // precipitation ...
        XAxis xAxis1 = homeBinding.precipitationChart1.getXAxis();
        xAxis1.setLabelCount(24, false);
        xAxis1.setTextSize(11f);
        xAxis1.setTextColor(Color.WHITE);
        xAxis1.setDrawGridLines(false);
        xAxis1.setDrawAxisLine(false);
        xAxis1.setAxisLineColor(Color.TRANSPARENT);

        // dailyPrecipitation ...
        XAxis xAxis2 = homeBinding.dailyPrecipitationChart.getXAxis();
        xAxis2.setLabelCount(7, true);
        xAxis2.setTextSize(11f);
        xAxis2.setTextColor(Color.WHITE);
        xAxis2.setDrawGridLines(false);
        xAxis2.setDrawAxisLine(false);
        xAxis2.setAxisLineColor(Color.TRANSPARENT);

        // temp ...
        YAxis leftAxis = homeBinding.tempHourlyChart1.getAxisLeft();
        leftAxis.setTextColor(Color.TRANSPARENT);
        if (Preference.getUnit(requireActivity()).equalsIgnoreCase("Metric")) {
            leftAxis.setAxisMaximum(100f);
            leftAxis.setAxisMinimum(0f);
        } else {
            leftAxis.setAxisMaximum(212f);
            leftAxis.setAxisMinimum(32f);
        }
        leftAxis.setDrawGridLines(false);
        leftAxis.setGranularityEnabled(false);

        // precipitation ...
        YAxis leftAxis1 = homeBinding.precipitationChart1.getAxisLeft();
        leftAxis1.setTextColor(Color.TRANSPARENT);
        leftAxis1.setAxisMaximum(100f);
        leftAxis1.setAxisMinimum(0f);
        leftAxis1.setDrawGridLines(false);
        leftAxis1.setGranularityEnabled(false);

        // dailyPrecipitation ...
        YAxis leftAxis2 = homeBinding.dailyPrecipitationChart.getAxisLeft();
        leftAxis2.setTextColor(Color.TRANSPARENT);
        leftAxis2.setAxisMaximum(100f);
        leftAxis2.setAxisMinimum(0f);
        leftAxis2.setDrawGridLines(false);
        leftAxis2.setGranularityEnabled(false);

        // temp ...
        YAxis rightAxis = homeBinding.tempHourlyChart1.getAxisRight();
        rightAxis.setTextColor(Color.TRANSPARENT);
        if (Preference.getUnit(requireContext()).equalsIgnoreCase("Metric")) {
            rightAxis.setAxisMaximum(100f);
            rightAxis.setAxisMinimum(0f);
        } else {
            rightAxis.setAxisMaximum(212f);
            rightAxis.setAxisMinimum(32f);
        }
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawZeroLine(false);
        rightAxis.setGranularityEnabled(false);

        // precipitation ...
        YAxis rightAxis1 = homeBinding.precipitationChart1.getAxisRight();
        rightAxis1.setTextColor(Color.TRANSPARENT);
        rightAxis1.setAxisMaximum(100f);
        rightAxis1.setAxisMinimum(0f);
        rightAxis1.setDrawGridLines(false);
        rightAxis1.setDrawZeroLine(false);
        rightAxis1.setGranularityEnabled(false);

        // dailyPrecipitation ...
        YAxis rightAxis2 = homeBinding.dailyPrecipitationChart.getAxisRight();
        rightAxis2.setTextColor(Color.TRANSPARENT);
        rightAxis2.setAxisMaximum(100f);
        rightAxis2.setAxisMinimum(0f);
        rightAxis2.setDrawGridLines(false);
        rightAxis2.setDrawZeroLine(false);
        rightAxis2.setGranularityEnabled(false);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (requireContext() != null) {
            if (Preference.getKey(requireContext()).equals("")) {
                Preference.setLatitude(requireContext(), String.valueOf(location.getLatitude()));
                Preference.setLongitude(requireContext(), String.valueOf(location.getLongitude()));
                new getKey(location.getLatitude(), location.getLongitude()).execute();
            } else {
                Const.KEY = Preference.getKey(requireContext());
                ArrayList<AddLocationData> addLocationDataArrayList;
                if (!Preference.getKey(requireContext()).equals("")) {
                    Const.KEY = Preference.getKey(requireContext());
                    new getCurrentData().execute();
                    addLocationDataArrayList = new Gson().fromJson(Preference.getDefaultLocationListInfo(requireContext()), type);
                    AddLocationData addLocationData = new AddLocationData();
                    addLocationData.setKey(Const.KEY);
                    homeBinding.setMatrixType(Preference.getUnit(requireContext()));
                    if (addLocationDataArrayList.contains(addLocationData)) {
                        if (addLocationDataArrayList.indexOf(addLocationData) != -1) {
                            int pos = addLocationDataArrayList.indexOf(addLocationData);
                            address = addLocationDataArrayList.get(pos).getCity() + ", " + addLocationDataArrayList.get(pos).getCountry();
                            Const.KEY = Preference.getKey(requireContext());
                            new getCurrentData().execute();
                        }
                    }
                }
            }
        }

        if (location != null) {
            mLocManager.removeUpdates(this);
        }
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Toast.makeText(requireContext(), "Gps Disabled", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());

        homeBinding.tempHourlyChart1.centerViewToAnimated(e.getX(), e.getY(), homeBinding.tempHourlyChart1.getData().getDataSetByIndex(h.getDataSetIndex())
                .getAxisDependency(), 500);
        homeBinding.precipitationChart1.centerViewToAnimated(e.getX(), e.getY(), homeBinding.precipitationChart1.getData().getDataSetByIndex(h.getDataSetIndex())
                .getAxisDependency(), 500);
        homeBinding.dailyPrecipitationChart.centerViewToAnimated(e.getX(), e.getY(), homeBinding.dailyPrecipitationChart.getData().getDataSetByIndex(h.getDataSetIndex())
                .getAxisDependency(), 500);
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

    public String getAddress(double lat, double lng) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);

                StringBuilder strReturnedAddress = new StringBuilder();

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                city = returnedAddress.getLocality();
                area = strReturnedAddress.toString();
                country = returnedAddress.getCountryName();

                strAdd = returnedAddress.getLocality() + ", " + returnedAddress.getCountryName();

            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strAdd;
    }

    private boolean getDay15Data(String KEY) {

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
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                if (homeBinding.rlLoading.getVisibility() == View.VISIBLE) {
                                    homeBinding.rlLoading.setVisibility(View.GONE);
                                    homeBinding.svMain.setVisibility(View.VISIBLE);
                                }
                            });
                        }

                        dailyForecastsItemList = new ArrayList<>();
                        Days15Response days15Response = new Gson().fromJson(response.toString(), Days15Response.class);

                        for (int i = 1; i < 8; i++) {
                            dailyForecastsItemList.add(days15Response.getDailyForecasts().get(i));
                        }
                        isDay15Called = true;

                        // set Daily temperature data to adapter

                        tempIconAdapter = new DailyTempIconAdapter(dailyForecastsItemList, getActivity());
                        homeBinding.rvDailyTemp.setAdapter(tempIconAdapter);

                        LinearLayout lout_bar;
                        ImageView ivBar;
                        TextView tvMinChart;
                        TextView tvMaxChart;
                        TextView tvDay;

                        int[] minarrayt = new int[7];
                        int[] maxarray = new int[7];

                        for (int i = 0; i < dailyForecastsItemList.size(); i++) {
                            minarrayt[i] = Math.round(Float.parseFloat(String.valueOf(dailyForecastsItemList.get(i).getTemperature().getMinimum().getValue())));
                            maxarray[i] = Math.round(Float.parseFloat(String.valueOf(dailyForecastsItemList.get(i).getTemperature().getMaximum().getValue())));
                        }

                        int maxtemp = Const.getMaxValue(maxarray);
                        int mintemp = Const.getMinValue(minarrayt);

                        homeBinding.listDailyChart.removeAllViews();
                        for (int i = 0; i < dailyForecastsItemList.size(); i++) {

                            View layout2 = LayoutInflater.from(requireContext()).inflate(R.layout.list_item_graph_daily, homeBinding.listDailyChart, false);
                            lout_bar = layout2.findViewById(R.id.lout_bar);
                            ivBar = layout2.findViewById(R.id.ivBar);
                            tvMinChart = layout2.findViewById(R.id.tvMinChart);
                            tvMaxChart = layout2.findViewById(R.id.tvMaxChart);
                            tvDay = layout2.findViewById(R.id.tvDay);
                            tvMinChart.setText("" + Math.round(Float.parseFloat(String.valueOf(dailyForecastsItemList.get(i).getTemperature().getMinimum().getValue()))) + "" + "" + (char) 0x00B0);
                            tvMaxChart.setText("" + Math.round(Float.parseFloat(String.valueOf(dailyForecastsItemList.get(i).getTemperature().getMaximum().getValue()))) + "" + "" + (char) 0x00B0);


                            DateTime dt = new DateTime(dailyForecastsItemList.get(i).getDate());
                            String date1 = "";
                            date1 = weekOfDay[dt.getDayOfWeek() - 1] + " " + dt.getDayOfMonth() + "," + " " + dt.getYear();

                            String[] date = date1.split(" ");
                            tvDay.setText("" + date[0].substring(0, 1).toUpperCase() + "" + date[0].substring(1, 3).toLowerCase());
                            tvMaxChart.measure(0, 0);
                            tvMinChart.measure(0, 0);

                            int heighttext = (tvMaxChart.getMeasuredHeight()) + (tvMinChart.getMeasuredHeight());
                            int height = (requireContext().getResources().getDimensionPixelSize(R.dimen.loutbar)) - (heighttext);

                            int maindiff = (maxtemp) - (mintemp);
                            int gap = Math.round(height / maindiff);
                            int eachDiff = Math.round(Float.parseFloat(String.valueOf(dailyForecastsItemList.get(i).getTemperature().getMaximum().getValue())) - Math.round(Float.parseFloat(String.valueOf(dailyForecastsItemList.get(i).getTemperature().getMinimum().getValue()))));
                            int newheight = (eachDiff * gap);

                            lout_bar.getLayoutParams().height = (requireContext().getResources().getDimensionPixelSize(R.dimen.loutbar));
                            lout_bar.requestLayout();
                            ivBar.getLayoutParams().height = newheight;
                            ivBar.requestLayout();

                            ivBar.setBackground(requireContext().getResources().getDrawable(R.drawable.grediant_line));

                            LinearLayout.LayoutParams childParam2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
                            childParam2.weight = 1f;
                            layout2.setLayoutParams(childParam2);
                            layout2.requestLayout();

                            homeBinding.listDailyChart.addView(layout2);

                        }

                        // set Daily Precipitation data to chart
                        setDailyPrecipitationChartData();
                        homeBinding.dailyPrecipitationChart.notifyDataSetChanged();
                        homeBinding.dailyPrecipitationChart.invalidate();

                        dailyWindAdapter = new DailyWindAdapter(dailyForecastsItemList, getActivity());
                        homeBinding.rvDailyWind.setAdapter(dailyWindAdapter);

                        getActivity().runOnUiThread(() -> {

//                            activityMainBinding.rlLoading.setVisibility(View.GONE);
//                            activityMainBinding.svMain.setVisibility(View.VISIBLE);

                           /* mAppUpdateManager = AppUpdateManagerFactory.create(requireContext());

                            mAppUpdateManager.registerListener(installStateUpdatedListener);

                            mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {

                                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE *//*AppUpdateType.IMMEDIATE*//*)) {

                                    try {
                                        mAppUpdateManager.startUpdateFlowForResult(
                                                appUpdateInfo, AppUpdateType.FLEXIBLE *//*AppUpdateType.IMMEDIATE*//*, requireContext(), RC_APP_UPDATE);
                                        Log.e("LLL_Update_App: ", "Update available");
                                    } catch (IntentSender.SendIntentException e) {
                                        e.printStackTrace();
                                        Log.e("LLL_Update_App: ", e.getMessage());
                                    }

                                } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                                    //CHECK THIS if AppUpdateType.FLEXIBLE, otherwise you can skip
                                    popupSnackbarForCompleteUpdate();
                                } else {
                                    Log.e("LLL_Update_App: ", "checkForAppUpdateAvailability: something else");
                                }
                            });*/
                        });

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("LLL_Days15_Error: ", anError.getErrorBody());
                    }
                });
        return isDay15Called;
    }

    private void get24HoursData() {

        hours24ResponseItems = new ArrayList<>();
        boolean isMatrix = true;
        if (!Preference.getUnit(requireContext()).equalsIgnoreCase("Metric")) {
            isMatrix = false;
        }
        AndroidNetworking.get("http://api.accuweather.com/forecasts/v1/hourly/24hour/" + Const.KEY + ".json")
                .addQueryParameter("apikey", "srRLeAmTroxPinDG8Aus3Ikl6tLGJd94")
                .addQueryParameter("language", "en-gb")
                .addQueryParameter("details", "true")
                .addQueryParameter("metric", String.valueOf(isMatrix))
                .setPriority(Priority.IMMEDIATE)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        hours24ResponseItems.clear();
                        try {
                            if (response.length() > 0) {

                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject jsonObject = response.getJSONObject(i);
                                    Hours24ResponseItem newResponse = new Gson().fromJson(jsonObject.toString(), Hours24ResponseItem.class);
                                    hours24ResponseItems.add(newResponse);
                                }

                                // set temperature data on chart
                                setTempChartData();
                                homeBinding.tempHourlyChart1.notifyDataSetChanged();
                                homeBinding.tempHourlyChart1.invalidate();

                                // set precipitation data on chart
                                setPrecipitationChartData();
                                homeBinding.precipitationChart1.notifyDataSetChanged();
                                homeBinding.precipitationChart1.invalidate();

                                // set Wind data to adapter
                                hourlyWindAdapter = new HourlyWindAdapter(hours24ResponseItems, getActivity());
                                homeBinding.rvHourlyWind.setAdapter(hourlyWindAdapter);

                                new getDays15().execute();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }

    private String getKey(double latitude, double longitude) {
        AndroidNetworking.get("https://api.accuweather.com/locations/v1/cities/geoposition/search.json")
                .addQueryParameter("apikey", "d7e795ae6a0d44aaa8abb1a0a7ac19e4")
                .addQueryParameter("q", latitude + "," + longitude)
                .addQueryParameter("language", "en-gb")
                .addQueryParameter("details", "true")
                .addQueryParameter("toplevel", "false")
                .setPriority(Priority.IMMEDIATE)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String key = response.getString("Key");
                            mainKey = key;
                            Const.KEY = key;
                            Preference.setKey(requireContext(), Const.KEY);

                            Calendar calendar = Calendar.getInstance();
                            new AlarmReceiver().setRepeatAlarm(requireContext(), 1001, calendar);

                            address = getAddress(latitude,
                                    longitude);

                            LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(requireContext());
                            Intent localIn;
                            localIn = new Intent("REFRESH");
                            localIn.putExtra("key", mainKey);
                            lbm.sendBroadcast(localIn);

                            ArrayList<AddLocationData> addLocationDataArrayList;
                            if (!Preference.getDefaultLocationListInfo(requireContext()).equals("")) {
                                addLocationDataArrayList = new Gson().fromJson(Preference.getDefaultLocationListInfo(requireContext()), type);
                            } else {
                                addLocationDataArrayList = new ArrayList<>();
                            }
                            AddLocationData addLocationData = new AddLocationData();
                            addLocationData.setKey(mainKey);
                            addLocationData.setCity(city);
                            addLocationData.setCountry(country);
                            addLocationData.setArea(area);
                            addLocationData.setDaynight(0);
                            if (addLocationDataArrayList.size() > 0) {
                                addLocationDataArrayList.remove(0);
                                addLocationDataArrayList.add(0, addLocationData);
                            } else {
                                addLocationDataArrayList.add(addLocationData);
                            }
                            Preference.setDefaultLocationInfo(requireContext(), new Gson().toJson(addLocationDataArrayList));
                            LocalBroadcastManager lbm1 = LocalBroadcastManager.getInstance(requireContext());
                            Intent localIn1;
                            localIn1 = new Intent("REFRESH_LOCATION");
                            lbm1.sendBroadcast(localIn1);
                            new getCurrentData().execute();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("LLL_GetKey_Error: ", anError.getErrorBody());
                    }
                });
        return mainKey;
    }

    private void getCurrentData() {
        AndroidNetworking.get("https://api.accuweather.com/currentconditions/v1/" + Const.KEY + ".json")
                .addQueryParameter("apikey", "srRLeAmTroxPinDG8Aus3Ikl6tLGJd94")
                .addQueryParameter("language", "en-gb")
                .addQueryParameter("details", "true")
                .addQueryParameter("getphotos", "false")
                .setPriority(Priority.IMMEDIATE)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            if (response.length() > 0) {

                                JSONObject jsonObject = response.getJSONObject(0);
                                CurrentDataResponseItem newResponse = new Gson().fromJson(jsonObject.toString(), CurrentDataResponseItem.class);

                                homeBinding.setCurrentdata(newResponse);
                                homeBinding.setCityName(address);

                                DateTime dt = new DateTime(newResponse.getLocalObservationDateTime());
                                String dateString = DateFormat.format("MMMM dd, yyyy", dt.toDate()).toString();

                                homeBinding.setTodayData(dateString);

                                int WeatherIcon = newResponse.getWeatherIcon();
                                homeBinding.imgCurrentIcon.setImageResource(Const.weatherIcon[WeatherIcon - 1]);

                               /* if (WeatherIcon.equals("6") || WeatherIcon.equals("7") || WeatherIcon.equals("8") || WeatherIcon.equals("12") || WeatherIcon.equals("13") || WeatherIcon.equals("14") || WeatherIcon.equals("15") || WeatherIcon.equals("16") || WeatherIcon.equals("17") ||
                                        WeatherIcon.equals("18") || WeatherIcon.equals("19") || WeatherIcon.equals("20") || WeatherIcon.equals("21") || WeatherIcon.equals("26") || WeatherIcon.equals("38") || WeatherIcon.equals("39") || WeatherIcon.equals("40") || WeatherIcon.equals("41") || WeatherIcon.equals("42")) {
                                    activityMainBinding.imgIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_monsson_1));
                                    activityMainBinding.rlBg.setBackground(getResources().getDrawable(R.drawable.ic_monsson_bg));
                                } else if (WeatherIcon.equals("11") || WeatherIcon.equals("22") || WeatherIcon.equals("23") || WeatherIcon.equals("24") || WeatherIcon.equals("25") || WeatherIcon.equals("26") || WeatherIcon.equals("29") || WeatherIcon.equals("32") || WeatherIcon.equals("42") || WeatherIcon.equals("43") ||
                                        WeatherIcon.equals("44") || WeatherIcon.equals("31") || WeatherIcon.equals("25") || WeatherIcon.equals("29") || WeatherIcon.equals("24") || WeatherIcon.equals("22") || WeatherIcon.equals("23")) {
                                    activityMainBinding.imgIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_winter));
                                    activityMainBinding.rlBg.setBackground(getResources().getDrawable(R.drawable.ic_winter_bg));
                                } else {
                                    activityMainBinding.imgIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_summar));
                                    activityMainBinding.rlBg.setBackground(getResources().getDrawable(R.drawable.ic_sunny_bg));
                                }*/

                            }

                            if (Preference.getValidAlarm(requireContext()).equals("")) {
                                updateAllWidgets(requireContext(), new Intent(requireContext(), WeatherWidget.class));
                                Preference.setValidAlarm(requireContext(), "1");
                            }


                            new getHours24Data().execute();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("LLL_Current_Data: ", anError.getErrorBody());
                    }
                });
    }

    private class RefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (intent.getBooleanExtra("isLocationUpdate", false)) {
                    address = intent.getStringExtra("name");
                    mainKey = intent.getStringExtra("key");
                    Const.KEY = intent.getStringExtra("key");
                    new getCurrentData().execute();
                } else {
                    if (isAdded()) {
                        setChartStyle();

                        ArrayList<AddLocationData> addLocationDataArrayList;
                        if (!Preference.getKey(requireContext()).equals("")) {
                            Const.KEY = Preference.getKey(requireContext());
                            new getCurrentData().execute();
                            addLocationDataArrayList = new Gson().fromJson(Preference.getDefaultLocationListInfo(requireContext()), type);
                            AddLocationData addLocationData = new AddLocationData();
                            addLocationData.setKey(Const.KEY);
                            homeBinding.setMatrixType(Preference.getUnit(requireContext()));
                            if (addLocationDataArrayList.contains(addLocationData)) {
                                if (addLocationDataArrayList.indexOf(addLocationData) != -1) {
                                    int pos = addLocationDataArrayList.indexOf(addLocationData);
                                    address = addLocationDataArrayList.get(pos).getCity() + ", " + addLocationDataArrayList.get(pos).getCountry();
                                    Const.KEY = Preference.getKey(requireContext());
                                    new getCurrentData().execute();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private final class getCurrentData extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            getCurrentData();
            return "";
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }

    private final class getKey extends AsyncTask<Void, Void, String> {

        double latitude, longitude;

        public getKey(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;

            getActivity().runOnUiThread(() -> {
                homeBinding.rlLoading.setVisibility(View.VISIBLE);
                homeBinding.svMain.setVisibility(View.GONE);
            });
        }

        @Override
        protected String doInBackground(Void... params) {
            Const.KEY = getKey(latitude, longitude);
            return Const.KEY;
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }

    private final class getHours24Data extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            get24HoursData();
            return "";
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }

    private final class getDays15 extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            isDay15Called = getDay15Data(Const.KEY);
            if (isDay15Called)
                return "Executed";
            else
                return "";
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }

}