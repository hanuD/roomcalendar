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

    private static final String ORDER_BY = "orderby";
    private static final String START_TIME = "starttime";
    private static final String FUTURE_EVENT = "futureevent";
    private static final String TRUE = "true";
    private static final String SORT_ORDER = "sortorder";
    private static final String DESCENDING = "descending";
    private static final int MAX_RESULTS = 15;
    private static final String TIME_ZONE = "IST";
    CalendarQuery calendarQuery = null;


    @Override
    protected CalendarQuery doInBackground(String... params) {
        URL feedUrl = null;
        try {
            feedUrl = new URL(params[0]);
            calendarQuery = new CalendarQuery(feedUrl);
            setQueryParameter();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return calendarQuery;
    }

    private void setQueryParameter() {
        calendarQuery.setStringCustomParameter(ORDER_BY, START_TIME);
//      calendarQuery.setStringCustomParameter("singleevent", "true");
        calendarQuery.setStringCustomParameter(FUTURE_EVENT, TRUE);
        calendarQuery.setStringCustomParameter(SORT_ORDER, DESCENDING);
        calendarQuery.setMaxResults(MAX_RESULTS);

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendarQuery.setMinimumStartTime(new DateTime(new Date(calendar.getTimeInMillis())));

        calendar.add(Calendar.DATE, 1);
        calendarQuery.setMaximumStartTime(new DateTime(new Date(calendar.getTimeInMillis())));
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
