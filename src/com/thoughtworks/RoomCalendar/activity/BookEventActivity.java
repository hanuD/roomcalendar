package com.thoughtworks.RoomCalendar.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import com.thoughtworks.RoomCalendar.R;
import com.thoughtworks.RoomCalendar.domain.BookingDetails;
import com.thoughtworks.RoomCalendar.domain.EventDetails;
import com.thoughtworks.RoomCalendar.utils.BookEventTasker;

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
        eventDetails = (ArrayList<EventDetails>) intent.getSerializableExtra("eventDetails");

        initializeActivity();
        registerListeners();

    }

    private void initializeActivity() {
        Calendar cal = Calendar.getInstance();

        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);

        eventNameText = (EditText) findViewById(R.id.eventNameText);
        organizerText = (EditText) findViewById(R.id.organizer);
        startTimePicker = (TimePicker) findViewById(R.id.startTimePicker);
        endTimePicker = (TimePicker) findViewById(R.id.endTimePicker);
        okButton = (Button) findViewById(R.id.okay_button);
        cancelButton = (Button) findViewById(R.id.cancel_button);

        setTimeFor(startTimePicker, hour, min);
        setTimeFor(endTimePicker, hour, min);

    }

    private void setTimeFor(TimePicker timePicker, int hour, int min) {
        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(min);
    }

    private void registerListeners() {
        setCancelButtonListener();
        setOKButtonListener();
    }

    private void setOKButtonListener() {
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                initializeAlertDialog(alertDialogBuilder);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }

            private void initializeAlertDialog(AlertDialog.Builder alertDialogBuilder) {
                alertDialogBuilder.setTitle("Confirmation");
                alertDialogBuilder.setMessage("Are you sure you want to book the event with the id " + organizerText.getText() + ". The event wont be booked if the id is not valid.");
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        bookEventIfAvailable();
                    }

                    private void bookEventIfAvailable() {
                        boolean isEventOverlap = false;

                        if (eventDetails != null && eventDetails.size() > 0) {
                            isEventOverlap = setEventOverlapStatus(getStartTime(), getEndTime(), isEventOverlap);
                        }
                        if (!isEventOverlap) {
                            BookEventTasker eventTasker = new BookEventTasker(context);
                            BookingDetails bookingDetails = new BookingDetails(eventNameText.getText().toString(), organizerText.getText().toString(), getStartTime(), getEndTime());
                            eventTasker.execute(bookingDetails);
                            Toast.makeText(context, "Event booked", Toast.LENGTH_LONG).show();
                            startHomeActivity();
                        } else {
                            Toast.makeText(context, "Event rejected. An event already exists in the given time", Toast.LENGTH_LONG).show();
                            startHomeActivity();
                        }
                    }

                });
                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
            }

        });
    }

    private void setCancelButtonListener() {
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startHomeActivity();
            }
        });
    }

    private Calendar getEndTime() {
        Calendar endTime = Calendar.getInstance();
        endTime.set(Calendar.HOUR_OF_DAY, endTimePicker.getCurrentHour());
        endTime.set(Calendar.MINUTE, endTimePicker.getCurrentMinute());
        return endTime;
    }

    private Calendar getStartTime() {
        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, startTimePicker.getCurrentHour());
        startTime.set(Calendar.MINUTE, startTimePicker.getCurrentMinute());
        return startTime;
    }

    private boolean setEventOverlapStatus(Calendar startTime, Calendar endTime, boolean isEventOverlap) {
        for (EventDetails events : eventDetails) {
            if ((startTime.getTimeInMillis() > events.getStartTime()
                    && startTime.getTimeInMillis() < events.getEndTime()) ||
                    (endTime.getTimeInMillis() > events.getStartTime()
                            && endTime.getTimeInMillis() < events.getEndTime())) {
                isEventOverlap = true;
                break;
            }
        }
        return isEventOverlap;
    }

    private void startHomeActivity() {
        Intent intent = new Intent(context, RoomCalendarActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }
}
