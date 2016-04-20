package com.caddosierra.mafiaparty;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.caddosierra.mafiaparty.game.NewGame;
import com.caddosierra.mafiaparty.timer.DayTimer;


public class MainActivity extends AppCompatActivity {
    boolean isDarkTheme;
    ListView listView;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle acbt;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean darkTheme = preferences.getBoolean("pref_theme", false);

        if(darkTheme) {
            setTheme(R.style.AppThemeDark);
            isDarkTheme = true;
        }
        else
            isDarkTheme = false;

        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_drawer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(darkTheme)
            toolbar.setPopupTheme(R.style.DarkPopup);

        String[] list = new String[] {"Setups",
                "Rules",
                "Settings",
                "About"};

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        listView = (ListView) findViewById(R.id.left_drawer_list);

        listView.setAdapter(new ArrayAdapter<>(this, R.layout.design_navigation_item, list));
        listView.setOnItemClickListener(new DrawerItemClickListener());
        acbt = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0)
        {
            @Override
            public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);
            // code here will execute once the drawer is opened( As I dont want anything happened whe drawer is
            // open I am not going to put anything here)
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Code here will execute once drawer is closed
            }};
        drawerLayout.setDrawerListener(acbt);
        acbt.syncState(); //If this ever misbehaves, put it in OnPostCreate

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, new SetupFragment())
                .commit();
        setTitle("Setups");
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean darkTheme = preferences.getBoolean("pref_theme", false);

        if(darkTheme)
        {
            if(!isDarkTheme) {
                finish();
                startActivity(getIntent());
            }
        } else {
            if (isDarkTheme) {
                finish();
                startActivity(getIntent());
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void newGame(View view)
    {
        Intent intent = new Intent(this, NewGame.class);
        startActivity(intent);
    }

    public void startTimer(View view)
    {
        Intent intent = new Intent(this, DayTimer.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position)
    {
        if(position == 2)
        {
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
        }
        else if(position == 3)
        {
            Toast.makeText(this, "Coming soon to a theatre near you.", Toast.LENGTH_SHORT)
                    .show();
            Intent intent2 = new Intent(this, NewGame.class);
            startActivity(intent2);
        }
        else
        {
            Fragment fragment;
            Toast.makeText(this, "" + position, Toast.LENGTH_SHORT).show();

            if(position == 0)
            {
                fragment = new SetupFragment();
                setTitle("Setups");
            }
            else if(position == 1)
            {
                fragment = new RulesFragment();
                setTitle("Rules");
            }
            else
            {
                fragment = new Fragment();
            }

            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();

            listView.setItemChecked(position, true);
            drawerLayout.closeDrawer(listView);
        }
    }
}
