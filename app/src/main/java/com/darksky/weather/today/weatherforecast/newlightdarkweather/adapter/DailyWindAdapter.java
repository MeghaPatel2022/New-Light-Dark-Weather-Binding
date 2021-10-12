package com.darksky.weather.today.weatherforecast.newlightdarkweather.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.darksky.weather.today.weatherforecast.newlightdarkweather.R;
import com.darksky.weather.today.weatherforecast.newlightdarkweather.databinding.ListWindBinding;
import com.darksky.weather.today.weatherforecast.newlightdarkweather.model.days15.DailyForecastsItem;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.List;

import static com.darksky.weather.today.weatherforecast.newlightdarkweather.application.MainApplication.requireContext;

public class DailyWindAdapter extends RecyclerView.Adapter<DailyWindAdapter.MyClassView> {

    List<DailyForecastsItem> days15Responses;
    Activity activity;

    public DailyWindAdapter(List<DailyForecastsItem> days15Responses, Activity activity) {
        this.days15Responses = days15Responses;
        this.activity = activity;
    }

    @NonNull
    @NotNull
    @Override
    public MyClassView onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ListWindBinding windBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.list_wind,parent,false);
        return new MyClassView(windBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyClassView holder, int position) {
        DailyForecastsItem days15Response = days15Responses.get(position);

        DateTime dtsunset = new DateTime(days15Response.getSun().getSet());
        DateTime dtsunrise = new DateTime(days15Response.getSun().getRise());

        int currentHours = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        if (currentHours > dtsunrise.getHourOfDay() &&
                currentHours < dtsunset.getHourOfDay()) {
            holder.windBinding.tvWind.setText(String.valueOf(days15Responses.get(position).getDay().getWind().getSpeed().getValue()));
        } else  {
            holder.windBinding.tvWind.setText(String.valueOf(days15Responses.get(position).getNight().getWind().getSpeed().getValue()));
        }
    }

    @Override
    public int getItemCount() {
        return days15Responses.size();
    }

    public class MyClassView extends RecyclerView.ViewHolder {
        private final ListWindBinding windBinding;
        public MyClassView(ListWindBinding windBinding) {
            super(windBinding.getRoot());
            this.windBinding = windBinding;
        }
    }
}
