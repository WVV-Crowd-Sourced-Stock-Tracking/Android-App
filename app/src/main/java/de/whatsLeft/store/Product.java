package de.whatsLeft.store;

import androidx.annotation.NonNull;

import java.io.Serializable;

/**
 * Class to represent single products of stores
 *
 * @since 1.0.0
 * @author Marvin Jütte
 * @version 1.0
 */
public class Product implements Serializable, Cloneable {
    private int id;
    private String name;
    private String emoticon;
    private int availability;

    /**
     * Constructor
     *
     * @param id product id from backend
     * @param name product name
     * @param emoticon product emoticon
     * @param availability product stock of market
     * @since 1.0.0
     */
    public Product(int id, String name, String emoticon, int availability) {
        this.id = id;
        this.name = name;
        this.emoticon = emoticon;
        this.availability = availability;
    }

    /**
     * @return id id of product
     * @since 1.0.0
     */
    public int getId() {
        return id;
    }

    /**
     * @return name product name
     * @since 1.0.0
     */
    public String getName() {
        return name;
    }

    /**
     * @return emoticon emoticon representing product
     * @since 1.0.0
     */
    public String getEmoticon() {
        return emoticon;
    }

    /**
     * @return availability availability of product in store
     * @since 1.0.0
     */
    public int getAvailability() {
        return availability;
    }

    /**
     * Sets the availability of the product
     * @param availability new availability
     * @since 1.0.0
     */
    public void setAvailability(int availability) {
        this.availability = availability;
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @NonNull
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", availability=" + availability +
                '}';
    }
}
