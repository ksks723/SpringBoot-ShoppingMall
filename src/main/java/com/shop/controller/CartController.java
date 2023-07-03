package com.shop.controller;

import com.shop.dto.CartDetailDto;
import com.shop.dto.CartItemDto;
import com.shop.dto.CartOrderDto;
import com.shop.entity.BaseEntity;
import com.shop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
@Controller
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping(value = "/cart")
    public @ResponseBody ResponseEntity order(@RequestBody @Valid CartItemDto cartItemDto, BindingResult bindingResult,
                                              Principal principal){
        if(bindingResult.hasErrors()){
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for(FieldError fieldError : fieldErrors){
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }
        String email = principal.getName();
        Long cartItemId;
        try{
            cartItemId = cartService.addCart(cartItemDto,email);
        }catch (Exception e){
            return new ResponseEntity<String>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
        return  new ResponseEntity<Long>(cartItemId,HttpStatus.OK);
    }

    @GetMapping(value = "/cart")
    public String orderHist(Principal principal, Model model){
        List<CartDetailDto> cartDetailDtoList = cartService.getCartList(principal.getName());
        model.addAttribute("cartItems",cartDetailDtoList);
        return "cart/cartList";
    }
    //REST 아키텍처 스타일에 따라 HTTP 메서드를 적절하게 사용하는 것이 중요
    // HTTP DELETE 메서드는 리소스를 삭제하는 의미를 가지고 있으므로,
    // DeleteMapping을 사용하여 해당 리소스에 대한 삭제 기능을 구현하는 것은 RESTful한 API 디자인에 부합
    //Ajax 타입과 맞춤 ex: type : patch
    @PatchMapping(value = "/cartItem/{cartItemId}")//요청된 자원의 일부를 업데이트할때 PATCH 를 사용한다. 장바구니 상품 수량만 업데이트 하니까 어노테이션 사용
    public @ResponseBody ResponseEntity updateCartItem(@PathVariable("cartItemId") Long cartItemId, int count, Principal principal){

        if(count <= 0){
            return new ResponseEntity<String>("최소 1개 이상 담아주세요", HttpStatus.BAD_REQUEST);
        } else if(!cartService.validateCartItem(cartItemId, principal.getName())){//장바구니에 넣은사람과 현재 요청한(로그인한)사람이 같은지 체크
            return new ResponseEntity<String>("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        cartService.updateCartItemCount(cartItemId, count);//개수 업데이트
        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }
    //Ajax 타입과 맞춤 ex: type : delete
    @DeleteMapping(value = "/cartItem/{cartItemId}")//장바구니 상품을 삭제하기 때문에 해당 어노테이션사용
    public @ResponseBody ResponseEntity deleteCartItem(@PathVariable("cartItemId") Long cartItemId, Principal principal){

        if(!cartService.validateCartItem(cartItemId, principal.getName())){//장바구니에 넣은사람과 현재 요청한(로그인한)사람이 같은지 체크
            return new ResponseEntity<String>("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        cartService.deleteCartItem(cartItemId);

        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }

    @PostMapping(value = "/cart/orders")
    public @ResponseBody ResponseEntity orderCartItem(@RequestBody CartOrderDto cartOrderDto, Principal principal){

        List<CartOrderDto> cartOrderDtoList = cartOrderDto.getCartOrderDtoList();

        if(cartOrderDtoList == null || cartOrderDtoList.size() == 0){
            return new ResponseEntity<String>("주문할 상품을 선택해주세요", HttpStatus.FORBIDDEN);
        }

        for (CartOrderDto cartOrder : cartOrderDtoList) {
            if(!cartService.validateCartItem(cartOrder.getCartItemId(), principal.getName())){
                return new ResponseEntity<String>("주문 권한이 없습니다.", HttpStatus.FORBIDDEN);
            }
        }

        Long orderId = cartService.orderCartItem(cartOrderDtoList, principal.getName());//주문로직 호출 및 생성된 주문 번호 반환받음
        return new ResponseEntity<Long>(orderId, HttpStatus.OK);//요청이 성공했다는 HTTP 응답상태 코드를 반환한다.
    }
}
