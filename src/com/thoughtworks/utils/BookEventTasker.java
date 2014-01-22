package com.thoughtworks.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.CalendarContract;
import com.thoughtworks.RoomCalendar.RoomCalendarActivity;

public class BookEventTasker extends AsyncTask<BookingDetails, Void, Integer> {

    private  Context context;
    private  SharedPreferences preferences;
    private ContentResolver contentResolver;

    public BookEventTasker(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(RoomCalendarActivity.PREFS_NAME, 0);
        contentResolver = context.getContentResolver();
    }

    @Override
    protected Integer doInBackground(BookingDetails... bookingDetails) {
        long calendarId = gerCalendarId();
        long eventID = createEvent(calendarId, bookingDetails[0]);
        Uri uri = addOrganizerToAttendees(eventID, bookingDetails[0]);
        return Integer.parseInt(uri.getLastPathSegment());
    }

    private Uri addOrganizerToAttendees(long eventID, BookingDetails bookingDetail) {
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Attendees.ATTENDEE_NAME, bookingDetail.getOrganizer());
        values.put(CalendarContract.Attendees.ATTENDEE_EMAIL, bookingDetail.getOrganizer() + "@thoughtworks.com");
        values.put(CalendarContract.Attendees.ATTENDEE_RELATIONSHIP, CalendarContract.Attendees.RELATIONSHIP_ORGANIZER);
        values.put(CalendarContract.Attendees.ATTENDEE_TYPE, CalendarContract.Attendees.TYPE_REQUIRED);
        values.put(CalendarContract.Attendees.ATTENDEE_STATUS, CalendarContract.Attendees.ATTENDEE_STATUS_INVITED);
        values.put(CalendarContract.Attendees.EVENT_ID, eventID);
        return contentResolver.insert(CalendarContract.Attendees.CONTENT_URI, values);
    }

    private long createEvent(long calendarId, BookingDetails bookingDetail) {
        ContentValues eventValues = new ContentValues();
        eventValues.put(CalendarContract.Events.DTSTART, bookingDetail.getStartTime().getTimeInMillis());
        eventValues.put(CalendarContract.Events.DTEND, bookingDetail.getEndTime().getTimeInMillis());
        eventValues.put(CalendarContract.Events.TITLE, bookingDetail.getEventName());
        eventValues.put(CalendarContract.Events.CALENDAR_ID, calendarId);
        eventValues.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
        eventValues.put(CalendarContract.Events.ORGANIZER, bookingDetail.getOrganizer());
        eventValues.put(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
        eventValues.put(CalendarContract.Events.GUESTS_CAN_MODIFY, 1);
        eventValues.put(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, 1);
        String roomName = context.getSharedPreferences(RoomCalendarActivity.PREFS_NAME, 0).getString("roomFullName", null);
        if (roomName == null) {
            roomName =  context.getSharedPreferences(RoomCalendarActivity.PREFS_NAME, 0).getString("roomName", null);
        }
        eventValues.put(CalendarContract.Events.EVENT_LOCATION, roomName);

        Uri uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, eventValues);
        return Long.parseLong(uri.getLastPathSegment());
    }

    private long gerCalendarId() {
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String[] calendarProjection = new String[] {CalendarContract.Calendars._ID};
        String selection =  CalendarContract.Calendars.CALENDAR_DISPLAY_NAME + " like ?";
        String[] selectionArgs = new String[] {"%"+preferences.getString("roomName", null)+"%"};
        Cursor cursor = null;
        long calendarId = 0;

        cursor = contentResolver.query(uri, calendarProjection, selection, selectionArgs, null);
        if (cursor.moveToFirst()) {
            calendarId = cursor.getLong(0);
        }
        cursor.close();
        return calendarId;
    }
}
