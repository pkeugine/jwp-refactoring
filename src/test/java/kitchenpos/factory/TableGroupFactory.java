package kitchenpos.factory;

import java.time.LocalDateTime;
import java.util.List;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.TableGroup;

public class TableGroupFactory {

    private Long id;
    private LocalDateTime createdDate;
    private List<OrderTable> orderTables;

    private TableGroupFactory() {

    }

    private TableGroupFactory(Long id, LocalDateTime createdDate, List<OrderTable> orderTables) {
        this.id = id;
        this.createdDate = createdDate;
        this.orderTables = orderTables;
    }

    public static TableGroupFactory builder() {
        return new TableGroupFactory();
    }

    public static TableGroupFactory copy(TableGroup tableGroup) {
        return new TableGroupFactory(
            tableGroup.getId(),
            tableGroup.getCreatedDate(),
            tableGroup.getOrderTables()
        );
    }

    public TableGroupFactory id(Long id) {
        this.id = id;
        return this;
    }

    public TableGroupFactory createdDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public TableGroupFactory orderTables(List<OrderTable> orderTables) {
        this.orderTables = orderTables;
        return this;
    }

    public TableGroup build() {
        TableGroup tableGroup = new TableGroup();
        tableGroup.setId(id);
        tableGroup.setCreatedDate(createdDate);
        tableGroup.setOrderTables(orderTables);
        return tableGroup;
    }
}
