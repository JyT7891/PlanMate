package com.example.planmate.user.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.planmate.R;
import com.google.firebase.auth.FirebaseAuth;

public class NotificationsFragment extends Fragment {
    private NotificationsViewModel notificationsViewModel;
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        recyclerView = view.findViewById(R.id.recycler_notifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        auth = FirebaseAuth.getInstance();
        notificationsViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);

        String userId = auth.getCurrentUser().getUid();
        notificationsViewModel.getNotifications(userId).observe(getViewLifecycleOwner(), notificationItems -> {
            if (notificationItems != null && !notificationItems.isEmpty()) {
                adapter = new NotificationAdapter(notificationItems, getContext());
                recyclerView.setAdapter(adapter);
            }
        });

        return view;
    }
}
