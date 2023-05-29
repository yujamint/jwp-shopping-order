package cart.dto;

import cart.domain.order.Order;
import java.time.LocalDateTime;
import java.util.List;

public class OrderSelectResponse {
    private final Long id;
    private final int originalPrice;
    private final int discountPrice;
    private final int discountedPrice;
    private final List<OrderItemSelectResponse> cartItems;
    private final LocalDateTime createdAt;

    private OrderSelectResponse(final Long id,
                               final int originalPrice,
                               final int discountPrice,
                               final int discountedPrice,
                               final List<OrderItemSelectResponse> cartItems,
                               final LocalDateTime createdAt) {
        this.id = id;
        this.originalPrice = originalPrice;
        this.discountPrice = discountPrice;
        this.discountedPrice = discountedPrice;
        this.cartItems = cartItems;
        this.createdAt = createdAt;
    }

    public static OrderSelectResponse from(Order order) {
        return new OrderSelectResponse(
                order.getId(),
                order.getOriginalPrice().getValue(),
                order.getOriginalPrice().getValue() - order.getDiscountedPrice().getValue(),
                order.getDiscountedPrice().getValue(),
                OrderItemSelectResponse.from(order.getOrderItems()),
                order.getCreatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public int getOriginalPrice() {
        return originalPrice;
    }

    public int getDiscountPrice() {
        return discountPrice;
    }

    public int getDiscountedPrice() {
        return discountedPrice;
    }

    public List<OrderItemSelectResponse> getCartItems() {
        return cartItems;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
