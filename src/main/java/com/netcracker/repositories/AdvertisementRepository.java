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

    /*@Query(value = "select * from advertisements ad \n" +
            "where ad.name like :words or ad.description like :words ,nativeQuery = true)
    List<Advertisement> findAllBySearch(@Param("words")String[] words);*/


}
