package com.example.planmate.eventOrganizer.ui.rsvp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planmate.R;

import java.util.List;
import java.util.Map;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<Map<String, String>> users;
    private final List<String> selectedUserIds;

    public UserAdapter(List<Map<String, String>> users, List<String> selectedUserIds) {
        this.users = users;
        this.selectedUserIds = selectedUserIds;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, String> user = users.get(position);
        String userId = user.get("userId");
        String username = user.get("username");

        holder.textViewUserName.setText(username != null ? username : "Unknown User");

        // Set the CheckBox based on whether the user ID is in selectedUserIds
        holder.checkBox.setOnCheckedChangeListener(null); // Prevents triggering listener on binding
        holder.checkBox.setChecked(selectedUserIds.contains(userId));

        // Add or remove the user ID from selectedUserIds when CheckBox is checked/unchecked
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedUserIds.add(userId);
            } else {
                selectedUserIds.remove(userId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUserName;
        CheckBox checkBox;

        ViewHolder(View itemView) {
            super(itemView);
            textViewUserName = itemView.findViewById(R.id.textViewUserName);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }

    // Update the users list and notify adapter of data change
    public void updateList(List<Map<String, String>> filteredList) {
        this.users = filteredList;
        notifyDataSetChanged();
    }
}
