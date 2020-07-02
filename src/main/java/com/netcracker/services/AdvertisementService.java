package com.netcracker.services;

import com.netcracker.dto.AdvertisementDTO;
import com.netcracker.dto.DTOHelper;
import com.netcracker.dto.ImageDTO;
import com.netcracker.dto.MainPageParams;
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
import org.apache.commons.lang.SystemUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
    public List<AdvertisementDTO> getAllAdvertisementsByParentCategory(DTOHelper params) {
        Long id = Long.parseLong(params.getFirstLine());
        Long page = Long.parseLong(params.getSecondLine()) * 6;

        List<Long> ids = categoryRepository.findAllByParentCategory(id).stream().map(Category::getId)
                .collect(Collectors.toList());
        return advertisementRepository.findAllByCategory_idAndPage(ids, page - 5, page)
                .stream()
                .map(AdvertisementDTO::fromAdvertisement)
                .collect(Collectors.toList());
    }
    public String saveImage(ImageDTO image) {
        String name = RandomStringUtils.randomAlphanumeric(20).toUpperCase();
        if (!SystemUtils.IS_OS_WINDOWS) {
            try {
                InputStream in = new ByteArrayInputStream(image.getValue());
                BufferedImage bufferedImage = ImageIO.read(in);
                File file = new File("/images/" + name + image.getExtension());
                file.createNewFile();
                ImageIO.write(bufferedImage, image.getExtension(), file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Files.write(new File("./images/" + name + image.getExtension()).toPath(), image.getValue());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return name + image.getExtension();

    }
    public List<AdvertisementDTO> getAllAdvertisementsBySearch(MainPageParams params) {
        Long id = Long.parseLong(params.getCategory_id());
        Long page = Long.parseLong(params.getPage()) * 6;
        String[] search = params.getSearch().toLowerCase().trim().split("\\s");
        List<AdvertisementDTO> advertisements = findAllAdvs(id, search);

        return advertisements.stream().sorted((adv1, adv2) -> {
            int count1 = 0;
            int count2 = 0;
            for (String word : search) {
                count1 = getCount(adv1, count1, word);

                count2 = getCount(adv2, count2, word);
            }
            return count2 - count1;
        }).skip(page - 6).limit(6)
                .collect(Collectors.toList());
    }
    private int getCount(AdvertisementDTO adv, int count, String word) {
        count += StringUtils.countOccurrencesOf(adv.getName().toLowerCase(), word);
        count += StringUtils.countOccurrencesOf(adv.getName().toLowerCase(), " " + word + " ");
        count += StringUtils.countOccurrencesOf(adv.getDescription().toLowerCase(), word);
        count += StringUtils.countOccurrencesOf(adv.getDescription().toLowerCase(), " " + word + " ");
        return count;
    }
    public Integer findCountOfAdvertisements(MainPageParams params) {
        Long id = Long.parseLong(params.getCategory_id());
        String search = params.getSearch();
        if (search == null || search.isEmpty()) {
            return advertisementRepository.findCountByCategory(categoryRepository.findAllByParentCategory(id).stream().map(Category::getId)
                    .collect(Collectors.toList()));
        } else {
            return findAllAdvs(id, search.toLowerCase().split("\\s")).size();
        }
    }
    private List<AdvertisementDTO> findAllAdvs(Long id, String[] search) {
        List<Long> ids = categoryRepository.findAllByParentCategory(id).stream().map(Category::getId)
                .collect(Collectors.toList());
        return advertisementRepository.findAllByCategory_Ids(ids).stream()
                .sorted(Comparator.comparing(Advertisement::getDate).reversed())
                .map(AdvertisementDTO::fromAdvertisement)
                .filter(adv -> {
                    int count = 0;
                    for (String word : search) {
                        count += StringUtils.countOccurrencesOf(adv.getName().toLowerCase(), word);
                        count += StringUtils.countOccurrencesOf(adv.getDescription().toLowerCase(), word);
                    }
                    return count != 0;
                }).collect(Collectors.toList());
    }
    public List<AdvertisementDTO> getAllAdvertisements(String id) {
        return advertisementRepository.findAllByUser_Id(Long.parseLong(id))
                .stream()
                .map(AdvertisementDTO::fromAdvertisement)
                .collect(Collectors.toList());
    }
}
