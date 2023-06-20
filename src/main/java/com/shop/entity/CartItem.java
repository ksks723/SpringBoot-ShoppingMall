package com.shop.entity;

import javax.persistence.*;

public class CartItem {
    @Id
    @GeneratedValue
    @Column(name = "cart_item_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name="cart_id")
    private Cart cart;//하나의 장바구니에 여러개의 상품을 담을 수 있으므로 다대일 관계 @ManyToOne

    @ManyToOne
    @JoinColumn(name="item_id")
    private Item item;//하나의 상품은 여러 장바구니에 상품으로 담길 수 있으므로 다대일 관계 @ManyToOne

    private int count;//같은 상품을 장바구니에 몇개담을지 결정한다.
}
