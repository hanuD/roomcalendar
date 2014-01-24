package com.thoughtworks.RoomCalendar.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import com.thoughtworks.RoomCalendar.R;
import com.thoughtworks.RoomCalendar.utils.BookEventTasker;
import com.thoughtworks.RoomCalendar.domain.BookingDetails;
import com.thoughtworks.RoomCalendar.domain.EventDetails;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class BookEventActivity extends Activity {

    EditText eventNameText;
    EditText organizerText;
    TimePicker startTimePicker;
    TimePicker endTimePicker;
    Button okButton;
    Button cancelButton;
    Context context;
    private ArrayList<EventDetails> eventDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_event);
        context = this;
        Intent intent = getIntent();
        eventDetails = (ArrayList<EventDetails>) intent.getSerializableExtra("eventDetail");

        Calendar cal=Calendar.getInstance();

        int hour=cal.get(Calendar.HOUR_OF_DAY);
        int min=cal.get(Calendar.MINUTE);

        eventNameText = (EditText) findViewById(R.id.eventNameText);
        organizerText = (EditText) findViewById(R.id.organizer);
        startTimePicker = (TimePicker) findViewById(R.id.startTimePicker);
        startTimePicker.setIs24HourView(true);
        startTimePicker.setCurrentHour(hour);
        startTimePicker.setCurrentMinute(min);

        endTimePicker = (TimePicker) findViewById(R.id.endTimePicker);
        endTimePicker.setIs24HourView(true);
        endTimePicker.setCurrentHour(hour);
        endTimePicker.setCurrentMinute(min);


        okButton = (Button) findViewById(R.id.okay_button);
        cancelButton = (Button) findViewById(R.id.cancel_button);

        registerListeners();

    }

    private void registerListeners() {
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RoomCalendarActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar startTime = Calendar.getInstance();
                startTime.set(Calendar.HOUR_OF_DAY, startTimePicker.getCurrentHour());
                startTime.set(Calendar.MINUTE, startTimePicker.getCurrentMinute());

                Calendar endTime = Calendar.getInstance();
                endTime.set(Calendar.HOUR_OF_DAY, endTimePicker.getCurrentHour());
                endTime.set(Calendar.MINUTE, endTimePicker.getCurrentMinute());

                boolean isEventOverlap = false;

                if (eventDetails != null && eventDetails.size() > 0) {
                    System.out.println("inside if");
                    for (EventDetails events : eventDetails) {
                        if (startTime.getTimeInMillis() < events.getEndTime()) {
                            isEventOverlap = true;
                            break;
                        }
                    }
                }
                if (!isEventOverlap) {
                    BookEventTasker eventTasker = new BookEventTasker(context);
                    BookingDetails bookingDetails = new BookingDetails(eventNameText.getText().toString(), organizerText.getText().toString(), startTime, endTime);
                    eventTasker.execute(bookingDetails);
                    try {
                        System.out.println(eventTasker.get());
                        Toast.makeText(context, "Event booked", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(context, RoomCalendarActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(context, "Event rejected. An event already exists in the given time", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, RoomCalendarActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });
    }
}
