package com.example.planmate.user.ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;

public class NotificationsViewModel extends ViewModel {
    private NotificationService notificationService;
    private LiveData<List<NotificationItem>> notifications;

    public NotificationsViewModel() {
        notificationService = new NotificationService();
    }

    public LiveData<List<NotificationItem>> getNotifications(String userId) {
        if (notifications == null) {
            notifications = notificationService.getNotifications(userId);
        }
        return notifications;
    }
}
