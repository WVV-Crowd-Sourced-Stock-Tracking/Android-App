package de.nivram710.whatsLeft.store;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Product implements Serializable, Cloneable {
    private int id;
    private String name;
    private String emoticon;
    private int availability;

    public Product(int id, String name, String emoticon, int availability) {
        this.id = id;
        this.name = name;
        this.emoticon = emoticon;
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

    public String getEmoticon() {
        return emoticon;
    }

    public void setEmoticon(String emoticon) {
        this.emoticon = emoticon;
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