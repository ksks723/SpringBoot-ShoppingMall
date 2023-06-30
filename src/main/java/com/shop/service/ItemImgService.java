package com.shop.service;

import com.shop.entity.ItemImg;
import com.shop.repository.ItemImgRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityNotFoundException;

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
            imgName = fileService.uploadFile(itemImgLocation,oriImgName,itemImgFile.getBytes());
            imgUrl = "/images/item/" + imgName;
            //저장한 사움 이미지를 불러올 경로를 설정한다. //WebMvcConfig에서 설정한 /images/**와 application.properties에서 설정한 "C:/shop/
            //아래 item 폴더에 이미지를 저장하므로 상품이미지를 불러오는 경로로 "/images/item/"를 붙여준다.
        }
        //상품 이미지 정보 저장
        itemImg.updateItemImg(oriImgName,imgName,imgUrl);
        itemImgRepository.save(itemImg);
    }

    public void updateItemImg(Long itemImgId, MultipartFile itemImgFile) throws Exception{
        if(!itemImgFile.isEmpty()){//상품이미지를 수정한 경우
            ItemImg savedItemImg = itemImgRepository.findById(itemImgId).orElseThrow(EntityNotFoundException::new);//업데이트 한다.
            //기존 이미지파일 삭제
            if (!StringUtils.isEmpty(savedItemImg.getImgName())) {//기존 이미지 파일이 있으면 삭제한다.
                fileService.deleteFile(itemImgLocation+"/"+savedItemImg.getImgName());
            }
            String oriImgName = itemImgFile.getOriginalFilename();
            String imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());//업데이트한 상품이미지 파일 업로드
            String imgUrl = "/images/item/"+imgName;
            savedItemImg.updateItemImg(oriImgName,imgName,imgUrl);// 변경된 상품 이미지 정보 셋팅.
            //savedItemImg 엔티티는 현재 영속 상태이기에 데이터 변경만으로도 변경감지 기능이 작동하여 트랜잭션이 끝날때 update 쿼리가 실행된다.
        }
    }
}
