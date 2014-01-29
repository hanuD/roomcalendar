package com.thoughtworks.RoomCalendar.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;
import com.thoughtworks.RoomCalendar.R;
import com.thoughtworks.RoomCalendar.domain.EventDetails;
import com.thoughtworks.RoomCalendar.utils.CustomListViewAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class RoomCalendarActivity extends Activity {

    public static String BROADCAST_ACTION = "com.thoughtworks.roomcalendar.SHOWCALENDAREVENTS";
    public static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm");
    public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM dd, yyyy");
    public static final String PREFS_NAME = "RoomCalendarPrefs";

    TextView roomNameTextView;
    ListView upcomingEventsListView;
    TextView currentEventDetailsTextView;
    TextView currentEventNameTextView;
    List<EventDetails> eventsList;
    String currentEventName;
    String currentEventAuthorName;
    String currentEventStartTime;
    String currentEventEndTime;
    ArrayList<EventDetails> eventDetails;
    IntentFilter filter = new IntentFilter();
    CustomListViewAdapter adapter;
    Resources resources;
    SharedPreferences preferences;
    Context context;
    Button addButton;
    RelativeLayout eventsViewHolder;
    TextView currentDate;
    TextView cityNameTextView;

    public void setEventDetails(ArrayList<EventDetails> eventDetails) {
        this.eventDetails = eventDetails;
    }

    public ArrayList<EventDetails> getEventDetails() {
        return eventDetails;
    }

    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            adapter.clear();
            setEventDetails((ArrayList <EventDetails>) intent.getSerializableExtra("eventDetail"));
            updateCalendarEvents(adapter);

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.new_main);

        ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        resources = getResources();
        preferences = getSharedPreferences(PREFS_NAME, 0);
        preferences.edit().putString("roomName", resources.getString(R.string.majestic)).commit();
        preferences.edit().putString("cityName", resources.getString(R.string.bangalore)).commit();

        context = this;

        if (mWifi.isConnected()) {

            eventsViewHolder = (RelativeLayout) findViewById(R.id.eventsViewHolder);
            roomNameTextView = (TextView) findViewById(R.id.roomNameTextView);
            currentEventDetailsTextView = (TextView) findViewById(R.id.currentEventDetailsTextView);
            currentEventNameTextView = (TextView) findViewById(R.id.currentEventNameTextView);
            upcomingEventsListView = (ListView) findViewById(R.id.upcomingEventsListView);
            eventsList = new ArrayList<EventDetails>();
            roomNameTextView.setText(resources.getString(R.string.majestic));
            addButton = (Button) findViewById(R.id.addButton);
            currentDate = (TextView) findViewById(R.id.currentDate);
            cityNameTextView = (TextView) findViewById(R.id.cityNameTextView);

            registerClickListeners();

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            filter.addAction(RoomCalendarActivity.BROADCAST_ACTION);
            getApplicationContext().registerReceiver(receiver, filter);

            adapter = new CustomListViewAdapter(this,
                    R.layout.list_item, eventsList);
            Log.d("Event Details", eventsList.toString());
            upcomingEventsListView.setAdapter(adapter);

            Intent intent = new Intent(this, CalendarService.class);
            startService(intent);
        } else {
            Toast.makeText(context, resources.getString(R.string.internet_unavailable), Toast.LENGTH_LONG).show();
        }
    }

    private void updateCalendarEvents(CustomListViewAdapter adapter) {

        try {
            Date date = new Date();
            currentDate.setText(DATE_FORMAT.format(date));
            cityNameTextView.setText(preferences.getString("cityName", null));
            if (isRoomAvailable()) {
                currentEventDetailsTextView.setVisibility(View.GONE);
                eventsViewHolder.setBackgroundColor(Color.parseColor("#009900"));
                addButton.setVisibility(View.VISIBLE);
            } else {
                currentEventDetailsTextView.setVisibility(View.VISIBLE);
                eventsViewHolder.setBackgroundColor(Color.parseColor("#800000"));
                addButton.setVisibility(View.INVISIBLE);
                currentEventNameTextView.setText(resources.getString(R.string.event_name) + resources.getString(R.string.new_line) + currentEventName);
                currentEventDetailsTextView.setText(currentEventStartTime + resources.getString(R.string.hyphen) + currentEventEndTime + resources.getString(R.string.new_line) + currentEventAuthorName);
            }
            eventsList.clear();
            for (EventDetails eventDetail : getEventDetails()) {
                eventsList.add(eventDetail);
            }

            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isRoomAvailable() throws ParseException {
        boolean isOccupied;
        for (EventDetails eventDetail : getEventDetails()) {
            Date before = new Date(eventDetail.getStartTime());
            Date after = new Date(eventDetail.getEndTime());
            Date toCheck = new Date();
            isOccupied = (before.getTime() < toCheck.getTime()) && after.getTime() > toCheck.getTime();
            if (isOccupied) {
                currentEventName = eventDetail.getEventName();
                currentEventAuthorName = eventDetail.getOrganizer();
                currentEventStartTime = RoomCalendarActivity.TIME_FORMAT.format(eventDetail.getStartTime());
                currentEventEndTime = RoomCalendarActivity.TIME_FORMAT.format(eventDetail.getEndTime());
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

    private void registerClickListeners() {
        roomNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setTitle(R.string.select_room);
                final String[] roomNames = resources.getStringArray(R.array.room_array);
                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });

                builder.setSingleChoiceItems(R.array.room_array,0,new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String roomName = roomNames[which];
                        String cityName = null;
                        roomName = roomNames[which].substring(roomNames[which].indexOf(resources.getString(R.string.hyphen)) + 1, roomNames[which].length());
                        cityName = roomNames[which].substring(0, roomNames[which].indexOf(resources.getString(R.string.hyphen)));
                        preferences.edit().putString("roomName", roomName).commit();
                        preferences.edit().putString("cityName", cityName).commit();
                        roomNameTextView.setText(roomName);
                        Intent intent = new Intent(context, CalendarService.class);
                        startService(intent);
                    }
                });
                builder.show();

            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BookEventActivity.class);
                intent.putExtra("eventDetails", getEventDetails());
                startActivity(intent);
            }
        });
    }
}
