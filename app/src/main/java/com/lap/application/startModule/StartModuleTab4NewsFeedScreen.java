package com.lap.application.startModule;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lap.application.R;
import com.lap.application.child.fragments.ChildPostFragment;
import com.lap.application.coach.fragments.CoachManageAttendanceFragment;
import com.lap.application.coach.fragments.CoachManageScoresFragment;
import com.lap.application.coach.fragments.CoachManageTimelineFragment;
import com.lap.application.parent.fragments.ParentBookNowFragment;
import com.lap.application.startModule.fragment.CoachAreaFragment;

import androidx.appcompat.app.AppCompatActivity;

public class StartModuleTab4NewsFeedScreen extends AppCompatActivity {
    ImageView backButton;
    String typeStr;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_module_news_feed_activity);
        backButton = findViewById(R.id.backButton);
        title = findViewById(R.id.title);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        typeStr = getIntent().getStringExtra("type");

        if(typeStr.equalsIgnoreCase("IDP's")){
            title.setText("IDP'S");
            CoachManageScoresFragment coachManageScoresFragment = new CoachManageScoresFragment();
            StartModuleTab4NewsFeedScreen.this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainFrameLayout, coachManageScoresFragment)
                    .commit();
        }else if(typeStr.equalsIgnoreCase("ATTENDANCE")){
            title.setText("ATTENDANCE");
            CoachManageAttendanceFragment coachManageAttendanceFragment = new CoachManageAttendanceFragment();
            StartModuleTab4NewsFeedScreen.this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainFrameLayout, coachManageAttendanceFragment)
                    .commit();
        }else if(typeStr.equalsIgnoreCase("BOOK NOW")){
            title.setText("ATTENDANCE");
            ParentBookNowFragment parentBookNowFragment = new ParentBookNowFragment();
            StartModuleTab4NewsFeedScreen.this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainFrameLayout, parentBookNowFragment)
                    .commit();
        }else if(typeStr.equalsIgnoreCase("NEWSFEED")){
            title.setText("NEWSFEED");
            CoachManageTimelineFragment coachManageTimelineFragment = new CoachManageTimelineFragment();
            StartModuleTab4NewsFeedScreen.this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainFrameLayout, coachManageTimelineFragment)
                    .commit();


        }else if(typeStr.equalsIgnoreCase("NEWSFEED + POST")){
            title.setText("NEWSFEED");
            ChildPostFragment childPostFragment = new ChildPostFragment();
            StartModuleTab4NewsFeedScreen.this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainFrameLayout, childPostFragment)
                    .commit();
        }else if(typeStr.equalsIgnoreCase("COACH AREA")){
            title.setText("COACH AREA");
            CoachAreaFragment childPostFragment = new CoachAreaFragment();
            StartModuleTab4NewsFeedScreen.this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainFrameLayout, childPostFragment)
                    .commit();
        }




    }
}