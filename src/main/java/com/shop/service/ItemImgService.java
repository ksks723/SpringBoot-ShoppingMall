package com.shop.service;

import com.shop.entity.ItemImg;
import com.shop.repository.ItemImgRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemImgService {
    @Value("${itemImgLocation}")
    private String itemImgLocation;
    private final ItemImgRepository itemImgRepository;
    private final FileService fileService;
    public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile) throws Exception{
        String oriImgName = itemImgFile.getOriginalFilename();;
        String imgName = "";
        String imgUrl = "";

        //파일 업로드
        if(!StringUtils.isEmpty(oriImgName)){
            System.out.println(itemImgLocation+"-------------------");
            imgName = fileService.uploadFile(itemImgLocation,oriImgName,itemImgFile.getBytes());
            imgUrl = "/images/item/" + imgName;
            //저장한 사움 이미지를 불러올 경로를 설정한다. //WebMvcConfig에서 설정한 /images/**와 application.properties에서 설정한 "C:/shop/
            //아래 item 폴더에 이미지를 저장하므로 상품이미지를 불러오는 경로로 "/images/item/"를 붙여준다.
        }
        //상품 이미지 정보 저장
        itemImg.updateItemImg(oriImgName,imgName,imgUrl);
        itemImgRepository.save(itemImg);
    }
}
