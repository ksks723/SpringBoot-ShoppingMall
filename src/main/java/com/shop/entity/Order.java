package com.shop.entity;

import com.shop.constant.OrderStatus;
import lombok.Getter;
import lombok.Setter;
import org.aspectj.weaver.ast.Or;

import javax.persistence.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="orders")//정렬 명령어 "order"과 겹치기 때문에 "orders" 로 한다.
@Getter@Setter
public class Order extends BaseEntity{
    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member; //한명의 회원은 여러번 주문할수 있으므로 다대일 단방향매칭 @ManyToOne 을 한다.

    private LocalDateTime orderDate; //주문일

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; //주문상태

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)//
    private List<OrderItem> orderItems = new ArrayList<>();//하나의 주문이 여러개의 주문 상품을 갖으므로 List자료형을 사용해서 매핑
    //주문 상품 엔티티와 일대다 매핑함. 외래키(order_id)가 order_item테이블에 있으므로 연관 관게의 주인은 OrderItem 엔티티이다.
    //Order 엔티티가 주인이 아니므로 "mappedBy"속성으로 연관관계의 주인을 설정한다.
    //속성의 값으로 "order"를 적어준 이유는 OrderItem 에 있는 Order에 의해 관리된다는 의미로 해석한다.
    //즉 , 연관 관계상 주인필드인 order를 mappedBy의 값으로 세팅하면 된다.

//    private LocalDateTime regTime;         BaseEntity를 상속받음으로써 더이상 필요없기에 없앤다.
//    private LocalDateTime updateTime;
    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);//Order엔티티와 OrderITem 엔티티가 양방향 참조관계이므로 orderITem 객체에도 order 객체를 세팅한다.
    }

    public static Order createOrder(Member member,List<OrderItem> orderItemList){
        Order order = new Order();
        order.setMember(member);//상품을 주문한 회원정보 세팅
        for(OrderItem orderItem:orderItemList){//여러개 주문한 경우
            order.addOrderItem(orderItem);
        }
        order.setOrderStatus(OrderStatus.ORDER);//주문상태 세팅
        order.setOrderDate(LocalDateTime.now());//주문시간 세팅
        return order;
    }

    public int getTotalPrice(){
        int totalPrice = 0;
        for(OrderItem orderItem : orderItems){
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

    public void cancelOrder(){
        this.orderStatus = OrderStatus.CANCEL;
        for(OrderItem orderItem : orderItems){
            orderItem.cancel();
        }
    }
}
