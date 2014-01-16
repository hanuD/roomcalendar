package com.thoughtworks.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.thoughtworks.RoomCalendar.CalendarService;

public class StartCalendarServiceReceiver extends BroadcastReceiver{


    @Override
    public void onReceive(Context context, Intent intent) {
    Log.d("CalendarReceiver", "entered calendar receiver");


        Intent service = new Intent(context, CalendarService.class);
        context.startService(service);
    }
}
