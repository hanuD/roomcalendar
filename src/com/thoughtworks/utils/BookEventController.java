package com.thoughtworks.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.util.Log;
import com.thoughtworks.RoomCalendar.RoomCalendarActivity;

import java.util.Calendar;
import java.util.Date;

public class BookEventController {

    private Context context;
    private ContentResolver resolver;
    private SharedPreferences preferences;

    public BookEventController(Context context) {
        this.context = context;
        resolver = context.getContentResolver();
        preferences = context.getSharedPreferences(RoomCalendarActivity.PREFS_NAME, 0);
    }

    public void bookEventForCalendar(String eventName, String organizer, Calendar startTime, Calendar endTime) {
        bookEvent(eventName, organizer, startTime, endTime);
    }

    private void bookEvent(final String eventName, final String organizer, final Calendar startTime, final Calendar endTime) {


        Uri uri = Calendars.CONTENT_URI;
        String[] calendarProjection = new String[] {Calendars._ID};
        String selection =  Calendars.CALENDAR_DISPLAY_NAME + " like ?";
        String[] selectionArgs = new String[] {"%"+preferences.getString("roomName", null)+"%"};
        final long[] calendarId = new long[1];
        CustomQueryHandler queryHandlers = new CustomQueryHandler(context, new CustomQueryHandler.AsyncQueryListener() {
            @Override
            public void onQueryComplete(int token, Object cookie, Cursor cursor) {
                if (cursor.moveToFirst()) {
                    do {
                        calendarId[0] = cursor.getLong(0);
                        ContentValues values = new ContentValues();
                        values.put(Events.DTSTART, startTime.getTimeInMillis());
                        values.put(Events.DTEND, endTime.getTimeInMillis());
                        values.put(Events.TITLE, eventName);
                        values.put(Events.CALENDAR_ID, calendarId[0]);
                        values.put(Events.EVENT_TIMEZONE, "Asia/Kolkata");
                        values.put(Events.ORGANIZER, organizer+"@thoughtworks.com");
                        values.put(Events.AVAILABILITY, Events.AVAILABILITY_BUSY);
                        values.put(Events.GUESTS_CAN_MODIFY, 1);
                        values.put(Events.GUESTS_CAN_INVITE_OTHERS, 1);
                        values.put(Events.EVENT_LOCATION, context.getSharedPreferences(RoomCalendarActivity.PREFS_NAME,0).getString("roomFullName", null));

                        System.out.println(new Date(values.getAsLong(Events.DTSTART)));
                        System.out.println(new Date(values.getAsLong(Events.DTEND)));
                        System.out.println(values.getAsLong(Events.CALENDAR_ID));
                        System.out.println(values.getAsString(Events.TITLE));

                        CustomQueryHandler queryHandlers = new CustomQueryHandler(context, new CustomQueryHandler.AsyncQueryListener() {
                            @Override
                            public void onQueryComplete(int token, Object cookie, Cursor cursor) {
                            }

                            @Override
                            public void onInsertComplete(int token, Object cookie, Uri uri) {
                                Log.d("Calendar insert ", "successful");
                                Log.d("token value " , Integer.toString(token));
                            }
                        });
                        queryHandlers.startInsert(1, null, Events.CONTENT_URI, values);
                    }while(cursor.moveToNext());

                }
                cursor.close();
            }

            @Override
            public void onInsertComplete(int token, Object cookie, Uri uri) {
            }
        });
        queryHandlers.startQuery(1, null, uri, calendarProjection, selection, selectionArgs, null);


    }

    private long getCalendarId() {
        Uri uri = Calendars.CONTENT_URI;
        String[] calendarProjection = new String[] {Calendars._ID};
        String selection =  Calendars.CALENDAR_DISPLAY_NAME + " like ?";
        String[] selectionArgs = new String[] {"%"+preferences.getString("roomName", null)+"%"};
        final long[] calendarId = new long[1];
        CustomQueryHandler queryHandlers = new CustomQueryHandler(context, new CustomQueryHandler.AsyncQueryListener() {
            @Override
            public void onQueryComplete(int token, Object cookie, Cursor cursor) {
                if (cursor.moveToFirst()) {
                    do {
                        calendarId[0] = cursor.getLong(0);
                        System.out.println("get "+calendarId[0]);
                    }while(cursor.moveToNext());

                }
                cursor.close();
            }

            @Override
            public void onInsertComplete(int token, Object cookie, Uri uri) {
            }
        });
        queryHandlers.startQuery(1, null, uri, calendarProjection, selection, selectionArgs, null);
        return calendarId[0];
    }
}
