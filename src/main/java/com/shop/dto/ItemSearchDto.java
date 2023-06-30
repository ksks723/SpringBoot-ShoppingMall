package com.shop.dto;

import com.shop.constant.ItemSellStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ItemSearchDto {
    private String searchDateType;
    //현재시간과 상품등록일을 비교해서 상품 데이터를 조회한다. 조회시간 기준은 등록일 전체/하루/일주일/한달/6개월 로 나뉜다.
    private ItemSellStatus searchSellStatus;
    //판매상태를 기준으로 상품 데이터를 조회한다.
    private String searchBy;
    //조회할때 어떤 유형으로 조회할지. itemNm:상품명, createdBy:상품 등록자 아이디
    private String searchQuery="";
    //조회할 검색어를 저장하는 변수.
}
