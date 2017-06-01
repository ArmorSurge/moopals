package com.caddosierra.mafiaparty;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;


public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String darkTheme = preferences.getString("pref_theme_option", "0");
        setTheme(new ThemeManager(darkTheme).getThemeId());

        super.onCreate(savedInstanceState);

        setContentView(R.layout.preference_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SettingsFragment fragment = new SettingsFragment();

        getFragmentManager().beginTransaction()
                .replace(R.id.preference_activity_content, fragment)
                .commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == android.R.id.home) {
            super.onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
