package de.whatsLeft.store;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class to represent single stores
 *
 * @since 1.0.0
 * @author Marvin Jütte
 * @version 1.0
 */
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

    /**
     * Constructor
     *
     * @param id        store id from backend
     * @param name      store name
     * @param address   address of store
     * @param city      city of store
     * @param distance  distance between request location and store
     * @param latitude  latitude of store's position
     * @param longitude longitude of store's position
     * @param products  array list containing all products
     * @param isOpen    boolean representing if current store is open
     */
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

    /**
     * @return storeID store id from backend
     * @since 1.0.0
     */
    public String getId() {
        return id;
    }

    /**
     * @return name
     * @since 1.0.0
     */
    public String getName() {
        return name;
    }

    /**
     * @return address
     * @since 1.0.0
     */
    public String getAddress() {
        return address;
    }

    /**
     * @return city
     * @since 1.0.0
     */
    public String getCity() {
        return city;
    }

    /**
     * @return distance
     * @since 1.0.0
     */
    public double getDistance() {
        return distance;
    }

    /**
     * @return latitude latitude of the stores position
     * @since 1.0.0
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * @return longitude longitude of the stores position
     * @since 1.0.0
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * @return products array list containing all products
     * @since 1.0.0
     */
    public ArrayList<Product> getProducts() {
        return products;
    }

    /**
     * @return isOpen true if the store is open; false if the store is closed
     */
    public boolean isOpen() {
        return isOpen;
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
