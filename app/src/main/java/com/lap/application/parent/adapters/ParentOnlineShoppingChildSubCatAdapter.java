package com.lap.application.parent.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.lap.application.R;
import com.lap.application.beans.ParentOnlineStoreChildSubCatBean;

import java.util.ArrayList;

public class ParentOnlineShoppingChildSubCatAdapter extends BaseAdapter {

    Context context;
    ArrayList<ParentOnlineStoreChildSubCatBean> parentOnlineStoreChildSubCatBeanArrayList;

    LayoutInflater layoutInflater;

    Typeface helvetica;
    Typeface linoType;

    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options;

    public ParentOnlineShoppingChildSubCatAdapter(Context context, ArrayList<ParentOnlineStoreChildSubCatBean> parentOnlineStoreChildSubCatBeanArrayList){
        this.context = context;
        this.parentOnlineStoreChildSubCatBeanArrayList = parentOnlineStoreChildSubCatBeanArrayList;
        layoutInflater = LayoutInflater.from(context);

        helvetica = Typeface.createFromAsset(context.getAssets(),"fonts/HelveticaNeue.ttf");
        linoType = Typeface.createFromAsset(context.getAssets(),"fonts/LinotypeOrdinarRegular.ttf");

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
        return parentOnlineStoreChildSubCatBeanArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return parentOnlineStoreChildSubCatBeanArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.parent_online_shopping_child_sub_cat_adapter, null);

        RelativeLayout mainRelativeLayout = (RelativeLayout) convertView.findViewById(R.id.mainRelativeLayout);
        ImageView image = (ImageView) convertView.findViewById(R.id.image);
        TextView categoryName = (TextView) convertView.findViewById(R.id.categoryName);
        //TextView desc = (TextView) convertView.findViewById(R.id.desc);

        final ParentOnlineStoreChildSubCatBean parentOnlineStoreBean = parentOnlineStoreChildSubCatBeanArrayList.get(position);
        categoryName.setText(parentOnlineStoreBean.getCategoryName());
        //desc.setText(Html.fromHtml(parentOnlineStoreBean.getDescription()));
        imageLoader.displayImage(parentOnlineStoreBean.getImage(), image, options);

        categoryName.setTypeface(linoType);
       // desc.setTypeface(linoType);

//        if(position % 2 == 0){
//            mainRelativeLayout.setBackgroundColor(context.getResources().getColor(R.color.darkBlue));
//            categoryName.setTextColor(context.getResources().getColor(R.color.white));
//            desc.setTextColor(context.getResources().getColor(R.color.white));
//        } else {
//            mainRelativeLayout.setBackgroundColor(context.getResources().getColor(R.color.yellow));
//            categoryName.setTextColor(context.getResources().getColor(R.color.black));
//            desc.setTextColor(context.getResources().getColor(R.color.black));
//        }

        return convertView;
    }
}