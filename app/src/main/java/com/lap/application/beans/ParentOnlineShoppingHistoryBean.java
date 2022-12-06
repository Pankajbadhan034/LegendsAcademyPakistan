package com.lap.application.beans;

import java.io.Serializable;

public class ParentOnlineShoppingHistoryBean implements Serializable {
    String image_url;
    String image;
    String product_name;
    String product_id;
    String id;
    String orders_id;
    String combination_id;
    String net_cost;
    String quantity;
    String total_cost;
    String product_cart_id;
    String attributes;

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrders_id() {
        return orders_id;
    }

    public void setOrders_id(String orders_id) {
        this.orders_id = orders_id;
    }

    public String getCombination_id() {
        return combination_id;
    }

    public void setCombination_id(String combination_id) {
        this.combination_id = combination_id;
    }

    public String getNet_cost() {
        return net_cost;
    }

    public void setNet_cost(String net_cost) {
        this.net_cost = net_cost;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getTotal_cost() {
        return total_cost;
    }

    public void setTotal_cost(String total_cost) {
        this.total_cost = total_cost;
    }

    public String getProduct_cart_id() {
        return product_cart_id;
    }

    public void setProduct_cart_id(String product_cart_id) {
        this.product_cart_id = product_cart_id;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }
}
