package com.thoughtworks.utils;


import android.os.AsyncTask;
import com.google.gdata.client.calendar.CalendarQuery;
import com.google.gdata.data.DateTime;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class CalendarRetrievalHandler extends AsyncTask<String, Void, CalendarQuery> {

    CalendarQuery calendarQuery = null;


    @Override
    protected CalendarQuery doInBackground(String... params) {
        URL feedUrl = null;
        try {
            feedUrl = new URL(params[0]);
        calendarQuery = new CalendarQuery(feedUrl);
        calendarQuery.setStringCustomParameter("orderby", "starttime");
//      calendarQuery.setStringCustomParameter("singleevent", "true");
        calendarQuery.setStringCustomParameter("futureevent", "true");
        calendarQuery.setStringCustomParameter("sortorder", "descending");
        calendarQuery.setMaxResults(15);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
        calendarQuery.setMinimumStartTime(new DateTime(new Date(calendar.getTimeInMillis())));

        calendar.add(Calendar.DATE, 1);
        calendarQuery.setMaximumStartTime(new DateTime(new Date(calendar.getTimeInMillis())));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return calendarQuery;
    }


    @Override
    protected void onPostExecute(CalendarQuery calendarQuery) {
        super.onPostExecute(calendarQuery);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
}
