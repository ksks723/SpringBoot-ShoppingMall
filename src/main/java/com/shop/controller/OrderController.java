package com.shop.controller;

import com.shop.dto.OrderDto;
import com.shop.dto.OrderHistDto;
import com.shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping(value = "/order")
    public @ResponseBody ResponseEntity order (@RequestBody @Valid OrderDto orderDto,
                                               BindingResult bindingResult, Principal principal){
        //스프링에서 비동기 처리할때 @RequestBody와 @ReponseBody 어노테이션을 사용한다.
        //@RequestBody : HTTP 요청의 본문(body)에 있는 데이터를 자바 객체로 매핑해주는 역할
        //@ResponseBody : 컨트롤러 메서드가 반환하는 데이터를 HTTP 응답의 본문(body)에 직접 넣어주는 역할
        if(bindingResult.hasErrors()){
            //orderDto 객체에 데이터 바인딩 시 에러가 있는지 검사한다.
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for(FieldError fieldError : fieldErrors){
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);//에러정보를 ResponseEntity객체에 담아서 반환
        }
        String email =  principal.getName();
        //현재 로그인 유저의 정보를 얻기 위해 @Controller 가 선언된 클래스에서 메소드 인자로 principal 객체를 넘겨줄 경우 직접 접근할수있다.
        //principal 객체에서 현재 로그인한 회원의 이메일정보를 조회한다.
        Long orderId;
        try {
            orderId = orderService.order(orderDto,email);
            //화념에서 넘어온 주문정보와 회원이메일 정보를 이용하여 주문 로직 호출한다.
        }catch (Exception e){
            return new ResponseEntity<String>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Long>(orderId,HttpStatus.OK);//생성된 주문번호 orderId 와 요청이 성공했다는 HTTP 응답 상태 코드를 반환한다.
    }

    @GetMapping(value = {"/orders", "/orders/{page}"})
    public String orderHist(@PathVariable("page") Optional<Integer> page, Principal principal, Model model){

        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 4);
        Page<OrderHistDto> ordersHistDtoList = orderService.getOrderList(principal.getName(), pageable);

        model.addAttribute("orders", ordersHistDtoList);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("maxPage", 5);

        return "order/orderHist";
    }
}
