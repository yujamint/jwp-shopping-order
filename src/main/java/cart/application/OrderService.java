package cart.application;

import cart.domain.Member;
import cart.domain.cart.CartItem;
import cart.domain.cart.CartItems;
import cart.domain.order.DiscountPolicy;
import cart.domain.order.FixedDiscountPolicy;
import cart.domain.order.Order;
import cart.domain.order.OrderItems;
import cart.domain.order.Price;
import cart.dto.order.OrderCreateRequest;
import cart.repository.CartItemRepository;
import cart.repository.OrderRepository;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;

    public OrderService(final CartItemRepository cartItemRepository, final OrderRepository orderRepository) {
        this.cartItemRepository = cartItemRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Long order(final Member member, final OrderCreateRequest orderCreateRequest) {
        final List<CartItem> findCartItems = cartItemRepository.findByIds(orderCreateRequest.getCartItemIds());
        final CartItems cartItems = new CartItems(findCartItems, member);

        final OrderItems orderItems = OrderItems.from(cartItems);
        final DiscountPolicy discountPolicy = FixedDiscountPolicy.from(orderItems.sumOfPrice());
        final Order order = new Order(member, orderItems, discountPolicy);

        validateOrderPrice(orderCreateRequest, order);

        final Long orderId = orderRepository.createOrder(order);
        cartItemRepository.deleteByIds(orderCreateRequest.getCartItemIds());

        return orderId;
    }

    private void validateOrderPrice(final OrderCreateRequest orderCreateRequest, final Order order) {
        final Price requestPrice = new Price(orderCreateRequest.getFinalPrice());
        if (!order.getDiscountedPrice().equals(requestPrice)) {
            throw new IllegalArgumentException("계산된 금액이 일치하지 않습니다");
        }
    }

    @Transactional(readOnly = true)
    public Order getOrder(final Member member, final Long orderId) {
        return orderRepository.findById(member, orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문 내역을 찾을 수 없습니다"));
    }

    @Transactional(readOnly = true)
    public List<Order> getAllOrders(final Member member) {
        return orderRepository.findAll(member);
    }

    public List<FixedDiscountPolicy> getFixedDiscountPolicies() {
        return Arrays.stream(FixedDiscountPolicy.values())
                .filter(policy -> policy.getDiscountPrice() != 0)
                .collect(Collectors.toUnmodifiableList());
    }
}
