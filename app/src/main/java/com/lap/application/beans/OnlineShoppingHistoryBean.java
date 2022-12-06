package com.lap.application.beans;

import java.io.Serializable;

public class OnlineShoppingHistoryBean implements Serializable {
   //sub orders
    String product_name;
    String product_id;
    String id;
    String orders_id;
    String combination_id;
    String net_cost;
    String quantity;
    String total_cost;
    String created_at;
    String modify_at;
    String product_cart_id;

    // discount inline
    String discount_label;
    String discount_value;
    String deduct_type;
    String deduct_value;





}
