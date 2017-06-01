package com.caddosierra.mafiaparty;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;


public class Rules extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String themeOption = preferences.getString("pref_theme_option", "0");
        setTheme(new ThemeManager(themeOption).getThemeId());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.rules);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView htmlText = (TextView) findViewById(R.id.html_text);
        htmlText.setText(Html.fromHtml(getString(R.string.rules_html)));
        htmlText.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.home)
        {
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }

}
