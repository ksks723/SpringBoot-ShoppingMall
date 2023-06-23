package com.shop.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Setter @Getter
public class OrderItem {
    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)//지연로딩 설정
    @JoinColumn(name = "item_id")
    private Item item; //하나의 상품은 여러 주문 상품으로 들어갈수 있으므로 상품기준 다대일 단방향 매핑

    @ManyToOne(fetch = FetchType.LAZY)//지연로딩 설정
    @JoinColumn(name = "order_id")
    private Order order;//한번의 주문에 여러개의 상품을 주문할수 있으므로 주문상품 엔티티와 주문 엔티티를 다대일 단방향 매핑설정한다.

    private int orderPrice;//주문가격

    private int count;//수량

    private LocalDateTime regTime;
    private LocalDateTime updateTime;
}
