package com.caddosierra.mafiaparty;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.caddosierra.mafiaparty.game.NewGameActivity;
import com.caddosierra.mafiaparty.timer.DayTimer;


public class MainActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    private String themeOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        themeOption = preferences.getString("pref_theme_option", "0");
        setTheme(new ThemeManager(themeOption).getThemeId());

        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String newThemeOption = preferences.getString("pref_theme_option", "0");

        if (!themeOption.equals(newThemeOption)) {
            recreate();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void newGame(View view)
    {
        Intent intent = new Intent(this, NewGameActivity.class);
        startActivity(intent);
    }

    public void startTimer(View view)
    {
        Intent intent = new Intent(this, DayTimer.class);
        startActivity(intent);
    }

    public void viewRules(View v) {
        Intent intent = new Intent(this, Rules.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the new_game_options_menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
