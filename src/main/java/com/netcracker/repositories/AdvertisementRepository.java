package com.netcracker.repositories;

import com.netcracker.models.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AdvertisementRepository extends JpaRepository<Advertisement,Long> {

    Optional<Advertisement> findById(Long id);

    void deleteById(Long id);

    void deleteAdvertisementByUser_IdAndId(Long user_id, Long id);

    List<Advertisement> findAllByCategory_Id(Long id);

    Optional<Advertisement> findAdvertisementByUser_Id(Long id);

    @Query(value = "select * from advertisements ad where ad.category_id in (:ids) \n" +
            "order by ad.date desc", nativeQuery = true)
    List<Advertisement> findAllByCategory_Ids(@Param("ids") List<Long> ids);


    @Query(value = "select *\n" +
            "from (select ad.id,ad.user_id,ad.category_id,ad.name,ad.date,ad.description,ad.price," +
            "row_number() over(order by date desc) from advertisements ad where ad.category_id in(:id)) as dd\n" +
            "where dd.row_number between :minP and :maxP", nativeQuery = true)
    List<Advertisement> findAllByCategory_idAndPage(@Param("id") List<Long> id, @Param("minP") Long pageMin, @Param("maxP") Long pageMax);

    @Query(value = "select count(ad.id) from advertisements ad where ad.category_id in(:ids)", nativeQuery = true)
    Integer findCountByCategory(@Param("ids") List<Long> ids);

    List<Advertisement> findAllByUser_Id(Long id);


}
