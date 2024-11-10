package com.example.planmate.user.ui.notifications;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.planmate.R;
import com.example.planmate.user.ui.notifications.EventDetailsActivity;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private List<NotificationItem> notifications;
    private Context context;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());

    public NotificationAdapter(List<NotificationItem> notifications, Context context) {
        this.notifications = notifications;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationItem notification = notifications.get(position);
        holder.messageTextView.setText(notification.getMessage());

        // Format and set the timestamp if it's not null
        Timestamp timestamp = notification.getTimestamp();
        if (timestamp != null) {
            holder.timestampTextView.setText(dateFormat.format(timestamp.toDate()));
        } else {
            holder.timestampTextView.setText("No date available");
        }

        // Set up a click listener on the notification item
        holder.itemView.setOnClickListener(v -> {
            // Open EventDetailsActivity, passing the eventId
            Intent intent = new Intent(context, EventDetailsActivity.class);
            intent.putExtra("eventId", notification.getEventId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView timestampTextView;

        ViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.notification_message);
            timestampTextView = itemView.findViewById(R.id.notification_timestamp);
        }
    }
}
