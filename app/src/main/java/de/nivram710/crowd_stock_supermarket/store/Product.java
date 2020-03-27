package de.nivram710.crowd_stock_supermarket.store;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Product implements Serializable, Cloneable {
    private int id;
    private String name;
    private int availability;

    public Product(int id, String name, int availability) {
        this.id = id;
        this.name = name;
        this.availability = availability;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAvailability() {
        return availability;
    }

    public void setAvailability(int availability) {
        this.availability = availability;
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", availability=" + availability +
                '}';
    }
}
