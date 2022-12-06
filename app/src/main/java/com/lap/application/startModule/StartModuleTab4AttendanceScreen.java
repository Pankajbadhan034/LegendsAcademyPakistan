package com.lap.application.startModule;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.lap.application.R;
import com.lap.application.coach.fragments.CoachManageScoresFragment;

import androidx.appcompat.app.AppCompatActivity;

public class StartModuleTab4AttendanceScreen extends AppCompatActivity {
    ImageView backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_module_tab4_attendance_activity);
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        CoachManageScoresFragment coachManageScoresFragment = new CoachManageScoresFragment();
        StartModuleTab4AttendanceScreen.this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainFrameLayout, coachManageScoresFragment)
                .commit();

//        CoachManageAttendanceFragment coachManageAttendanceFragment = new CoachManageAttendanceFragment();
//        StartModuleTab4AttendanceScreen.this.getSupportFragmentManager().beginTransaction()
//                .replace(R.id.mainFrameLayout, coachManageAttendanceFragment)
//                .commit();
    }
}