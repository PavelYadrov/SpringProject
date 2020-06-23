package com.netcracker.dto;

import com.netcracker.models.Advertisement;
import com.netcracker.models.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class AdvertisementDTO {

    private Long id;

    private Long user_id;

    private String firstName;

    private String lastName;

    private String name;

    private String description;

    private Double price;

    private Long category_id;

    private Date date;

    private List<String> urls;

    private String avatar;

    public static AdvertisementDTO fromAdvertisement(Advertisement advertisement) {
        AdvertisementDTO advertisementDTO = new AdvertisementDTO();
        User user = advertisement.getUser();
        List<String> urls = new ArrayList<>();
        advertisement.getPhotos().forEach(photo -> urls.add(photo.getUrl()));

        advertisementDTO.setId(advertisement.getId());
        advertisementDTO.setUser_id(user.getId());
        advertisementDTO.setFirstName(user.getFirstName());
        advertisementDTO.setLastName(user.getLastName());
        advertisementDTO.setName(advertisement.getName());
        advertisementDTO.setDescription(advertisement.getDescription());
        advertisementDTO.setPrice(advertisement.getPrice());
        advertisementDTO.setCategory_id(advertisement.getCategory().getId());
        advertisementDTO.setUrls(urls);
        advertisementDTO.setDate(advertisement.getDate());
        advertisementDTO.setAvatar(user.getAvatar());

        return advertisementDTO;
    }
}
