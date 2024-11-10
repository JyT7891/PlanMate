package com.example.planmate.user.ui.notifications;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class NotificationService {
    private FirebaseFirestore firestore;
    private MutableLiveData<List<NotificationItem>> notificationsLiveData;

    public NotificationService() {
        firestore = FirebaseFirestore.getInstance();
        notificationsLiveData = new MutableLiveData<>();
    }

    public LiveData<List<NotificationItem>> getNotifications(String userId) {
        firestore.collection("notifications")
                .whereEqualTo("userId", userId)
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null) {
                        return;
                    }

                    List<NotificationItem> notifications = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        NotificationItem notification = document.toObject(NotificationItem.class);
                        notifications.add(notification);
                    }
                    notificationsLiveData.setValue(notifications);
                });

        return notificationsLiveData;
    }
}
