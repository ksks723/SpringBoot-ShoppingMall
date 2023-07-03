package com.shop.service;

import com.shop.dto.CartItemDto;
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

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

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
}
