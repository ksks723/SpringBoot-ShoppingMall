package com.shop.service;

import com.shop.dto.CartDetailDto;
import com.shop.dto.CartItemDto;
import com.shop.dto.CartOrderDto;
import com.shop.dto.OrderDto;
import com.shop.entity.Cart;
import com.shop.entity.CartItem;
import com.shop.entity.Item;
import com.shop.entity.Member;
import com.shop.repository.CartItemRepository;
import com.shop.repository.CartRepository;
import com.shop.repository.ItemRepository;
import com.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderService orderService;
    public Long addCart(CartItemDto cartItemDto,String email){
        Item item = itemRepository.findById(cartItemDto.getItemId()).orElseThrow(EntityNotFoundException::new);
        //장바구니에 담을 상품 엔티티 조회
        Member member = memberRepository.findByEmail(email);
        //현재 로그인한 회원 넣기
        Cart cart = cartRepository.findByMemberId(member.getId());
        //로그인한 회원의 카트 넣기
        if(cart == null){//처음 장바구니 사용하는거면 만들어준다.
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }
        CartItem savedCartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(),item.getId());
        //현재 상품이 장바구니에 이미 들어가있는지 확인한다.
        if(savedCartItem != null){
            System.out.println("-------" + savedCartItem.getCart().getMember() + "++++\n" + savedCartItem.getItem().getItemNm());
            savedCartItem.addCount(cartItemDto.getCount());//이미 있는 아이템이면 기존수량 + 현재 수량 해준다.
            return savedCartItem.getId();
        }else {
            CartItem cartItem = CartItem.createCartItem(cart,item,cartItemDto.getCount());
            //장바구니엔티티, 상품엔티티, 수량을 이용해서 CartItem엔티티를 생성한다.
            cartItemRepository.save(cartItem);
            //장바구니에 들어갈 상품을 저장한다.
            return cartItem.getId();
        }
    }


    @Transactional(readOnly = true)
    public List<CartDetailDto> getCartList(String email){

        List<CartDetailDto> cartDetailDtoList = new ArrayList<>();

        Member member = memberRepository.findByEmail(email);//로그인한 회원정보로 장바구니 조회
        Cart cart = cartRepository.findByMemberId(member.getId());
        //만약 비어있다면 상품을 한번도 안담은거니까 빈 리스트를 반환한다.
        if(cart == null){
            return cartDetailDtoList;
        }

        cartDetailDtoList = cartItemRepository.findCartDetailDtoList(cart.getId());
        return cartDetailDtoList;
    }

    @Transactional(readOnly = true)
    public boolean validateCartItem(Long cartItemId, String email){
        Member curMember = memberRepository.findByEmail(email);//현재 로그인한 사람 정보
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
        Member savedMember = cartItem.getCart().getMember();//장바구니정보로 조회한 사람정보

        if(!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())){//일치여부 확인
            return false;
        }

        return true;
    }

    public void updateCartItemCount(Long cartItemId, int count){
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);

        cartItem.updateCount(count);
    }

    public void deleteCartItem(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
        cartItemRepository.delete(cartItem);
    }

    public Long orderCartItem(List<CartOrderDto> cartOrderDtoList, String email){
        List<OrderDto> orderDtoList = new ArrayList<>();

        for (CartOrderDto cartOrderDto : cartOrderDtoList) {
            //장바구니에서 전달받은 주문 상품 번호를 이용해서 주문 로직으로 전달할 orderDto 객체를 만든다.
            CartItem cartItem = cartItemRepository
                    .findById(cartOrderDto.getCartItemId())
                    .orElseThrow(EntityNotFoundException::new);

            OrderDto orderDto = new OrderDto();
            orderDto.setItemId(cartItem.getItem().getId());
            orderDto.setCount(cartItem.getCount());
            orderDtoList.add(orderDto);
        }

        Long orderId = orderService.orders(orderDtoList, email);//장바구니에 담은 상품을 주문하도록 주문 로직을 호출한다.
        for (CartOrderDto cartOrderDto : cartOrderDtoList) {//주문한 상품들을 제거한다.
            CartItem cartItem = cartItemRepository
                    .findById(cartOrderDto.getCartItemId())
                    .orElseThrow(EntityNotFoundException::new);
            cartItemRepository.delete(cartItem);
        }

        return orderId;
   }

}
