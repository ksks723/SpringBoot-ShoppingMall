package com.shop.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.entity.Item;
import com.shop.entity.QItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.print.attribute.standard.PageRanges;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class ItemRepositoryTest {
    @Autowired
    ItemRepository itemRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    @DisplayName("상품저장테스트")
    public void createItemTest() {
        Item item = new Item();
        item.setItemNm("testprocut");
        item.setPrice(10000);
        item.setItemDetail("테스트상품상세설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        Item savedItem = itemRepository.save(item);
        System.out.println(savedItem.toString());
    }

    public void createItemTestList() {
        for (int i = 1; i <= 10; i++) {
            Item item = new Item();
            item.setItemNm("테스트상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트상품상세설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            Item savedItem = itemRepository.save(item);
        }
    }

    @Test
    @DisplayName("상품명조회테스트")
    public void findByItemNmTest() {
        this.createItemTestList();
        List<Item> itemList = itemRepository.findByItemNm("테스트상품1");
        for (Item item : itemList)
            System.out.println(item.toString());
    }

    @Test
    @DisplayName("상품명, 상품상세 설명 or 테스트")
    public void findByItemNmOrItemDetailTest(){
        this.createItemTestList();
        List<Item> itemList=
                itemRepository.findByItemNmOrItemDetail("테스트상품1","테스트상품상세설명5");
        for(Item item : itemList){
            System.out.println(item.toString());
        }

    }
    @Test
    @DisplayName("@Query이용한 상품 조회 테스트")
    public void findByItemDetailTest(){
        this.createItemTestList();
        List<Item> itemList = itemRepository.findByItemDetail("테스트상품상세설명");
        for(Item item:itemList)
        {
            System.out.println(item.toString());
        }
    }
    @Test
    @DisplayName("nativeQuery 속성을 이용한 상품조회테스트")
    public void findByItemDetailByNative(){
        this.createItemTestList();
        List<Item> itemList = itemRepository.findByItemDetailByNative("테스트상품상세설명");
        for (Item item:itemList)
            System.out.println(item.toString());
    }
    @Test
    @DisplayName("Querydsl조회테스트1")
    public void queryDslTest(){
        this.createItemTestList();
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
       QItem qItem = QItem.item;
        JPAQuery<Item> query = queryFactory.selectFrom(qItem)
                .where(qItem.itemSellStatus.eq(ItemSellStatus.SELL))
                .where(qItem.itemDetail.like("%"+"테스트상품상세설명"+"%"))
                .orderBy(qItem.price.desc());
        List<Item> itemList = query.fetch();
        for(Item item:itemList){
            System.out.println(item.toString());
        }

    }

    public void createItemTestList2() {
        for (int i = 1; i <= 5; i++) {
            Item item = new Item();
            item.setItemNm("테스트상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트상품상세설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }
        for (int i = 6; i <= 10; i++) {
            Item item = new Item();
            item.setItemNm("테스트상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트상품상세설명" + i);
            item.setItemSellStatus(ItemSellStatus.SOLD_OUT);
            item.setStockNumber(0);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }
    }
    @Test
    @DisplayName("상품 Querydsl 조회 테스트 2")
    public void queryDslTest2(){
        this.createItemTestList2();
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QItem qItem = QItem.item;
        String itemDetail = "테스트상품상세설명";
        int price = 10003;
        String itemSellStat = "SELL";
        booleanBuilder.and(qItem.itemDetail.like("%"+itemDetail+"%"));
        booleanBuilder.and(qItem.price.gt(price));
        if(StringUtils.equals(itemSellStat, ItemSellStatus.SELL)){
            booleanBuilder.and(qItem.itemSellStatus.eq(ItemSellStatus.SELL));
        }
        Pageable pageable = PageRequest.of(0,5);
        Page<Item> itemPage =
                itemRepository.findAll(booleanBuilder, pageable);
        System.out.println("total elements : "+itemPage.getTotalElements());
        List<Item> resultItemList = itemPage.getContent();
        for(Item item:resultItemList)
            System.out.println(item.toString());
    }
}