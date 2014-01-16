package com.thoughtworks.RoomCalendar;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import com.thoughtworks.utils.CalendarEventRetriever;
import com.thoughtworks.utils.EventDetails;

import java.util.TreeSet;
import java.util.concurrent.ExecutionException;

public class CalendarService extends IntentService {


    public  String BROADCAST_ACTION = "com.thoughtworks.roomcalendar.SHOWCALENDAREVENTS";

    CalendarEventRetriever eventRetriever;

    private String userName = "mytw@thoughtworks.com";
    private String url = "http://www.google.com/calendar/feeds/thoughtworks.com_2d3130303837303331383434%40resource.calendar.google.com/private/full";
    private String password = "Th0ughtW0rks@2012";
    private TreeSet<EventDetails> eventDetailSet;
    private String dateFormat = "yyyy-MM-dd'T'HH:mm:ss";


    public CalendarService() {
        super("Calendar");
        try {
            eventRetriever = new CalendarEventRetriever(url);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("calendar service", "entered calendar service");
        eventDetailSet = eventRetriever.retrieveCalendarEvents(userName, password, url);

        Intent intentBroadcast = new Intent();
        intentBroadcast.setAction(BROADCAST_ACTION);
        intentBroadcast.putExtra("eventDetailSet", eventDetailSet);
        getApplicationContext().sendBroadcast(intentBroadcast);

    }


}
