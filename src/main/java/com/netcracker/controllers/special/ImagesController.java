package com.netcracker.controllers.special;

import com.netcracker.dto.ImageDTO;
import com.netcracker.services.AdvertisementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/images")
public class ImagesController {
    private AdvertisementService advertisementService;

    @Autowired
    public ImagesController(AdvertisementService advertisementService) {
        this.advertisementService = advertisementService;
    }

    @PostMapping(value = "addImage")
    public ResponseEntity<String> addImage(@RequestBody ImageDTO imageDTO){
        return ResponseEntity.ok(advertisementService.saveImage(imageDTO));
    }
}
