package com.shop.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "item_img")
@Getter @Setter
public class ItemImg extends BaseEntity{
    @Id
    @Column(name="item_img_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String imgName;//이미지파일명
    private String oriImgName;//원본이미지파일명
    private String imgUrl;//이미지 조회경로
    private String repimgYn;//대표 이미지 여부
    @ManyToOne(fetch = FetchType.LAZY)//상품엔티티와 다대일단방향 관계로 매핑한다. 지연로딩설정으로 매핑된 상품엔티티 정보 필요시 데이터 조회하도록 한다.
    @JoinColumn(name="item_id")
    private Item item;

    public void updateItemImg(String oriImgName, String imgName,String imgUrl){
        this.oriImgName = oriImgName;
        this.imgName = imgName;
        this.imgUrl = imgUrl;
    }
}
