package com.lap.application.parent.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lap.application.R;
import com.lap.application.beans.ParentOnlineShoppingHistoryBean;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

public class ParentOnlineShoppingHistoryDetailAdapter extends BaseAdapter {

    Context context;
    ArrayList<ParentOnlineShoppingHistoryBean> parentOnlineShoppingHistoryBeanArrayList;

    LayoutInflater layoutInflater;

    Typeface helvetica;
    Typeface linoType;

    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options;
    String academy_currency;

    public ParentOnlineShoppingHistoryDetailAdapter(Context context, ArrayList<ParentOnlineShoppingHistoryBean> parentOnlineShoppingHistoryBeanArrayList, String academy_currency){
        this.context = context;
        this.parentOnlineShoppingHistoryBeanArrayList = parentOnlineShoppingHistoryBeanArrayList;
        this.academy_currency = academy_currency;
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
        return parentOnlineShoppingHistoryBeanArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return parentOnlineShoppingHistoryBeanArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.parent_online_shopping_history_adapter, null);

        ImageView image = (ImageView) convertView.findViewById(R.id.image);
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView totalPrice = convertView.findViewById(R.id.totalPrice);
        TextView attributeCost = convertView.findViewById(R.id.attributeCost);
        TextView perItemPrice = convertView.findViewById(R.id.perItemPrice);
        TextView quantity = convertView.findViewById(R.id.quantity);

        final ParentOnlineShoppingHistoryBean parentOnlineStoreProductsBean = parentOnlineShoppingHistoryBeanArrayList.get(position);
        name.setText(parentOnlineStoreProductsBean.getProduct_name());
        imageLoader.displayImage(parentOnlineStoreProductsBean.getImage_url()+""+parentOnlineStoreProductsBean.getImage(), image, options);
        totalPrice.setText(parentOnlineStoreProductsBean.getTotal_cost()+" "+academy_currency);
        attributeCost.setText("ATTRIBUTE DATA - "+parentOnlineStoreProductsBean.getAttributes());
        perItemPrice.setText("PER ITEM PRICE - "+parentOnlineStoreProductsBean.getNet_cost()+" "+academy_currency);
        quantity.setText("QUANTITY - "+parentOnlineStoreProductsBean.getQuantity());
        name.setTypeface(linoType);

        return convertView;
    }
}