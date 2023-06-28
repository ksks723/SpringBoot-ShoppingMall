package com.shop.controller;

import com.shop.dto.ItemFormDto;
import com.shop.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
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
}
