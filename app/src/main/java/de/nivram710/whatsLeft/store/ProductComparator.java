package de.nivram710.whatsLeft.store;

import java.util.Comparator;

public class ProductComparator implements Comparator<Product> {
    @Override
    public int compare(Product product, Product t1) {
        return Integer.compare(product.getAvailability(), t1.getAvailability());
    }
}
