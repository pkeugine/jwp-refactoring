package kitchenpos.integration.templates;

import kitchenpos.DomainBuilder;
import kitchenpos.domain.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ProductTemplate extends IntegrationTemplate {

    public static final String PRODUCT_URL = "/api/products";

    public ResponseEntity<Product> create(String name, BigDecimal price) {
        Product product = DomainBuilder.createProduct(name, price);

        return post(
                PRODUCT_URL,
                product,
                Product.class
        );
    }

    public ResponseEntity<Product[]> list() {
        return get(
                PRODUCT_URL,
                Product[].class
        );
    }
}
