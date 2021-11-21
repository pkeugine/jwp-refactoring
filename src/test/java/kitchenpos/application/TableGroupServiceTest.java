package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import kitchenpos.dao.OrderRepository;
import kitchenpos.dao.OrderTableRepository;
import kitchenpos.dao.TableGroupRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.TableGroup;
import kitchenpos.dto.TableGroupRequest;
import kitchenpos.dto.TableGroupResponse;
import kitchenpos.factory.OrderTableFactory;
import kitchenpos.factory.TableGroupFactory;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.test.util.ReflectionTestUtils;

@MockitoSettings
class TableGroupServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderTableRepository orderTableRepository;

    @Mock
    private TableGroupRepository tableGroupRepository;

    @InjectMocks
    private TableGroupService tableGroupService;

    @Nested
    class CreateTest {

        private OrderTable orderTable1;

        private OrderTable savedOrderTable1;

        private OrderTable savedOrderTable2;

        private List<OrderTable> savedOrderTables;

        private List<Long> orderTableIds;

        private TableGroup tableGroup;

        private Long savedTableGroupId;

        private TableGroup savedTableGroup;

        private TableGroupRequest tableGroupRequest;

        @BeforeEach
        void setUp() {
            orderTable1 = OrderTableFactory.builder()
                .id(1L)
                .tableGroupId(null)
                .numberOfGuests(0)
                .empty(true)
                .build();

            OrderTable orderTable2 = OrderTableFactory.builder()
                .id(2L)
                .tableGroupId(null)
                .numberOfGuests(0)
                .empty(true)
                .build();

            orderTableIds = Arrays.asList(
                orderTable1.getId(),
                orderTable2.getId()
            );

            savedOrderTable1 = OrderTableFactory.copy(orderTable1).build();

            savedOrderTable2 = OrderTableFactory.copy(orderTable2).build();

            savedOrderTables = Arrays.asList(
                savedOrderTable1,
                savedOrderTable2
            );

            tableGroup = TableGroupFactory.builder()
                .orderTables(orderTable1, orderTable2)
                .build();

            savedTableGroupId = 1L;

            savedTableGroup = TableGroupFactory.copy(tableGroup)
                .id(savedTableGroupId)
                .orderTables(savedOrderTable1, savedOrderTable2)
                .build();

            tableGroupRequest = TableGroupFactory.dto(tableGroup);
        }

        @DisplayName("TableGroup 을 생성한다")
        @Test
        void create() {
            // given
            given(orderTableRepository.findAllByIdIn(orderTableIds)).willReturn(savedOrderTables);
            given(tableGroupRepository.save(any(TableGroup.class))).willAnswer(
                invocation -> {
                    TableGroup toSave = invocation.getArgument(0);
                    ReflectionTestUtils.setField(toSave, "id", savedTableGroupId);
                    return null;
                }
            );

            // when
            TableGroupResponse result = tableGroupService.create(tableGroupRequest);

            // then
            assertThat(result.getId()).isEqualTo(savedTableGroup.getId());
            assertThat(result.getOrderTables())
                .usingRecursiveComparison()
                .isEqualTo(savedTableGroup.getOrderTables().toList());
        }

        @DisplayName("TableGroup 생성 실패한다 - 요청한 orderTables 가 비어있는 경우")
        @Test
        void createFail_whenOrderTablesAreEmpty() {
            // given
            tableGroup = TableGroupFactory.copy(tableGroup)
                .orderTables()
                .build();
            tableGroupRequest = TableGroupFactory.dto(tableGroup);

            // when
            ThrowingCallable throwingCallable = () -> tableGroupService.create(tableGroupRequest);

            // then
            assertThatThrownBy(throwingCallable)
                .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("TableGroup 생성 실패한다 - 요청한 orderTable 의 수가 2 개 미만인 경우")
        @Test
        void createFail_whenOrderTablesAreLessThanTwo() {
            // given
            tableGroup = TableGroupFactory.copy(tableGroup)
                .orderTables(orderTable1)
                .build();
            tableGroupRequest = TableGroupFactory.dto(tableGroup);

            // when
            ThrowingCallable throwingCallable = () -> tableGroupService.create(tableGroupRequest);

            // then
            assertThatThrownBy(throwingCallable)
                .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("TableGroup 생성 실패한다 - 요청한 orderTable 중 존재하지 않는 것이 있는 경우")
        @Test
        void createFail_whenOrderTableDoesNotExist() {
            // given
            given(orderTableRepository.findAllByIdIn(orderTableIds)).willReturn(
                Collections.singletonList(savedOrderTable1));

            // when
            ThrowingCallable throwingCallable = () -> tableGroupService.create(tableGroupRequest);

            // then
            assertThatThrownBy(throwingCallable)
                .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("TableGroup 생성 실패한다 - 요청한 orderTable 이 비어있지 않는 경우")
        @Test
        void createFail_whenOrderTableIsNotEmpty() {
            // given
            savedOrderTable1 = OrderTableFactory.copy(savedOrderTable1)
                .empty(false)
                .build();
            savedOrderTables = Arrays.asList(
                savedOrderTable1,
                savedOrderTable2
            );
            given(orderTableRepository.findAllByIdIn(orderTableIds)).willReturn(savedOrderTables);

            // when
            ThrowingCallable throwingCallable = () -> tableGroupService.create(tableGroupRequest);

            // then
            assertThatThrownBy(throwingCallable)
                .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("TableGroup 생성 실패한다 - 요청한 orderTable 이 이미 tableGroup 을 가지고 있는 경우")
        @Test
        void createFail_whenOrderTableAlreadyHasTableGroupId() {
            // given
            savedOrderTable1 = OrderTableFactory.copy(savedOrderTable1)
                .tableGroupId(2L)
                .build();
            savedOrderTables = Arrays.asList(
                savedOrderTable1,
                savedOrderTable2
            );
            given(orderTableRepository.findAllByIdIn(orderTableIds)).willReturn(savedOrderTables);

            // when
            ThrowingCallable throwingCallable = () -> tableGroupService.create(tableGroupRequest);

            // then
            assertThatThrownBy(throwingCallable)
                .isExactlyInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class UngroupTest {

        private List<OrderTable> orderTables;

        private List<Long> orderTableIds;

        private Long tableGroupId;

        private List<OrderStatus> notCompletionOrderStatuses;

        @BeforeEach
        void setUp() {
            OrderTable orderTable1 = OrderTableFactory.builder()
                .id(1L)
                .tableGroupId(tableGroupId)
                .numberOfGuests(0)
                .empty(false)
                .build();

            OrderTable orderTable2 = OrderTableFactory.builder()
                .id(2L)
                .tableGroupId(tableGroupId)
                .numberOfGuests(0)
                .empty(false)
                .build();

            orderTables = Arrays.asList(
                orderTable1,
                orderTable2
            );

            orderTableIds = Arrays.asList(
                orderTable1.getId(),
                orderTable2.getId()
            );

            TableGroup tableGroup = TableGroupFactory.builder()
                .id(1L)
                .orderTables(orderTable1, orderTable2)
                .build();

            tableGroupId = tableGroup.getId();

            notCompletionOrderStatuses = Arrays.asList(
                OrderStatus.COOKING,
                OrderStatus.MEAL
            );
        }

        @DisplayName("TableGroup 을 ungroup 한다")
        @Test
        void ungroup() {
            // given
            given(orderTableRepository.findAllByTableGroupId(tableGroupId)).willReturn(orderTables);
            given(orderRepository.existsByOrderTableIdInAndOrderStatusIn(
                orderTableIds,
                notCompletionOrderStatuses
            )).willReturn(false);

            // when
            ThrowingCallable throwingCallable = () -> tableGroupService.ungroup(tableGroupId);

            // then
            assertThatCode(throwingCallable)
                .doesNotThrowAnyException();
        }

        @DisplayName("TableGroup 을 ungroup 하는 것을 실패한다 - 요청한 orderTable 의 order 상태가 COMPLETION 이 아닌 경우")
        @Test
        void ungroupFail_whenOrderTableStatusIsNotCompletion() {
            // given
            given(orderTableRepository.findAllByTableGroupId(tableGroupId)).willReturn(orderTables);
            given(orderRepository.existsByOrderTableIdInAndOrderStatusIn(
                orderTableIds,
                notCompletionOrderStatuses
            )).willReturn(true);

            // when
            ThrowingCallable throwingCallable = () -> tableGroupService.ungroup(tableGroupId);

            // then
            assertThatThrownBy(throwingCallable)
                .isExactlyInstanceOf(IllegalArgumentException.class);
        }
    }
}
