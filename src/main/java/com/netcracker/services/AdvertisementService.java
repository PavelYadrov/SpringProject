package com.netcracker.services;

import com.netcracker.dto.AdvertisementDTO;
import com.netcracker.dto.DTOHelper;
import com.netcracker.dto.ImageDTO;
import com.netcracker.models.Advertisement;
import com.netcracker.models.Category;
import com.netcracker.models.Photo;
import com.netcracker.repositories.AdvertisementRepository;
import com.netcracker.repositories.CategoryRepository;
import com.netcracker.repositories.PhotoRepository;
import com.netcracker.repositories.UserRepository;
import com.netcracker.security.jwt.JwtUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class AdvertisementService {

    private AdvertisementRepository advertisementRepository;
    private CategoryRepository categoryRepository;
    private UserRepository userRepository;
    private PhotoRepository photoRepository;

    @Autowired
    public AdvertisementService(AdvertisementRepository advertisementRepository,CategoryRepository categoryRepository
                                ,UserRepository userRepository, PhotoRepository photoRepository) {
        this.userRepository=userRepository;
        this.advertisementRepository = advertisementRepository;
        this.categoryRepository = categoryRepository;
        this.photoRepository=photoRepository;

    }
    //TODO add photos
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

        List<Photo> images = advertisementDTO.getUrls().stream().map(url-> {
            Photo image = new Photo(url);
            image.setAdvertisement(advertisement);
            return image;
        }).collect(Collectors.toList());
        images.forEach(photoRepository::save);
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
    //TODO resolve query problem
    public List<AdvertisementDTO> getAllAdvertisementsByParentCategory(Long id) {
        if (id == null) id = 1L;
        List<Long> ids = categoryRepository.findAllByParentCategory(id).stream().map(Category::getId)
                .collect(Collectors.toList());
        List<Advertisement> advertisements = advertisementRepository.findAllByCategory_Ids(ids).stream()
                .sorted(Comparator.comparing(Advertisement::getDate).reversed()).collect(Collectors.toList());

        return advertisements.stream().map(AdvertisementDTO::fromAdvertisement).collect(Collectors.toList());

    }

    public String saveImage(ImageDTO image){
        String name = RandomStringUtils.randomAlphanumeric(20).toUpperCase();
        try {
            Files.write(new File("B:\\myNCWORK\\images\\" + name + image.getExtension()).toPath(), image.getValue());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return name + image.getExtension();
    }

    public List<AdvertisementDTO> getAllAdvertisementsBySearch(DTOHelper content) {
        List<AdvertisementDTO> advertisements = getAllAdvertisementsByParentCategory(Long.parseLong(content.getFirstLine()));
        String[] search = content.getSecondLine().trim().split("\\s");

        return advertisements.stream().sorted((adv1, adv2) -> {
            if (adv1.getId() == 0 || adv2.getId() == 0) return 0;
            int count1 = 0;
            int count2 = 0;
            for (String word : search) {
                count1 += StringUtils.countOccurrencesOf(adv1.getName(), word);
                count1 += StringUtils.countOccurrencesOf(adv1.getDescription(), word);

                count2 += StringUtils.countOccurrencesOf(adv2.getName(), word);
                count2 += StringUtils.countOccurrencesOf(adv2.getDescription(), word);
            }
            if (count1 == 0) adv1.setId(0L);
            if (count2 == 0) adv2.setId(0L);
            return count2 - count1;
        }).filter(advertisementDTO -> advertisementDTO.getId() != 0L).collect(Collectors.toList());
    }
}
