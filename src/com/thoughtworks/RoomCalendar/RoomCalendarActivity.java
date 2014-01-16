package com.thoughtworks.RoomCalendar;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gdata.data.DateTime;
import com.thoughtworks.utils.CustomListViewAdapter;
import com.thoughtworks.utils.EventDetails;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;


public class RoomCalendarActivity extends Activity {

    public String BROADCAST_ACTION = "com.thoughtworks.roomcalendar.SHOWCALENDAREVENTS";

    TextView availabilityStatus;
    TextView roomNameTextView;
    ListView upcomingEventsListView;
    TextView currentEventDetailsTextView;
    TextView currentEventNameTextView;
    TextView authorNameTextView;
    List<EventDetails> eventsList;
    String currentEventName;
    String currentEventAuthorName;
    String currentEventStartTime;
    String currentEventEndTime;
    TreeSet<EventDetails> eventDetails;
    IntentFilter filter = new IntentFilter();
    CustomListViewAdapter adapter;


    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            adapter.clear();
            eventDetails = (TreeSet<EventDetails>) intent.getSerializableExtra("eventDetailSet");
            updateCalendarEvents(adapter);

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.index);

        ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {

            availabilityStatus = (TextView) findViewById(R.id.availabilityStatusTextView);
            roomNameTextView = (TextView) findViewById(R.id.roomNameTextView);
            currentEventDetailsTextView = (TextView) findViewById(R.id.currentEventDetailsTextView);
            currentEventNameTextView = (TextView) findViewById(R.id.currentEventNameTextView);
            upcomingEventsListView = (ListView) findViewById(R.id.upcomingEventsListView);

            eventsList = new ArrayList<EventDetails>();

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            filter.addAction(BROADCAST_ACTION);
            getApplicationContext().registerReceiver(receiver, filter);


            adapter = new CustomListViewAdapter(this,
                    R.layout.list_item, eventsList);


            upcomingEventsListView.setAdapter(adapter);

            Intent intent = new Intent(this, CalendarService.class);
            startService(intent);
        } else {
            Toast.makeText(getApplicationContext(), "Internet connection not available", Toast.LENGTH_LONG);
        }
    }

    private void updateCalendarEvents(CustomListViewAdapter adapter) {
        try {

            if (isRoomAvailable(eventDetails)) {
                availabilityStatus.setBackgroundColor(Color.GREEN);
                roomNameTextView.setText("BANDIPUR");
                currentEventDetailsTextView.setText("\n\tAvailable");
                currentEventNameTextView.setText("");

            } else {
                availabilityStatus.setBackgroundColor(Color.RED);
                roomNameTextView.setText("BANDIPUR");
                currentEventNameTextView.setText("Event Name:\n" + currentEventName);
                currentEventDetailsTextView.setText(currentEventStartTime + " : " + currentEventEndTime);
                authorNameTextView.setText(currentEventAuthorName);
            }
            eventsList.clear();
            for (EventDetails eventDetail : eventDetails) {
                eventsList.add(eventDetail);
                Log.d("Event Detail", eventDetail.toString());
            }

            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isRoomAvailable(TreeSet<EventDetails> eventDetails) throws ParseException {
        DateTime dateTime = DateTime.now();
        boolean isOccupied;
        for (EventDetails eventDetail : eventDetails) {
            Date before = eventDetail.getEventStart();
            Date after = eventDetail.getEventEnd();
            Date toCheck = new Date(dateTime.getValue());
            isOccupied = (before.getTime() < toCheck.getTime()) && after.getTime() > toCheck.getTime();
            if (isOccupied) {
                currentEventName = eventDetail.getName();
                currentEventAuthorName = eventDetail.getAuthor();
                currentEventStartTime = eventDetail.getEventStart().toLocaleString().split(" ")[3].substring(0, eventDetail.getEventStart().toLocaleString().split(" ")[3].length() - 3);
                currentEventEndTime = eventDetail.getEventEnd().toLocaleString().split(" ")[3].substring(0, eventDetail.getEventEnd().toLocaleString().split(" ")[3].length() - 3);
                return false;
            }
        }
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        filter.addAction(getResources().getString(R.string.broadcast_action));
        getApplicationContext().registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getApplicationContext().unregisterReceiver(receiver);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        filter.addAction(getResources().getString(R.string.broadcast_action));
        getApplicationContext().registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        getApplicationContext().unregisterReceiver(receiver);
    }
}
