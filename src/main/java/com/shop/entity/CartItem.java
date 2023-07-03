package com.shop.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
@Getter
@Setter
@Entity
@Table(name = "cart_item")
public class CartItem extends BaseEntity{
    @Id
    @GeneratedValue
    @Column(name = "cart_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="cart_id")
    private Cart cart;//하나의 장바구니에 여러개의 상품을 담을 수 있으므로 다대일 관계 @ManyToOne

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="item_id")
    private Item item;//하나의 상품은 여러 장바구니에 상품으로 담길 수 있으므로 다대일 관계 @ManyToOne

    private int count;//같은 상품을 장바구니에 몇개담을지 결정한다.

    public static CartItem createCartItem(Cart cart, Item item,int count){
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setItem(item);
        cartItem.setCount(count);
        return cartItem;
    }

    public void addCount(int count){//기존에 있는걸 추가로담을때 필요
        this.count += count;
    }
}
