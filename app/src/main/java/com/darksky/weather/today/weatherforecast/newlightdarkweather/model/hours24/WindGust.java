package com.darksky.weather.today.weatherforecast.newlightdarkweather.model.hours24;

import com.google.gson.annotations.SerializedName;

public class WindGust {

    @SerializedName("Speed")
    private Speed speed;

    public Speed getSpeed() {
        return speed;
    }

    public void setSpeed(Speed speed) {
        this.speed = speed;
    }

    @Override
    public String toString() {
        return
                "WindGust{" +
                        "speed = '" + speed + '\'' +
                        "}";
    }
}