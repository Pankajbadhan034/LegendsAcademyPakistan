package com.lap.application.child;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.lap.application.R;
import com.lap.application.child.fragments.ChildCreatePostFragment;

public class ChildBlankActivityForCreatePost extends AppCompatActivity {

    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.child_activity_blank_activity_for_create_post);

        backButton = findViewById(R.id.backButton);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainFrameLayout, new ChildCreatePostFragment())
                .commit();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}