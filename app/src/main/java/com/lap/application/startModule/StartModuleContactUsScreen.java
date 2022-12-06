package com.lap.application.startModule;

import android.os.Bundle;

import com.lap.application.R;
import com.lap.application.parent.fragments.ParentContactCoachFragment;

import androidx.appcompat.app.AppCompatActivity;

public class StartModuleContactUsScreen extends AppCompatActivity {
   // ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_module_contact_us_activity);
//        backButton = findViewById(R.id.backButton);
//
//        backButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });

        ParentContactCoachFragment parentContactCoachFragment = new ParentContactCoachFragment();
        StartModuleContactUsScreen.this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainFrameLayout, parentContactCoachFragment)
                .commit();


    }
}