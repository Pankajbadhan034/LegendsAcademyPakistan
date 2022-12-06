package com.lap.application;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.lap.application.beans.PostBeanMultiplImages;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

public class ZoomImagesAdapter extends PagerAdapter {

    Context context;
    ArrayList<PostBeanMultiplImages> stringArrayList;
    LayoutInflater layoutInflater;
    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options;

    // Viewpager Constructor
    public ZoomImagesAdapter(Context context, ArrayList<PostBeanMultiplImages> stringArrayList) {
        this.context = context;
        this.stringArrayList = stringArrayList;
//        layoutInflater = LayoutInflater.from(context);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.placeholder)
                .showImageForEmptyUri(R.drawable.placeholder)
                .showImageOnFail(R.drawable.placeholder)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    @Override
    public int getCount() {
        return stringArrayList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((LinearLayout) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {

        View convertView = layoutInflater.inflate(R.layout.zoom_images_adapter_item, null);

        ImageViewTouch image = (ImageViewTouch) convertView.findViewById(R.id.image);

        final PostBeanMultiplImages sliderBean = stringArrayList.get(position);
        imageLoader.displayImage(sliderBean.getFilePath(), image, options);

        image.setDisplayType(ImageViewTouchBase.DisplayType.FIT_IF_BIGGER);

        // Adding the View
        Objects.requireNonNull(container).addView(convertView);

        return convertView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView((LinearLayout) object);
    }
}

