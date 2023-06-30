package com.shop.controller;

import com.shop.dto.ItemFormDto;
import com.shop.dto.ItemSearchDto;
import com.shop.entity.Item;
import com.shop.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    @GetMapping(value = "/admin/item/new")
    public String itemForm(Model model){
        model.addAttribute("itemFormDto",new ItemFormDto());
        return "/item/itemForm";
    }
    @PostMapping(value = "/admin/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult, Model model,
                          @RequestParam("itemImgFile")List<MultipartFile> itemImgFileList){
        if(bindingResult.hasErrors()){//상품등록 필수값이 없으면 상품등록으로 다시 보냄.
            return "item/itemForm";
        }
        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){//첫번째 상품이미지 없으면 에러
            model.addAttribute("errorMessage","첫번째 상품 이미지는 필수 입력 값입니다.");
            return "item/itemForm";
        }
        try{
            itemService.saveItem(itemFormDto,itemImgFileList);//상품저장, 상품정보와 상품이미지 정보를 담고있는 itemImgFileList 를 넘김
        }catch(Exception e){
            model.addAttribute("errorMessage","상품 등록 중 에러가 발생했습니다.");
            return "item/itemForm";
        }
        return "redirect:/";//저장되면 메인페이지 (정상작동)
    }

    @GetMapping(value="/admin/item/{itemId}")
    public String itemDtl(@PathVariable("itemId") Long itemId, Model model){
        try {
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);//특정아이템 조회해서 객체로 반환
            model.addAttribute("itemFormDto",itemFormDto);//모델에 담아 뷰로 반환
        }catch (EntityNotFoundException e){//없으면 에러메시지와 함께 등록페이지로 이동
            model.addAttribute("errorMessage","존재하지 않는 상품이다.");
            model.addAttribute("itemFormDto",new ItemFormDto());
            return "item/itemForm";
        }
        return "item/itemForm";
    }

    @PostMapping(value = "/admin/item/{itemId}")
    public String itemUpdate(@Valid ItemFormDto itemFormDto,BindingResult bindingResult,@RequestParam("itemImgFile")List<MultipartFile> itemImgFileList,Model model){
        if(bindingResult.hasErrors()){
            return "item/itemForm";
        }
        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage","첫번째 이미지는 필수입니다");
            return "item/itemForm";
        }
        try {
            itemService.updateItem(itemFormDto,itemImgFileList); //수정로직 호출
        }catch (Exception e){
            model.addAttribute("errorMesage","상품 수정 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }
        return "redirect:/";
    }

    @GetMapping(value={"/admin/items","/admin/items/{page}"})
    //url에 페이지 번호가 없는 경우& 있는 경우 2가지를 value 에 매핑한다.
    public String itemManage(ItemSearchDto itemSearchDto, @PathVariable("page")Optional<Integer> page,Model model){
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0,3);
        //페이징을위해 PageRequest.of메소드를 통해 pageable객체를 생성한다.첫파람은 조회할 페이지번호, 두번짼 데이터 수를 넣어준다.
        //url 경로에 페이지가 있으면 - 해당페이지 조회세팅, 없으면-0페이지를 조회하도록
        Page<Item> items = itemService.getAdminItemPage(itemSearchDto,pageable);
        //조회조건과 페이징 정보를 파람으로 넘겨서 객체를 받는다.
        model.addAttribute("items",items);
        //조회한 상품 데이터 및 페이징 정보를 뷰에 전달한다.
        model.addAttribute("itemSearchDto",itemSearchDto);
        //페이지 전환 시 기존 검색조건을 유지한채 이동하도록 뷰에 재전달한다.
        model.addAttribute("maxPage",5);
        //메뉴하단에 보여줄 페이지 번호의 최대 개수이다.
        return "item/itemMng";
    }
}
