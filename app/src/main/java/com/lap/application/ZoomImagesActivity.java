package com.lap.application;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lap.application.beans.PostBeanMultiplImages;
import com.lap.application.utils.Utilities;
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;


public class ZoomImagesActivity extends AppCompatActivity {
    ImageView backButton;
    TextView title;
    SharedPreferences sharedPreferences;
    ViewPager mViewPager;
    SpringDotsIndicator springDotsIndicator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_images);
        backButton = findViewById(R.id.backButton);
        title = findViewById(R.id.title);
        springDotsIndicator = findViewById(R.id.spring_dots_indicator);
        mViewPager = (ViewPager) findViewById(R.id.viewPagerMain);
        sharedPreferences = getSharedPreferences(Utilities.SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        ArrayList<PostBeanMultiplImages> images = (ArrayList<PostBeanMultiplImages>) getIntent().getSerializableExtra("images");

        ZoomImagesAdapter zoomImagesAdapter = new ZoomImagesAdapter(ZoomImagesActivity.this, images);
        mViewPager.setAdapter(zoomImagesAdapter);
        springDotsIndicator.setViewPager(mViewPager);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}