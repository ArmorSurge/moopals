package com.caddosierra.mafiaparty.timer;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caddosierra.mafiaparty.R;
import com.caddosierra.mafiaparty.Settings;

public class DayTimer extends AppCompatActivity implements DayTimerService.ServiceCallbacks {
    private int minutesInput;
    private String startText = "Start";
    private String stopText = "Stop";
    private String resumeText = "Resume";
    private DayTimerService mService;
    private TextView reset;
    private TextView start;
    private TextView time;
    private Window window;
    private Toolbar toolbar;
    private RelativeLayout content;
    private Filter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.day_timer);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        SharedPreferences sharedPref = DayTimer.this.getPreferences(Context.MODE_PRIVATE);
        minutesInput = sharedPref.getInt("timerMinutesInputSaved", 5);

        window = getWindow();
        time = (TextView) findViewById(R.id.timerTime);
        start = (TextView) findViewById(R.id.timerStartStop);
        reset = (TextView) findViewById(R.id.timerReset);
        content = (RelativeLayout) findViewById(R.id.timerContent);
        filter = new Filter(getResources().getColor(R.color.material_blue500));

        updateColor(0.0f);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent serviceIntent = new Intent(this, DayTimerService.class);
        if(!isMyServiceRunning(DayTimerService.class))
            startService(serviceIntent);
        bindService(serviceIntent, mConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mService.isRunning()) {
            mService.setTimerForeground(true);
            mService.setCallbacks(null);
            unbindService(mConnection);
        }
        else if(!mService.isPaused() && !mService.isFinished()) { //If nothing has happened (finished never true here)
            mService.setCallbacks(null);
            unbindService(mConnection);
            stopService(new Intent(this, DayTimerService.class));
        }
        else { //If it is paused, running, or finished
            mService.setCallbacks(null);
            unbindService(mConnection);
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DayTimerService.LocalBinder binder = (DayTimerService.LocalBinder) service;
            mService = binder.getService();
            mService.setCallbacks(DayTimer.this);

            if(mService.isPaused()) {
                start.setText(resumeText);
                start.setTextColor(getResources().getColorStateList(R.color.start_pressed));
                time.setText(mService.getTimerText());
                reset.setEnabled(true);
                time.setEnabled(false);
                updateColor(mService.getPercentFinished());
            }
            else if(mService.isRunning()) {
                start.setText(stopText);
                start.setTextColor(getResources().getColorStateList(R.color.white_toggleable));
                time.setText(mService.getTimerText());
                time.setEnabled(false);

                updateColor(mService.getPercentFinished());
                mService.setTimerForeground(false);
            }
            else if(mService.isFinished()) {
                notifyFinished();
                time.setEnabled(false);
                updateColor(mService.getPercentFinished());
                mService.setTimerForeground(false);
            }
            else {
                mService.makeBuilderString(minutesInput * 60000);
                time.setText(mService.getBuilderText());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public void notifyFinished()
    {
        start.setText(stopText);
        start.setEnabled(false);
        start.setTextColor(getResources().getColorStateList(R.color.white_toggleable));
        time.setText("00:00");
        reset.setEnabled(true);
    }

    public void startPressed(View v) {
        if (start.getText().equals(startText)) {
            mService.startTimer(minutesInput * 60000);
            start.setText(stopText);
            start.setTextColor(getResources().getColorStateList(R.color.white_toggleable));
            time.setEnabled(false);
        } else if (start.getText().equals(stopText)) {
            mService.stopTimer();
            start.setText(resumeText);
            reset.setEnabled(true);
            start.setTextColor(getResources().getColorStateList(R.color.start_pressed));
        } else if (start.getText().equals(resumeText)) {
            mService.startTimer(minutesInput * 60000);
            start.setText(stopText);
            reset.setEnabled(false);
            start.setTextColor(getResources().getColorStateList(R.color.white_toggleable));
        }
    }

    public void resetPressed(View v)
    {
        mService.resetTimer();
        mService.makeBuilderString(minutesInput * 60000);
        time.setText(mService.getBuilderText());
        start.setText(startText);
        reset.setEnabled(false);
        time.setEnabled(true);
        start.setEnabled(true);
        start.setTextColor(getResources().getColorStateList(R.color.start_pressed));
        updateColor(0.0f);
    }

    public void timePressed(View v)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Set Time");
        alert.setMessage("How many minutes?");

        final View input = View.inflate(getApplicationContext(), R.layout.edit_number_text, null);
        final EditText text = (EditText) input;
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                minutesInput = Integer.parseInt(text.getText().toString());
                mService.makeBuilderString(minutesInput * 60000);
                time.setText(mService.getBuilderText());

                SharedPreferences sharedPref = DayTimer.this.getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("timerMinutesInputSaved", minutesInput)
                        .apply();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        });

        AlertDialog alertToShow = alert.create();
        alertToShow.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alertToShow.show();
    }

    public void setTimerText(String text) {
        time.setText(text);
    }

    public void updateColor(float percent)
    {
        percent = percent * .75f;
        int viewColor = filter.getFiltered(percent);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int navigationColor = filter.getFiltered(percent + ((1.0f - percent) * 0.2f));
            window.setStatusBarColor(navigationColor);
            window.setNavigationBarColor(navigationColor);
        }
        content.setBackgroundColor(viewColor);
        toolbar.setBackgroundColor(viewColor);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the new_game_options_menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timer, menu);
        return true;
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        NavUtils.navigateUpFromSameTask(this);
        finish();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}