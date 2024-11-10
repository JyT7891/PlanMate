package com.example.planmate.user.ui.event;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EventViewModel extends ViewModel {

    private final MutableLiveData<String> eventDetails;

    public EventViewModel() {
        eventDetails = new MutableLiveData<>();
        eventDetails.setValue("This is the Event fragment");
    }

    public LiveData<String> getEventDetails() {
        return eventDetails;
    }
}
