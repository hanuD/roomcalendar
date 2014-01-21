package com.thoughtworks.RoomCalendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import com.thoughtworks.utils.BookEventController;
import com.thoughtworks.utils.EventDetails;

import java.util.ArrayList;
import java.util.Calendar;

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

        eventNameText = (EditText) findViewById(R.id.eventNameText);
        organizerText = (EditText) findViewById(R.id.organizer);
        startTimePicker = (TimePicker) findViewById(R.id.startTimePicker);
        startTimePicker.setIs24HourView(true);


        endTimePicker = (TimePicker) findViewById(R.id.endTimePicker);
        endTimePicker.setIs24HourView(true);
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
                endTime.set(Calendar.HOUR_OF_DAY,endTimePicker.getCurrentHour());
                endTime.set(Calendar.MINUTE, endTimePicker.getCurrentMinute());

                boolean isEventOverlap = false;

//                for (EventDetails events : eventDetails) {
//                    if (events.getStartTime() < startTime.getTimeInMillis() && startTime.getTimeInMillis() < events.getEndTime()) {
//                        isEventOverlap = true;
//                    }
//                }
                if (true) {
                    BookEventController bookController = new BookEventController(context);
                    bookController.bookEventForCalendar(eventNameText.getText().toString(), organizerText.getText().toString(), startTime, endTime);
                    Toast.makeText(context, "Event booked", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, RoomCalendarActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }  else {
                    Toast.makeText(context, "Event exists", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, RoomCalendarActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });
    }

}
