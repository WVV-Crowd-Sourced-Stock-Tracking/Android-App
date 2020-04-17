package de.whatsLeft.store;

import java.util.Comparator;

/**
 * Needed to sort the products list properly
 *
 * @since 1.0.0
 * @author Marvin Jütte
 * @version 1.0
 */
public class ProductComparator implements Comparator<Product> {
    @Override
    public int compare(Product product, Product t1) {
        return Integer.compare(product.getAvailability(), t1.getAvailability());
    }
}
