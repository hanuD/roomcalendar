package com.thoughtworks.RoomCalendar;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import com.thoughtworks.utils.EventsRetriever;

import java.util.concurrent.ExecutionException;

public class CalendarService extends IntentService {


    public CalendarService() {
        super("RoomCalendar");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        EventsRetriever eventRetriever = new EventsRetriever(getApplicationContext());
        SharedPreferences preferences = getSharedPreferences(RoomCalendarActivity.PREFS_NAME, 0);
        eventRetriever.execute(preferences.getString("roomName", null));

        try {
            Intent intentBroadcast = new Intent();
            intentBroadcast.setAction(RoomCalendarActivity.BROADCAST_ACTION);
            intentBroadcast.putExtra("eventDetail", eventRetriever.get());
            getApplicationContext().sendBroadcast(intentBroadcast);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


}
