package de.nivram710.whatsLeft.store;

import java.io.Serializable;
import java.util.ArrayList;

public class Store implements Serializable {

    private String id;
    private String name;
    private String address;
    private String city;
    private double distance;
    private double latitude;
    private double longitude;
    private ArrayList<Product> products;
    private boolean isOpen;

    public Store(String id, String name, String address, String city, double distance, double latitude, double longitude, ArrayList<Product> products, boolean isOpen) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.distance = distance;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void addProduct(Product product) {
        this.products.add(product);
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
                ", products=" + products +
                ", isOpen=" + isOpen +
                '}';
    }
}
