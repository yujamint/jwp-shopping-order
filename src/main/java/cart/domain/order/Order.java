package cart.domain.order;

import cart.domain.Member;
import java.time.LocalDateTime;
import java.util.List;

public class Order {

    private final Long id;
    private final Member member;
    private final OrderItems orderItems;
    private final DiscountPolicy discountPolicy;
    private final LocalDateTime createdAt;

    public Order(final Member member, final OrderItems orderItems, final DiscountPolicy discountPolicy) {
        this(null, member, orderItems, discountPolicy, null);
    }

    public Order(final Long id, final Member member, final OrderItems orderItems, final DiscountPolicy discountPolicy,
                 final LocalDateTime createdAt) {
        this.id = id;
        this.member = member;
        this.orderItems = orderItems;
        this.discountPolicy = discountPolicy;
        this.createdAt = createdAt;
    }

    public Price getOriginalPrice() {
        return orderItems.sumOfPrice();
    }

    public Price getDiscountedPrice() {
        final Price originalPrice = orderItems.sumOfPrice();
        return discountPolicy.discount(originalPrice);
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems.getOrderItems();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
