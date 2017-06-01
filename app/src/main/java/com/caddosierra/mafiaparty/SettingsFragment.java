package com.caddosierra.mafiaparty;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;

public class SettingsFragment extends PreferenceFragment {
    private int easterClicks = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String appPackageName = getActivity().getPackageName();
        final String versionName = BuildConfig.VERSION_NAME;

        addPreferencesFromResource(R.xml.preferences);

        Preference appVersion = findPreference("pref_app_version");
        appVersion.setSummary(versionName);

        appVersion.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                easterClicks++;

                if(easterClicks == 7) {
                    Toast.makeText(getActivity().getApplicationContext(), "Nothing happens when you tap the version number 7 times... Sorry!", Toast.LENGTH_LONG).show();
                    easterClicks = 0;
                }

                return true;
            }
        });

        Preference leaveRating = findPreference("pref_leave_rating");

        leaveRating.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                return true;
            }
        });

//        Preference button = findPreference("pref_donate");
//
//        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            @Override
//            public boolean onPreferenceClick(Preference preference) {
//                //TODO Implement interface callback for querying an item for purchase.
//                return true;
//            }
//        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        int attrs[] = {R.attr.colorContainer};
        TypedArray ta = getActivity().obtainStyledAttributes(attrs);
        view.setBackgroundColor(ta.getColor(0, ResourcesCompat.getColor(getResources(), R.color.material_background, null)));
        ta.recycle();
        return view;
    }
}
