package com.thoughtworks.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.thoughtworks.RoomCalendar.RoomCalendarActivity;

import java.util.Calendar;
import java.util.Date;

public class EventScheduleReceiver extends BroadcastReceiver{

    private static final long REPEAT_TIME = 1000 * 60 * 1;

    @Override
    public void onReceive(Context context, Intent incomingIntent) {

        AlarmManager alarmManagerService = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, StartCalendarServiceReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.SECOND, 60);
        alarmManagerService.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), REPEAT_TIME, pendingIntent);
    }
}
