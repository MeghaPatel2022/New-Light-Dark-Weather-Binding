package com.darksky.weather.today.weatherforecast.newlightdarkweather.model.currentdata;

import com.google.gson.annotations.SerializedName;

public class Past6HourRange {

    @SerializedName("Minimum")
    private Minimum minimum;

    @SerializedName("Maximum")
    private Maximum maximum;

    public Minimum getMinimum() {
        return minimum;
    }

    public void setMinimum(Minimum minimum) {
        this.minimum = minimum;
    }

    public Maximum getMaximum() {
        return maximum;
    }

    public void setMaximum(Maximum maximum) {
        this.maximum = maximum;
    }

    @Override
    public String toString() {
        return
                "Past6HourRange{" +
                        "minimum = '" + minimum + '\'' +
                        ",maximum = '" + maximum + '\'' +
                        "}";
    }
}