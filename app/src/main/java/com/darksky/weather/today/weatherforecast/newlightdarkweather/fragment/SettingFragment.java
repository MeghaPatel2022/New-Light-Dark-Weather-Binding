package com.darksky.weather.today.weatherforecast.newlightdarkweather.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.airbnb.lottie.LottieAnimationView;
import com.darksky.weather.today.weatherforecast.newlightdarkweather.AboutUsActivity;
import com.darksky.weather.today.weatherforecast.newlightdarkweather.MainActivity;
import com.darksky.weather.today.weatherforecast.newlightdarkweather.PrivacyPolicyActivity;
import com.darksky.weather.today.weatherforecast.newlightdarkweather.R;
import com.darksky.weather.today.weatherforecast.newlightdarkweather.databinding.FragmentSettingBinding;
import com.darksky.weather.today.weatherforecast.newlightdarkweather.sharedpreference.Preference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {

    MyClickHandlers myClickHandlers;
    Dialog dial1;
    Dialog dialog;
    private FragmentSettingBinding settingBinding;

    public SettingFragment() {
        // Required empty public constructor
    }

    public static SettingFragment newInstance() {
        SettingFragment fragment = new SettingFragment();
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
        settingBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false);

        myClickHandlers = new MyClickHandlers(requireContext());
        settingBinding.setOnClick(myClickHandlers);

        int nightModeFlags = getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;

        if (nightModeFlags != Configuration.UI_MODE_NIGHT_YES) {
            settingBinding.imgGrediant.setVisibility(View.GONE);
        } else {
            settingBinding.imgGrediant.setVisibility(View.VISIBLE);
        }

        settingBinding.imgTemp.setSelected(Preference.getUnit(requireContext()).equals("Metric"));

        settingBinding.imgNotification.setSelected(Preference.getNotification(requireContext()).equals(""));

        return settingBinding.getRoot();
    }

    public void sendEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
                Uri.parse("mailto:" + Uri.encode(getResources().getString(R.string.email))));

        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send email via..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(requireContext(),
                    "There are no email clients installed.", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void shareTextUrl() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        String url = "https://play.google.com/store/apps/details?id="
                + requireContext().getPackageName() + "";

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
        share.putExtra(Intent.EXTRA_TEXT, url);

        startActivity(Intent.createChooser(share, "Share link!"));
    }

    private void rateUs() {
        dial1 = new Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dial1.requestWindowFeature(1);
        dial1.setContentView(R.layout.dialige_rate_us);
        dial1.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dial1.setCanceledOnTouchOutside(true);

        LottieAnimationView animationView = dial1.findViewById(R.id.animationView);
        animationView.playAnimation();

        dial1.findViewById(R.id.yes).setOnClickListener(view -> {
            dial1.dismiss();
            /* This code assumes you are inside an activity */
            final Uri uri = Uri.parse("market://details?id=" + requireContext().getPackageName());
            final Intent rateAppIntent = new Intent(Intent.ACTION_VIEW, uri);

            if (requireContext().getPackageManager().queryIntentActivities(rateAppIntent, 0).size() > 0) {
                startActivity(rateAppIntent);
            }
        });
        dial1.findViewById(R.id.no).setOnClickListener(view -> dial1.dismiss());

        dial1.show();
    }

    private void showPopUpDialog() {
        String[] snooze_time = {"Light", "Dark"};
        dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_rx_refill);
        dialog.getWindow().setLayout(getResources().getDisplayMetrics().widthPixels * 90 / 100, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        RadioGroup radioGroup = dialog.findViewById(R.id.radioGroup);

        for (int i = 0; i < snooze_time.length; i++) {
            String account = snooze_time[i];
            String possibleEmail = account;
            RadioButton radioButton = new RadioButton(requireContext());
            radioButton.setPadding(30, 30, 7, 30);
            radioButton.setText(possibleEmail);
            radioButton.setId(i);

            radioGroup.addView(radioButton);
            if (possibleEmail.equals(Preference.getTheme(requireContext()))) {
                radioButton.setChecked(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    radioButton.setButtonTintList(getResources().getColorStateList(R.color.purple_700));
                    radioButton.setTextColor(getResources().getColorStateList(R.color.purple_700));
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    radioButton.setButtonTintList(getResources().getColorStateList(R.color.text_color1));
                    radioButton.setTextColor(getResources().getColorStateList(R.color.text_color1));
                }
            }
        }

        //set listener to radio button group
        radioGroup.setOnCheckedChangeListener((RadioGroup.OnCheckedChangeListener) (group, checkedId) -> {
            int checkedRadioButtonId = group.getCheckedRadioButtonId();
            RadioButton radioBtn = (RadioButton) dialog.findViewById(checkedRadioButtonId);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                radioBtn.setButtonTintList(getResources().getColorStateList(R.color.purple_700));
                radioBtn.setTextColor(getResources().getColorStateList(R.color.purple_700));
            }
            Preference.setTheme(requireContext(), String.valueOf(radioBtn.getText()));
            if (radioBtn.getText().toString().equals("Light")) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else if (radioBtn.getText().toString().equals("Dark")) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }

            dialog.dismiss();

            Intent intent = new Intent(requireActivity(), MainActivity.class);
            startActivity(intent);
            requireActivity().finish();

        });

        dialog.show();
    }


    public class MyClickHandlers {
        Context context;

        public MyClickHandlers(Context context) {
            this.context = context;
        }

        public void onUnitBtnClicked(View view) {
            if (Preference.getUnit(requireContext()).equalsIgnoreCase("Metric")) {
                Preference.setUnit(requireContext(), "Imperial");
                settingBinding.imgTemp.setSelected(false);
            } else {
                Preference.setUnit(requireContext(), "Metric");
                settingBinding.imgTemp.setSelected(true);
            }
            LocalBroadcastManager lbm2 = LocalBroadcastManager.getInstance(requireContext());
            Intent localIn2;
            localIn2 = new Intent("REFRESH_VIEW");
            lbm2.sendBroadcast(localIn2);

            LocalBroadcastManager lbm1 = LocalBroadcastManager.getInstance(requireContext());
            Intent localIn1;
            localIn1 = new Intent("REFRESH_HOME");
            localIn1.putExtra("isLocationUpdate", false);
            lbm1.sendBroadcast(localIn1);

            LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(requireContext());
            Intent localIn;
            localIn = new Intent("REFRESH");
            localIn.putExtra("key", Preference.getKey(requireContext()));
            lbm.sendBroadcast(localIn);
        }

        public void onNotificationBtnClick(View view) {
            if (Preference.getNotification(requireContext()).equals("1")) {
                Preference.setNotification(requireContext(), "");
                settingBinding.imgNotification.setSelected(true);
            } else {
                Preference.setNotification(requireContext(), "1");
                settingBinding.imgNotification.setSelected(false);
            }
        }

        public void shareAppBtnClick(View v) {
            shareTextUrl();
        }

        public void rateAppBtnClick(View view) {
            rateUs();
        }

        public void feedbackBtnClick(View view) {
            sendEmail();
        }

        public void aboutUsBtnClick(View view) {
            Intent intent = new Intent(requireContext(), AboutUsActivity.class);
            startActivity(intent);
        }

        public void privacyPolicy(View view) {
            Intent intent = new Intent(requireContext(), PrivacyPolicyActivity.class);
            startActivity(intent);
        }

        public void themeBtnClick(View view) {
            showPopUpDialog();
        }

    }
}