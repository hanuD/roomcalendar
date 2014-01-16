package com.thoughtworks.utils;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.thoughtworks.RoomCalendar.R;

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

        convertView = initializeViewHolder(holder);

        holder = (ViewHolder) convertView.getTag();

        setValues(holder, eventDetail);
        return convertView;
    }

    private View initializeViewHolder(ViewHolder holder) {
        View convertView;LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        convertView = mInflater.inflate(R.layout.list_item, null);
        holder.txtAuthor = (TextView) convertView.findViewById(R.id.eventOwner);
        holder.txtTitle = (TextView) convertView.findViewById(R.id.eventTime);
        holder.txtEventName = (TextView) convertView.findViewById(R.id.eventName);
        holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
        convertView.setTag(holder);
        return convertView;
    }

    private void setValues(ViewHolder holder, EventDetails eventDetail) {
        setEventName(holder, eventDetail);

        setAuthor(holder, eventDetail.getAuthor().split("@")[0]);
//        holder.txtTitle.setText(eventDetail.getEventStart().toLocaleString().split(" ")[3] + " " + eventDetail.getEventStart().toLocaleString().split(" ")[4] + " - " + eventDetail.getEventEnd().toLocaleString().split(" ")[3] + "  " + eventDetail.getEventEnd().toLocaleString().split(" ")[4]);
        setEventDetail(holder, eventDetail);
        holder.imageView.setImageResource(R.drawable.user_icon_for_upcoming_events);
    }

    private void setEventDetail(ViewHolder holder, EventDetails eventDetail) {
        holder.txtTitle.setText(getEventDate(eventDetail) + " " + getEventTime(eventDetail));
    }

    private void setAuthor(ViewHolder holder, String s) {
        holder.txtAuthor.setText("by " + s);
    }

    private void setEventName(ViewHolder holder, EventDetails eventDetail) {
        String eventName = eventDetail.getName();

        if (eventName != null) {
            if (eventName.length() < 24) {
                holder.txtEventName.setText(eventName);
            } else {
                holder.txtEventName.setText(eventName.substring(0,24) + "...");
            }
        }
    }

    private String getEventTime(EventDetails eventDetail) {
        return eventDetail.getEventStart().toLocaleString().split(" ")[3].substring(0, eventDetail.getEventStart().toLocaleString().split(" ")[3].length() - 3) + " - " + eventDetail.getEventEnd().toLocaleString().split(" ")[3].substring(0, eventDetail.getEventEnd().toLocaleString().split(" ")[3].length() - 3);
    }

    private String getEventDate(EventDetails eventDetail) {
        return eventDetail.getEventStart().toLocaleString().split(" ")[0] + "-" + eventDetail.getEventStart().toLocaleString().split(" ")[1];
    }
}