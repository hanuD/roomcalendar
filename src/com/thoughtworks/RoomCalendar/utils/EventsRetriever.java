package com.thoughtworks.RoomCalendar.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.CalendarContract;
import com.thoughtworks.RoomCalendar.activity.RoomCalendarActivity;
import com.thoughtworks.RoomCalendar.domain.EventDetails;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class EventsRetriever extends AsyncTask<String, Void, ArrayList<EventDetails>> {

    Context context;
    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");

    public EventsRetriever(Context context) {
        this.context = context;
    }

    @Override
    protected ArrayList<EventDetails> doInBackground(String... params) {

        String calendarName = params[0];
        ArrayList<EventDetails> eventDetailsList = new ArrayList<EventDetails>();

        String[] INSTANCE_PROJECTION = new String[]{
                CalendarContract.Instances.EVENT_LOCATION,
                CalendarContract.Instances.ORGANIZER,
                CalendarContract.Instances.TITLE,
                CalendarContract.Instances.BEGIN,
                CalendarContract.Instances.END,
                CalendarContract.Instances.EVENT_ID
        };

        Calendar startTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();

//        startTime.add(Calendar.MINUTE, -startTime.get(Calendar.MINUTE));
//        startTime.add(Calendar.SECOND, -startTime.get(Calendar.SECOND));

        endTime.add(Calendar.HOUR_OF_DAY, 24 - startTime.get(Calendar.HOUR_OF_DAY));
        endTime.add(Calendar.MINUTE, -endTime.get(Calendar.MINUTE));
        endTime.add(Calendar.SECOND, -endTime.get(Calendar.SECOND));

        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, startTime.getTimeInMillis());
        ContentUris.appendId(builder, endTime.getTimeInMillis());
        ContentResolver contentResolver = context.getContentResolver();

        Cursor cursor = contentResolver.query(builder.build(), INSTANCE_PROJECTION, null, null, "startDay ASC, startMinute ASC");
        String[] organizerName= new String[] {CalendarContract.Attendees.ATTENDEE_NAME, CalendarContract.Attendees.ATTENDEE_RELATIONSHIP};
        String selection = "((" + CalendarContract.Attendees.ATTENDEE_RELATIONSHIP + " = ?)";
        Cursor attendeeCursor = null;

        if (cursor.moveToFirst()) {
            do {
                    if (cursor.getString(0).contains(calendarName)) {
                        context.getSharedPreferences(RoomCalendarActivity.PREFS_NAME, 0).edit().putString("roomFullName", cursor.getString(0)).commit();
                        EventDetails eventDetails = new EventDetails();
                        eventDetails.setLocation(calendarName);

                        eventDetails.setOrganizer(cursor.getString(1));
                        eventDetails.setEventName(cursor.getString(2));


                        eventDetails.setStartTime(cursor.getLong(3));
                        eventDetails.setEndTime(cursor.getLong(4));

                        eventDetails.setEventId(cursor.getLong(5));
                        selection = selection +  "AND (" + CalendarContract.Attendees.EVENT_ID + " = ?))";
                        attendeeCursor = CalendarContract.Attendees.query(contentResolver, eventDetails.getEventId(), organizerName);

                        if(attendeeCursor.moveToFirst()) {
                            do {
                                String attendeeName = attendeeCursor.getString(0);
                                if(attendeeName.contains("thoughtworks_")) {
                                    continue;
                                } else {
                                    eventDetails.setOrganizer(attendeeCursor.getString(0));
                                }
                            }while (cursor.moveToNext());
                        }

                        eventDetailsList.add(eventDetails);
                    }
            } while (cursor.moveToNext());
        }
        cursor.close();

        return eventDetailsList;
    }
}
