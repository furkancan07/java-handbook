package org.example.spring.n_plus_one.repository;


import org.example.spring.n_plus_one.entity.Order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
// n+1 in çözümlerini basitçe burada göstercem
public interface OrderRepository extends JpaRepository<Order,Long> {
    // 1- klasik join fetch yaotık hibernate 6 ile beraber ekstra olarak distcint yapmana gerek yok
    @Query("select o from Order o join fetch o.items i join  fetch i.product")
    List<Order> findAllJoinFetch();
    // 2-entitygraph ile varsayılan olarak legft join gerch yapar
    @EntityGraph
    @Query("select o from Order o")
    List<Order> findWithEntityGraph();
    /**bu şkidle de yazabilirz ekstra sorgu eklememize gerek kalmaz ama
     entity graph kullanıca metot ismi sorguyu tanımlıyorsa query yazmana gerk yok tanımlamıyorsa yazcan **/
    @EntityGraph(attributePaths = {"items","items.product"})
    List<Order> findAllBy();
    @Override
    @EntityGraph(attributePaths = {"items","items.product"})
    List<Order> findAll();

    // 3- 2 aşamalı yapabiliz ilk başta idleri alırız sonra tamamını
    @Query("select o.id from Order  o order by o.id")
    Page<Long> findIds(Pageable pageable);
    @Query("select o from Order o join fetch o.items i join fetch  i.product where  o.id in :ids order by o.id" )
    List<Order> findByIdWithItems(@Param("ids") List<Long> ids);

    // 4- batch size var bu entity tarafında verilir

    // 5- @Fetch(FetchMode.SUBSELECT) yaparak da yapabiliriz 1 sorgu yerine hibernate 2 sorguyla yapar bunu





}
