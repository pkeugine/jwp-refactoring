package kitchenpos.integration.templates;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class MenuTemplate extends IntegrationTemplate {

    public static final String MENU_URL = "/api/menus";

    public ResponseEntity<Menu> create(String name,
                                       BigDecimal price,
                                       Long menuGroupId,
                                       List<MenuProduct> menuProducts) {
        Menu menu = new Menu();
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuGroupId(menuGroupId);
        menu.setMenuProducts(
                menuProducts
        );

        return post(
                MENU_URL,
                menu,
                Menu.class
        );
    }

    public ResponseEntity<Menu[]> list() {
        return get(
                MENU_URL,
                Menu[].class
        );
    }
}
