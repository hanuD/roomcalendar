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

public class BookEventController {

    private Context context;
    private ContentResolver resolver;
    private SharedPreferences preferences;

    public BookEventController(Context context) {
        this.context = context;
        resolver = context.getContentResolver();
        preferences = context.getSharedPreferences(RoomCalendarActivity.PREFS_NAME, 0);
    }

    public void bookEventForCalendar(String eventName, String organizer, Integer[] startTime, Integer[] endTime) {
        bookEvent(eventName, organizer, startTime, endTime, getCalendarId());
    }

    private void bookEvent(String eventName, String organizer, Integer[] startTime, Integer[] endTime, long calendarId) {
        Calendar eventStart = Calendar.getInstance();
        eventStart.set(Calendar.HOUR, startTime[0]);
        eventStart.set(Calendar.MINUTE, startTime[1]);

        Calendar eventEnd = Calendar.getInstance();
        eventEnd.set(Calendar.HOUR, endTime[0]);
        eventEnd.set(Calendar.MINUTE, endTime[1]);

        ContentValues values = new ContentValues();
        values.put(Events.DTSTART, eventStart.getTimeInMillis());
        values.put(Events.DTEND, eventEnd.getTimeInMillis());
        values.put(Events.TITLE, eventName);
        values.put(Events.CALENDAR_ID, calendarId);
        values.put(Events.EVENT_TIMEZONE, "Asia/Kolkata");
        values.put(Events.ORGANIZER, organizer);
        values.put(Events.AVAILABILITY, Events.AVAILABILITY_BUSY);
        values.put(Events.GUESTS_CAN_MODIFY, 1);
        values.put(Events.GUESTS_CAN_INVITE_OTHERS, 1);

        CustomQueryHandler queryHandlers = new CustomQueryHandler(context, new CustomQueryHandler.AsyncQueryListener() {
            @Override
            public void onQueryComplete(int token, Object cookie, Cursor cursor) {
            }

            @Override
            public void onInsertComplete(int token, Object cookie, Uri uri) {
                Log.d("Calendar insert ", "successful");
            }
        });
        queryHandlers.startInsert(1, null, Events.CONTENT_URI, values);
    }

    private long getCalendarId() {
        Uri uri = Calendars.CONTENT_URI;
        String[] calendarProjection = new String[] {Calendars._ID};
        String selection = "((" + Calendars.NAME + " like ? ))";
        String[] selectionArgs = new String[] {preferences.getString("roomName", null)};
        final long[] calendarId = new long[1];
        CustomQueryHandler queryHandlers = new CustomQueryHandler(context, new CustomQueryHandler.AsyncQueryListener() {
            @Override
            public void onQueryComplete(int token, Object cookie, Cursor cursor) {
                if (cursor != null) {
                    calendarId[0] = cursor.getLong(0);
                    cursor.close();
                }
            }

            @Override
            public void onInsertComplete(int token, Object cookie, Uri uri) {
            }
        });
        queryHandlers.startQuery(1, null, uri, calendarProjection, selection, selectionArgs, null);
        return calendarId[0];
    }
}
