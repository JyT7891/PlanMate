package com.example.planmate.eventOrganizer.ui.rsvp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planmate.R;

import java.util.List;
import java.util.Map;

public class AttendeesAdapter extends RecyclerView.Adapter<AttendeesAdapter.ViewHolder> {

    private List<Map<String, String>> attendeesList;

    public AttendeesAdapter(List<Map<String, String>> attendeesList) {
        this.attendeesList = attendeesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendee, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, String> attendee = attendeesList.get(position);
        holder.userName.setText(attendee.get("userName"));
        holder.status.setText(attendee.get("status"));
    }

    @Override
    public int getItemCount() {
        return attendeesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName, status;

        public ViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            status = itemView.findViewById(R.id.status);
        }
    }
}
