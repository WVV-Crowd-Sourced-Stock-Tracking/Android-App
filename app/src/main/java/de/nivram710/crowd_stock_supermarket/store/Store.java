package de.nivram710.crowd_stock_supermarket.store;

public class Store {

    private int id;
    private String name;
    private Product[] products;
    private boolean isClosed;

    public Store(int id, String name, Product[] products, boolean isClosed) {
        this.id = id;
        this.name = name;
        this.products = products;
        this.isClosed = isClosed;
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

    public Product[] getProducts() {
        return products;
    }

    public void setProducts(Product[] products) {
        this.products = products;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }
}
