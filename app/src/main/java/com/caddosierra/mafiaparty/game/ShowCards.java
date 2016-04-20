package com.caddosierra.mafiaparty.game;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caddosierra.mafiaparty.MainActivity;
import com.caddosierra.mafiaparty.R;

import java.util.ArrayList;


public class ShowCards extends AppCompatActivity {
    private ArrayList<String> values;
    private TextView role;
    private TextView roleLabel;
    private TextView seen;
    private TextView playerNumber;
    private Button next;
    private int total = 0;
    private int current = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean darkTheme = preferences.getBoolean("pref_theme", false);
        if(darkTheme)
            setTheme(R.style.AppThemeDark);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_cards);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        values = intent.getStringArrayListExtra("ArrayList");

        total = values.size();

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.card_display);
        role = (TextView) findViewById(R.id.role);
        roleLabel = (TextView) findViewById(R.id.role_label);
        seen = (TextView) findViewById(R.id.seen);
        playerNumber = (TextView) findViewById(R.id.player_number);
        next = (Button) findViewById(R.id.next_player);
        next.setEnabled(false);


        playerNumber.setText("Player: " + (current + 1));
        layout.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v)
            {
                roleLabel.setVisibility(View.VISIBLE);
                role.setText(values.get(current));
                seen.setText("ROLE HAS BEEN VIEWED");
                next.setEnabled(true);

                v.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_UP:
                                roleLabel.setVisibility(View.INVISIBLE);
                                role.setText("Tap and hold to view again. Press next player when finished.");

                                if(current == total - 1)
                                    role.setText("Tap and hold to view again. All cards have been viewed, press done when finished.");

                                v.setOnTouchListener(null);
                                break;
                        }
                        return false;
                    }
                });

                return true;
            }
        });
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("Current", current);
        outState.putBoolean("Seen", !seen.getText().equals("Unseen"));

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState)
    {
        current = savedInstanceState.getInt("Current");
        playerNumber.setText("Player: " + (current + 1));

        boolean isSeen = savedInstanceState.getBoolean("Seen");

        if(isSeen)
        {
            if(current == total - 1)
                role.setText("Tap and hold to view again. All cards have been viewed, press done when finished.");
            else
                role.setText("Tap and hold to view again. Press next player when finished.");


            seen.setText("ROLE HAS BEEN VIEWED");
            next.setEnabled(true);
        }
        else
        {
            role.setText("Tap and hold to view your role.");
            seen.setText("Unseen");
            next.setEnabled(false);
        }

        if(current == total - 1)
            next.setText("Done");

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.home)
        {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void nextPlayer(View view)
    {
        current++;
        if(current == total - 1)
        {
            next.setEnabled(false);
            next.setText("Done");
            roleLabel.setVisibility(View.INVISIBLE);
            role.setText("Tap and hold to view your role.");
            seen.setText("Unseen");
            playerNumber.setText("Player: " + (current + 1));
        }
        else if(current == total)
        {
            Intent reset = new Intent(this, MainActivity.class);
            startActivity(reset.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
        else
        {
            next.setEnabled(false);
            roleLabel.setVisibility(View.INVISIBLE);
            role.setText("Tap and hold to view your role.");
            seen.setText("Unseen");
            playerNumber.setText("Player: " + (current + 1));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
