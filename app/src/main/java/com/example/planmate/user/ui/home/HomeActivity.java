package com.example.planmate.user.ui.home;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.planmate.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home); // Use the layout with NavHostFragment and BottomNavigationView
    }
}
