package com.darksky.weather.today.weatherforecast.newlightdarkweather.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.darksky.weather.today.weatherforecast.newlightdarkweather.R;
import com.darksky.weather.today.weatherforecast.newlightdarkweather.Util.Const;
import com.darksky.weather.today.weatherforecast.newlightdarkweather.databinding.ListDailyTempIconBinding;
import com.darksky.weather.today.weatherforecast.newlightdarkweather.model.days15.DailyForecastsItem;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.List;

import static com.darksky.weather.today.weatherforecast.newlightdarkweather.application.MainApplication.requireContext;

public class DailyTempIconAdapter extends RecyclerView.Adapter<DailyTempIconAdapter.MyClassView> {

    List<DailyForecastsItem> days15Responses;
    Activity activity;

    String[] weekOfDay = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

    public DailyTempIconAdapter(List<DailyForecastsItem> days15Responses, Activity activity) {
        this.days15Responses = days15Responses;
        this.activity = activity;
    }

    @NonNull
    @NotNull
    @Override
    public MyClassView onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ListDailyTempIconBinding tempIconBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.list_daily_temp_icon, parent, false);
        return new MyClassView(tempIconBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyClassView holder, int position) {
        int day; // if day = 0; Day else Night
        DailyForecastsItem days15Response = days15Responses.get(position);

        DateTime dtsunset = new DateTime(days15Response.getSun().getSet());
        DateTime dtsunrise = new DateTime(days15Response.getSun().getRise());

        int currentHours = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int currentMin = Calendar.getInstance().get(Calendar.MINUTE);

        if (currentHours > dtsunrise.getHourOfDay() &&
                currentHours < dtsunset.getHourOfDay()) {
            day = 0;
        } else  {
            day = 1;
        }

        if (day == 0) {
            holder.tempIconBinding.imgIcon.setImageResource(Const.weatherIcon[days15Response.getDay().getIcon() - 1]);
        } else {
            holder.tempIconBinding.imgIcon.setImageResource(Const.weatherIcon[days15Response.getNight().getIcon() - 1]);
        }
        DateTime dt = new DateTime(days15Response.getDate());
        String date = "";
        date = weekOfDay[dt.getDayOfWeek() - 1];
        holder.tempIconBinding.tvWind.setText(date);
    }

    @Override
    public int getItemCount() {
        return days15Responses.size();
    }

    public class MyClassView extends RecyclerView.ViewHolder {

        private final ListDailyTempIconBinding tempIconBinding;

        public MyClassView(ListDailyTempIconBinding tempIconBinding) {
            super(tempIconBinding.getRoot());
            this.tempIconBinding = tempIconBinding;
        }
    }
}
