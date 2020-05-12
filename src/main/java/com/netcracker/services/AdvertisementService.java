package com.netcracker.services;

import com.netcracker.dto.AdvertisementDTO;
import com.netcracker.models.Advertisement;
import com.netcracker.models.Category;
import com.netcracker.repositories.AdvertisementRepository;
import com.netcracker.repositories.CategoryRepository;
import com.netcracker.repositories.UserRepository;
import com.netcracker.security.jwt.JwtUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@Slf4j
public class AdvertisementService {

    private AdvertisementRepository advertisementRepository;
    private CategoryRepository categoryRepository;
    private UserRepository userRepository;

    @Autowired
    public AdvertisementService(AdvertisementRepository advertisementRepository,CategoryRepository categoryRepository
                                ,UserRepository userRepository) {
        this.userRepository=userRepository;
        this.advertisementRepository = advertisementRepository;
        this.categoryRepository = categoryRepository;
    }
    public Advertisement toAdvertisement(AdvertisementDTO advertisementDTO){
        Advertisement advertisement = new Advertisement();

        advertisement.setCategory(categoryRepository.findById(advertisementDTO.getCategory_id()).get());
        advertisement.setUser(userRepository.findById(advertisementDTO.getUser_id()).get());
        advertisement.setName(advertisementDTO.getName());
        advertisement.setDate(new Date());
        advertisement.setDescription(advertisementDTO.getDescription());
        advertisement.setPrice(advertisementDTO.getPrice());

        return advertisement;
    }

    public void deleteAdminAdvertisement(Long id){
        advertisementRepository.deleteById(id);
    }

    public Advertisement findAdvertisementById(Long id){
        return advertisementRepository.findById(id).orElse(null);
    }

    //TODO add upload photos method
    public void addAdvertisement(AdvertisementDTO advertisementDTO){
        Advertisement advertisement = toAdvertisement(advertisementDTO);
        advertisementRepository.save(advertisement);
    }
    //TODO add upload photos method
    public String userUpdateAdvertisement(AdvertisementDTO advertisementDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUser user = (JwtUser) authentication.getPrincipal();
        if(!user.getId().equals(advertisementDTO.getUser_id())) return "You dont have rights to update this advertisement";

        advertisementRepository.save(userUpdate(advertisementDTO));
        return "Advertisement successfully updated";
    }

    public Advertisement userUpdate(AdvertisementDTO advertisementDTO){
        Advertisement advertisement = advertisementRepository.findById(advertisementDTO.getId()).get();

        if(advertisementDTO.getName()!=null) advertisement.setName(advertisementDTO.getName());
        if(advertisementDTO.getDescription()!=null) advertisement.setDescription(advertisementDTO.getDescription());
        if(advertisementDTO.getPrice()!=null) advertisement.setPrice(advertisementDTO.getPrice());
        return advertisement;
    }

    public String adminUpdateAdvertisement(AdvertisementDTO advertisementDTO){

        Advertisement advertisement = userUpdate(advertisementDTO);
        advertisement.setCategory(categoryRepository.findById(advertisementDTO.getCategory_id()).get());
        advertisement.setUser(userRepository.findById(advertisementDTO.getUser_id()).get());
        advertisement.setDate(advertisementDTO.getDate());

        advertisementRepository.save(advertisement);
        return "Advertisement successfully updated";
    }

    @Transactional
    public void userDeleteAdvertisement(Long advertisement_id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUser user = (JwtUser) authentication.getPrincipal();
        advertisementRepository.deleteAdvertisementByUser_IdAndId(user.getId(),advertisement_id);
    }

    //TODO resolve photos problem
    public List<AdvertisementDTO> getAllAdvertisementsByParentCategory(Long id){
        if(id==null) id=1L;
        List<Category> categories = categoryRepository.findAllByParentCategory(id);

        return categories.stream()
                .map(category -> advertisementRepository.findAdvertisementByCategory_Id(category.getId()).orElse(null))
                .filter(Objects::nonNull)
                .map(AdvertisementDTO::fromAdvertisement)
                .collect(Collectors.toList());
    }

}
