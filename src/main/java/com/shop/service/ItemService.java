package com.shop.service;

import com.shop.dto.ItemFormDto;
import com.shop.dto.ItemImgDto;
import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.entity.Item;
import com.shop.entity.ItemImg;
import com.shop.repository.ItemImgRepository;
import com.shop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.pl.REGON;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemImgRepository itemImgRepository;
    private final ItemImgService itemImgService;

    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception{
        //상품등록
        Item item = itemFormDto.createItem();//상품등록폼 에서 입력받은 데이터를 item 객체로 만듦
        itemRepository.save(item);//상품 데이터를 저장한다.

        //이미지 등록
        for(int i=0;i<itemImgFileList.size();i++){
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);
            if(i==0)//첫번쨰이미지라면 대표상품이미지 여부를 Y, 나머지 상품 이미지는 N
                itemImg.setRepimgYn("Y");
            else
                itemImg.setRepimgYn("N");
            itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));//상품의 이미지 정보 저장.
        }
        return item.getId();
    }

    @Transactional(readOnly = true)//상품 데이터 읽어오는 트랜잭션. jpa 가 더티체킹을 수행하지 않아 성능 향상됨.
    public ItemFormDto getItemDtl(Long itemId){
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);//등록순으로 가져오기 위해 아이디 오름차순으로 상품이미지 조회.
        List<ItemImgDto> itemImgDtoList = new ArrayList<>();
        for(ItemImg itemImg : itemImgList){//조회한 ItemImg 엔티티를 ItemImgDto 객체로 만들어서 리스트에 추가함.
            ItemImgDto itemImgDto = ItemImgDto.of(itemImg);//얘로 복사함
            itemImgDtoList.add(itemImgDto);
        }
        Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);//찾아야하는 아이템 탐색
        ItemFormDto itemFormDto = ItemFormDto.of(item);
        itemFormDto.setItemImgDtoList(itemImgDtoList);
        return itemFormDto;
    }

    public Long updateItem(ItemFormDto itemFormDto,List<MultipartFile> itemImgFileList)throws Exception{
        //상품수정
        Item item = itemRepository.findById(itemFormDto.getId()).orElseThrow(EntityNotFoundException::new);
        //상품등록화면에서 받은 아이디로 상품 엔티티 조회한다.
        item.updateItem(itemFormDto);
        //ItemFormDto를 통해 상품 엔티티를 업데이트 한다.
        List<Long> itemImgIds = itemFormDto.getItemImgIds();//상품 이미지 아이디 리스트를 조회한다.
        //이미지 등록
        for(int i=0;i<itemImgFileList.size();i++){
            itemImgService.updateItemImg(itemImgIds.get(i),itemImgFileList.get(i));
            //상품 이미지를 업데이트 하기 위해 updateItemImg() 메소드에 (상품 이미지 아이디, 상품 이미지 파일 정보)를 파라미터로 전달한다.
        }
        System.out.println("여기서 그냥 ItemFormDto.getId()로 해보는 실험해보자.-----");
        return item.getId();
    }
    @Transactional(readOnly = true)
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getAdminItemPage(itemSearchDto,pageable);
    }

    @Transactional(readOnly = true)
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto,Pageable pageable){
        return itemRepository.getMainItemPage(itemSearchDto,pageable);
    }
}
