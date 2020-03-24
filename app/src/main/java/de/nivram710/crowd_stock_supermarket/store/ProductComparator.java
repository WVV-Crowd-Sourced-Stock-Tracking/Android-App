package de.nivram710.crowd_stock_supermarket.store;

import java.util.Comparator;

public class ProductComparator implements Comparator<Product> {
    @Override
    public int compare(Product product, Product t1) {
        return Integer.compare(product.getAvailability(), t1.getAvailability());
    }
}
