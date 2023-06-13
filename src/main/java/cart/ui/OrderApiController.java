package cart.ui;

import cart.application.OrderService;
import cart.domain.Member;
import cart.domain.order.Order;
import cart.dto.order.OrderCreateRequest;
import cart.dto.order.OrderSelectResponse;
import cart.dto.order.OrderSimpleInfoResponse;
import cart.dto.order.discountpolicy.DiscountPolicyResponse;
import cart.dto.order.discountpolicy.FixedDiscountPolicyResponse;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderApiController {

    private final OrderService orderService;

    public OrderApiController(final OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Void> createOrder(Member member, @RequestBody OrderCreateRequest orderCreateRequest) {
        Long orderId = orderService.order(member, orderCreateRequest);

        return ResponseEntity.created(URI.create("/orders/" + orderId)).build();
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderSelectResponse> showOrderById(Member member, @PathVariable Long orderId) {
        final Order order = orderService.getOrder(member, orderId);
        final OrderSelectResponse response = OrderSelectResponse.from(order);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<OrderSimpleInfoResponse>> showOrders(Member member) {
        final List<Order> orders = orderService.getAllOrders(member);
        final List<OrderSimpleInfoResponse> response = orders.stream()
                .map(order -> OrderSimpleInfoResponse.from(order))
                .collect(Collectors.toUnmodifiableList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/discount-policies")
    public ResponseEntity<DiscountPolicyResponse> showAllDiscountPolicies(Member member) {
        final List<FixedDiscountPolicyResponse> response = orderService.getFixedDiscountPolicies().stream()
                .map(policy -> FixedDiscountPolicyResponse.from(policy))
                .collect(Collectors.toUnmodifiableList());

        return ResponseEntity.ok(new DiscountPolicyResponse(response));
    }
}
