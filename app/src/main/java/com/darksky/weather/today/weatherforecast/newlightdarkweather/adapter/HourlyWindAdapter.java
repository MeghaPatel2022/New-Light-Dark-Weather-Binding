package com.darksky.weather.today.weatherforecast.newlightdarkweather.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.darksky.weather.today.weatherforecast.newlightdarkweather.R;
import com.darksky.weather.today.weatherforecast.newlightdarkweather.databinding.ListWindBinding;
import com.darksky.weather.today.weatherforecast.newlightdarkweather.model.days15.DailyForecastsItem;
import com.darksky.weather.today.weatherforecast.newlightdarkweather.model.hours24.Hours24ResponseItem;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.darksky.weather.today.weatherforecast.newlightdarkweather.application.MainApplication.requireContext;

public class HourlyWindAdapter extends RecyclerView.Adapter<HourlyWindAdapter.MyClassView> {

    List<Hours24ResponseItem> hours24ResponseItems;
    Activity activity;

    public HourlyWindAdapter(List<Hours24ResponseItem> hours24ResponseItems, Activity activity) {
        this.hours24ResponseItems = hours24ResponseItems;
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
    public void onBindViewHolder(@NonNull @NotNull MyClassView holder, int position)    {
        holder.windBinding.tvWind.setText(String.valueOf(hours24ResponseItems.get(position).getWind().getSpeed().getValue()));
    }

    @Override
    public int getItemCount() {
        return hours24ResponseItems.size();
    }

    public class MyClassView extends RecyclerView.ViewHolder {
        private final ListWindBinding windBinding;
        public MyClassView(ListWindBinding windBinding) {
            super(windBinding.getRoot());
            this.windBinding = windBinding;
        }
    }
}
