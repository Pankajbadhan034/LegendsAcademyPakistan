package com.lap.application.startModule;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.lap.application.R;
import com.lap.application.startModule.fragment.CoachCreatePostFragment;

import androidx.appcompat.app.AppCompatActivity;

public class CoachBlankActivityForCreatePostNew extends AppCompatActivity {
    ImageView backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coach_blank_for_create_post_new_activity);

        backButton = findViewById(R.id.backButton);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainFrameLayout, new CoachCreatePostFragment())
                .commit();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}