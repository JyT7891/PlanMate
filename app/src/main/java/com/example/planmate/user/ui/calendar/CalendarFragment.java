package com.example.planmate.user.ui.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.planmate.databinding.FragmentCalendarBinding;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CalendarFragment extends Fragment {

    private FragmentCalendarBinding binding;
    private ArrayList<String> datesList;
    private CalendarAdapter calendarAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Set dynamic month title
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1; // Calendar month is 0-based
        int year = calendar.get(Calendar.YEAR);
        String monthTitle = String.format(Locale.getDefault(), "%s %d", getMonthName(month), year); // Using explicit Locale
        TextView textView = binding.textCalendar;
        textView.setText(monthTitle);

        // Set up RecyclerView
        RecyclerView calendarRecyclerView = binding.calendarRecyclerView;
        datesList = new ArrayList<>();
        calendarAdapter = new CalendarAdapter(datesList);
        calendarRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 7)); // 7 columns for each day of the week
        calendarRecyclerView.setAdapter(calendarAdapter);

        // Load the dates into the RecyclerView
        loadCalendarDates();

        // Handle date selection from the CalendarView widget
        CalendarView calendarView = binding.calendarView;
        calendarView.setDate(calendar.getTimeInMillis(), true, true); // Set the current date
        calendarView.setOnDateChangeListener((view, year1, month1, dayOfMonth) -> {
            String date = "Selected Date: " + dayOfMonth + "/" + (month1 + 1) + "/" + year1;
            Toast.makeText(getContext(), date, Toast.LENGTH_SHORT).show();
        });

        return root;
    }

    private void loadCalendarDates() {
        Calendar calendar = Calendar.getInstance();
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 1; i <= daysInMonth; i++) {
            datesList.add(String.valueOf(i));
        }
        calendarAdapter.notifyDataSetChanged();
    }

    // Helper function to get the month name
    private String getMonthName(int month) {
        // Use DateFormatSymbols with Locale for accurate month name
        Locale locale = Locale.getDefault();
        return new DateFormatSymbols(locale).getMonths()[month - 1]; // Adjust for 0-based index
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Optional: used to release reference to the binding object
    }
}
