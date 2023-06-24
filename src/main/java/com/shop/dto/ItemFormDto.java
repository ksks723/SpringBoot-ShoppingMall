package com.shop.dto;

import com.shop.constant.ItemSellStatus;
import com.shop.entity.Item;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
public class ItemFormDto {
    private Long id;
    @NotBlank(message = "상품명은 필수 입력 값입니다.")
    private String itemNm;
    @NotBlank(message = "가격은 필수 입력 값입니다.")
    private Integer price;
    @NotBlank(message = "이름으 필수 입력 값입니다.")
    private String itemDetail;
    @NotNull(message="재고는 필수 입력 값입니다.")
    private Integer stockNumber;
    private ItemSellStatus itemSellStatus;
    private List<ItemImgDto> itemImgDtoList = new ArrayList<>();//상품저장 후 수정할때 상품이미지 정보를 저장하는 리스트입니다.
    private List<Long> itemImgIds = new ArrayList<>();
    //상품의 이미지ID를 저장하는 리스트, 등록할땐 이미지가없어서 비어있고 수정후 이미지 ID를 담아둘 용도임
    private static ModelMapper modelMapper = new ModelMapper();
    public Item createItem(){
        return modelMapper.map(this, Item.class);
        //model매퍼를 사용해서 엔티티,DTO객체간의 데이터를 복사하여 복사한 객체를 반환해주는 메서드다.
    }
    public static ItemFormDto of(Item item){
        return modelMapper.map(item, ItemFormDto.class);
    }
}
