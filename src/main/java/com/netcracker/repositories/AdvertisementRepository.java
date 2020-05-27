package com.netcracker.repositories;

import com.netcracker.models.Advertisement;
import com.netcracker.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AdvertisementRepository extends JpaRepository<Advertisement,Long> {

    Optional<Advertisement> findById(Long id);

    void deleteById(Long id);

   void deleteAdvertisementByUser_IdAndId(Long user_id, Long id);

   List<Advertisement> findAllByCategory_Id(Long id);

   Optional<Advertisement> findAdvertisementByUser_Id(Long id);


}
