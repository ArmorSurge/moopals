package com.caddosierra.mafiaparty.timer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Vibrator;
import android.view.WindowManager;

import com.caddosierra.mafiaparty.R;

public class DayTimerService extends Service {
    private final IBinder mBinder = new LocalBinder();
    private ServiceCallbacks serviceCallbacks;
    private CountDownTimer countDownTimer;
    private boolean isRunning = false;
    private boolean isPaused = false;
    private boolean isFinished = false;
    private int seconds;
    private int minutes;
    private int secondsLast = -1;
    private int percentFinished;
    private long millisInput;
    private long millisUntilFinished = 0;
    private StringBuilder mBuilder = new StringBuilder();
    private Notification.Builder mNotiBuilder;
    private NotificationManager mNotiManager;
    private PendingIntent resultPendingIntent;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public void setTimerForeground(boolean isForeground)
    {
        if(isForeground)
        {
            Intent resultIntent = new Intent(this, DayTimer.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(DayTimer.class);
            stackBuilder.addNextIntent(resultIntent);
            resultPendingIntent =
                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            mNotiBuilder = new Notification.Builder(this);
            mNotiManager = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);

            percentFinished = (int) ((millisInput - millisUntilFinished) * 10000 / millisInput);

            Notification notification = mNotiBuilder
                    .setContentTitle("Mafia Day Timer")
                    .setContentText(mBuilder.toString())
                    .setSmallIcon(R.drawable.ic_timer_notification)
                    .setContentIntent(resultPendingIntent)
                    .setProgress(10000, percentFinished, false)
                    .build();
            startForeground(80085, notification);
        }
        else
            stopForeground(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder
    {
        DayTimerService getService() {
            return DayTimerService.this;
        }
    }

    public interface ServiceCallbacks
    {
        void setTimerText(String text);
        void notifyFinished();
        void updateColor(float percent);
    }

    public void setCallbacks(ServiceCallbacks callbacks)
    {
        serviceCallbacks = callbacks;
    }

    public void startTimer(final long millisInput)
    {
        isRunning = true;
        isPaused = false;
        isFinished = false;

        this.millisInput = millisInput;

        long millis;
        millis = millisUntilFinished != 0 ? millisUntilFinished : millisInput;

        countDownTimer = new CountDownTimer(millis, 100) { //millis first param
            @Override
            public void onTick(long millis) {
                millisUntilFinished = millis;
                percentFinished = (int) ((millisInput - millisUntilFinished) * 10000 / millisInput);
                if(isChanged(millis)) {
                    if (serviceCallbacks != null) {
                        makeBuilderString(millis);
                        serviceCallbacks.setTimerText(mBuilder.toString());
                    } else {
                        makeBuilderString(millis);
                        mNotiBuilder.setContentText(mBuilder.toString())
                                .setProgress(10000, percentFinished, false);
                        mNotiManager.notify(80085, mNotiBuilder.build());
                    }
                }
                if(serviceCallbacks != null)
                    serviceCallbacks.updateColor((float) percentFinished / 10000);

            }

            @Override
            public void onFinish() {
                isRunning = false;
                isFinished = true;
                if(serviceCallbacks != null) {
                    serviceCallbacks.notifyFinished();
                    serviceCallbacks.updateColor(1.0f);
                }
                else {
                    mNotiBuilder.setContentText("Day finished! Click to view.")
                            .setPriority(Notification.PRIORITY_MAX)
                            .setContentIntent(resultPendingIntent);
                    mNotiManager.notify(80085, mNotiBuilder.build());
                }
                countDownTimer = null;
                showSystemAlert();
            }
        }.start();
    }

    public void makeBuilderString(long millis)
    {
        calculateTime(millis);

        mBuilder.setLength(0);
        mBuilder.setLength(5);
        if(minutes < 10)
            mBuilder.append(0);
        mBuilder.append(minutes)
                .append(":");
        if(seconds < 10)
            mBuilder.append(0);
        mBuilder.append(seconds);
    }

    private boolean isChanged(long millis)
    {
        calculateTime(millis);

        if(seconds != secondsLast)
        {
            secondsLast = seconds;
            return true;
        }
        else
            return false;
    }

    private void calculateTime(long millis)
    {
        seconds = 0;
        minutes = 0;
        seconds = ((int) millis / 1000);
        minutes = seconds / 60;
        seconds = seconds % 60;
    }

    public void stopTimer()
    {
        countDownTimer.cancel();
        countDownTimer = null;
        isRunning = false;
        isPaused = true;
    }

    public void resetTimer()
    {
        isPaused = false;
        isFinished = false;
        millisUntilFinished = 0;
        secondsLast = -1;
    }

    private void showSystemAlert()
    {
        Uri alarmTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        final Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), alarmTone);
        r.play();
        final Vibrator vibrator;
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        MyDialog dialog = new MyDialog(this);
        dialog.setTitle("Day Finished!");
        dialog.setMessage("Deadline has ended.");
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(serviceCallbacks == null)
                    stopSelf();
            }
        });
        if(serviceCallbacks == null)
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "View Timer", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (serviceCallbacks == null)
                        launchTimerActivity();
                    dialog.dismiss();
                }
            });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (serviceCallbacks == null)
                    setTimerForeground(false);
                vibrator.cancel();
                r.stop();
            }
        });
        dialog.show();

        long[] pattern;
        pattern = new long[2];
        pattern[0] = 500;
        pattern[1] = 500;
        vibrator.vibrate(pattern, 0);
    }

    private void launchTimerActivity() {
        Intent dialogIntent = new Intent(this, DayTimer.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(dialogIntent);
    }

    public float getPercentFinished()
    {
        return (float) percentFinished / 10000;
    }

    public String getTimerText()
    {
        makeBuilderString(millisUntilFinished);
        return mBuilder.toString();
    }

    public String getBuilderText()
    {
        return mBuilder.toString();
    }

    public boolean isRunning()
    {
        return isRunning;
    }

    public boolean isPaused()
    {
        return  isPaused;
    }

    public boolean isFinished()
    {
        return isFinished;
    }
}