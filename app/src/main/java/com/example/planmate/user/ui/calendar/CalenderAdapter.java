package com.example.planmate.user.ui.calendar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {

    private final ArrayList<String> datesList;

    public CalendarAdapter(ArrayList<String> datesList) {
        this.datesList = datesList;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new CalendarViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        String date = datesList.get(position);
        holder.dateTextView.setText(date);

        // Highlight current day
        if (date.equals("11")) {  // Replace this with actual logic for the current day
            holder.dateTextView.setBackgroundColor(0xFF9C27B0); // Highlight color (purple)
        } else {
            holder.dateTextView.setBackgroundColor(0x00000000); // No background color
        }
    }

    @Override
    public int getItemCount() {
        return datesList.size();
    }

    public static class CalendarViewHolder extends RecyclerView.ViewHolder {
        public TextView dateTextView;

        public CalendarViewHolder(View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(android.R.id.text1);
        }
    }
}
