package com.thoughtworks.RoomCalendar.utils;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.thoughtworks.RoomCalendar.R;
import com.thoughtworks.RoomCalendar.activity.RoomCalendarActivity;
import com.thoughtworks.RoomCalendar.domain.EventDetails;

import java.util.List;

public class CustomListViewAdapter extends ArrayAdapter<EventDetails> {

    Context context;

    public CustomListViewAdapter(Context context, int resourceId,
                                 List<EventDetails> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView;
        TextView txtTitle;
        TextView txtAuthor;
        TextView txtEventName;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        EventDetails eventDetail = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        convertView = mInflater.inflate(R.layout.list_item, null);
        holder.txtAuthor = (TextView) convertView.findViewById(R.id.eventOwner);
        holder.txtTitle = (TextView) convertView.findViewById(R.id.eventTime);
        holder.txtEventName = (TextView) convertView.findViewById(R.id.eventName);
        holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
        convertView.setTag(holder);

        holder = (ViewHolder) convertView.getTag();

        String eventName = eventDetail.getEventName();

        if (eventName != null) {
            if (eventName.length() < 24) {
                holder.txtEventName.setText(eventName);
            } else {
                holder.txtEventName.setText(eventName.substring(0,24) + "...");
            }
        }

        holder.txtAuthor.setText("by " + eventDetail.getOrganizer().split("@")[0]);
        holder.txtTitle.setText(RoomCalendarActivity.DATE_FORMAT.format(eventDetail.getStartTime()) + "-" + RoomCalendarActivity.DATE_FORMAT.format(eventDetail.getEndTime()));
        holder.imageView.setImageResource(R.drawable.user_icon_for_upcoming_events);
        return convertView;
    }
}