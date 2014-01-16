package com.thoughtworks.utils;

import android.util.Log;
import com.google.gdata.client.calendar.CalendarQuery;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.calendar.CalendarEventFeed;
import com.google.gdata.data.extensions.When;
import com.google.gdata.data.extensions.Where;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;

public class CalendarEventRetriever {

    String url;
    URL feedUrl = null;
    CalendarQuery calendarQuery1;

    public CalendarEventRetriever(String url) throws ExecutionException, InterruptedException {
        CalendarRetrievalHandler handler = new CalendarRetrievalHandler();
        handler.execute(url);
        calendarQuery1 = handler.get();
    }

    TreeSet<EventDetails> eventDetailsSet;


    public TreeSet<EventDetails> retrieveCalendarEvents(String userName, String password, String url) {
        com.google.gdata.client.calendar.CalendarService calendarService = new com.google.gdata.client.calendar.CalendarService("Calendar");
        eventDetailsSet = new TreeSet<EventDetails>();
        try {
            feedUrl = new URL(url);
            calendarService.setUserCredentials(userName, password);
//            calendarQuery = new CalendarQuery(feedUrl);
//            calendarQuery.setStringCustomParameter("orderby", "starttime");
//            calendarQuery.setStringCustomParameter("singleevent", "true");
//            calendarQuery.setStringCustomParameter("futureevent", "true");
//            calendarQuery.setStringCustomParameter("sortorder", "descending");
//            calendarQuery.setMaxResults(15);
//            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
//            calendarQuery.setMinimumStartTime(new DateTime(new Date(calendar.getTimeInMillis())));
//
//            calendar.add(Calendar.DATE, 1);
//            calendarQuery.setMaximumStartTime(new DateTime(new Date(calendar.getTimeInMillis())));

            CalendarEventFeed resultFeed = calendarService.query(calendarQuery1, CalendarEventFeed.class);
            List<CalendarEventEntry> calendarEventEntries = resultFeed.getEntries();

            for (CalendarEventEntry entry : calendarEventEntries) {
                setEventDetails(entry);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (AuthenticationException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return eventDetailsSet;

    }

    private void setEventDetails(CalendarEventEntry entry) {
        EventDetails eventDetails = new EventDetails();
        eventDetails.setName(entry.getTitle().getPlainText());
        eventDetails.setAuthor(entry.getAuthors().get(0).getName());
        eventDetails.setEventId(entry.getIcalUID());

        setLocation(entry, eventDetails);

        setStartEventAndEndEvent(entry, eventDetails);
        Log.d("event details ", eventDetails.toString());
        eventDetailsSet.add(eventDetails);
    }

    private void setStartEventAndEndEvent(CalendarEventEntry entry, EventDetails eventDetails) {
        for (When when : entry.getTimes()) {
            if (when.getStartTime() != null) {
                eventDetails.setEventstart(new Date(when.getStartTime().getValue()));
            }
            if (when.getEndTime() != null) {
                eventDetails.setEventend(new Date(when.getEndTime().getValue()));
            }
        }
    }

    private void setLocation(CalendarEventEntry entry, EventDetails eventDetails) {
        for (Where where : entry.getLocations()) {
            eventDetails.setPlace(where.getValueString());
        }
    }
}
