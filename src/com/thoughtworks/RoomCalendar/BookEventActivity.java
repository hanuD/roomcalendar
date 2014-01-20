package com.thoughtworks.RoomCalendar;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

public class BookEventActivity extends Activity {

    EditText eventNameText;
    EditText organizerText;
    TimePicker startTimePicker;
    TimePicker endTimePicker;
    Button okButton;
    TimePicker.OnTimeChangedListener mStartTimeChangedListener;
    private TimePicker.OnTimeChangedListener mNullTimeChangedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_event);

        eventNameText = (EditText) findViewById(R.id.eventNameText);
        organizerText = (EditText) findViewById(R.id.organizer);
        startTimePicker = (TimePicker) findViewById(R.id.startTimePicker);
        startTimePicker.setIs24HourView(true);


        endTimePicker = (TimePicker) findViewById(R.id.endTimePicker);
        endTimePicker.setIs24HourView(true);
        okButton = (Button) findViewById(R.id.okay_button);

        mStartTimeChangedListener =
                new TimePicker.OnTimeChangedListener() {

                    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                        updateDisplay(view, hourOfDay, minute);
                    }
                };

        mNullTimeChangedListener = new TimePicker.OnTimeChangedListener() {

                    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

                    }
                };

        startTimePicker.setOnTimeChangedListener(mStartTimeChangedListener);
    }

    private void updateDisplay(TimePicker timePicker, int hourOfDay, int minute) {

        // do calculation of next time
        int nextMinute = 0;
        if (minute >= 45 && minute <= 59)
            nextMinute = 45;
        else if (minute >= 30)
            nextMinute = 30;
        else if (minute >= 15)
            nextMinute = 15;
        else if (minute > 0)
            nextMinute = 0;
        else {
            nextMinute = 45;
        }

        // remove ontimechangedlistener to prevent stackoverflow/infinite loop
            timePicker.setOnTimeChangedListener(mNullTimeChangedListener);

        // set minute
        timePicker.setCurrentMinute(nextMinute);

        // hook up ontimechangedlistener again
        timePicker.setOnTimeChangedListener(mStartTimeChangedListener);


    }

}
