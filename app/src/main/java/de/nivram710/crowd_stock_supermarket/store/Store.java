package de.nivram710.crowd_stock_supermarket.store;

import android.util.Log;

import java.util.Arrays;

public class Store {

    private String id;
    private String name;
    private String address;
    private double distance;
    private Product[] products;
    private boolean isOpen;

    private static final String TAG = "Store";
    
    public Store(String id, String name, String address, double distance, Product[] products, boolean isOpen) {
        Log.d(TAG, "Store: called");
        this.id = id;
        this.name = name;
        this.address = address;
        this.products = products;
        this.isOpen = isOpen;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Product[] getProducts() {
        return products;
    }

    public void setProducts(Product[] products) {
        this.products = products;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    @Override
    public String toString() {
        return "Store{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", distance=" + distance +
                ", products=" + Arrays.toString(products) +
                ", isOpen=" + isOpen +
                '}';
    }
}
