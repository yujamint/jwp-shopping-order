package cart.ui;

import cart.application.CartItemService;
import cart.domain.Member;
import cart.domain.cart.CartItem;
import cart.dto.PagedDataResponse;
import cart.dto.cart.CartItemQuantityUpdateRequest;
import cart.dto.cart.CartItemRequest;
import cart.dto.cart.CartItemResponse;
import java.net.URI;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart-items")
public class CartItemApiController {

    private final CartItemService cartItemService;

    public CartItemApiController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    @GetMapping
    public ResponseEntity<PagedDataResponse<CartItemResponse>> showPagedCartItems(Member member,
                                                                @RequestParam("unit-size") int unitSize,
                                                                @RequestParam int page) {
        final Page<CartItem> pagedCartItems = cartItemService.getPagedCartItems(member, unitSize, page);
        final Page<CartItemResponse> response = pagedCartItems.map(cartItem -> CartItemResponse.from(cartItem));
        return ResponseEntity.ok(PagedDataResponse.from(response));
    }

    @GetMapping("/{cartItemId}")
    public ResponseEntity<CartItemResponse> showCartItemById(Member member, @PathVariable Long cartItemId) {
        final CartItem cartItem = cartItemService.findByCartItemId(member, cartItemId);
        return ResponseEntity.ok(CartItemResponse.from(cartItem));
    }

    @PostMapping
    public ResponseEntity<Void> addCartItems(Member member, @RequestBody CartItemRequest cartItemRequest) {
        Long cartItemId = cartItemService.add(member, cartItemRequest);

        return ResponseEntity.created(URI.create("/cart-items/" + cartItemId)).build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateCartItemQuantity(Member member, @PathVariable Long id, @RequestBody CartItemQuantityUpdateRequest request) {
        cartItemService.updateQuantity(member, id, request);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeCartItems(Member member, @PathVariable Long id) {
        cartItemService.remove(member, id);

        return ResponseEntity.noContent().build();
    }
}
