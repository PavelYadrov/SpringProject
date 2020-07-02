package com.netcracker.controllers.special;

import com.netcracker.dto.ImageDTO;
import com.netcracker.services.AdvertisementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/images")
public class ImagesController {
    private AdvertisementService advertisementService;

    @Value("${server.image-url}")
    private String url;


    @Autowired
    public ImagesController(AdvertisementService advertisementService) {
        this.advertisementService = advertisementService;
    }

    @PostMapping(value = "addImage")
    public ResponseEntity<String> addImage(@RequestBody ImageDTO imageDTO) {
        return ResponseEntity.ok(advertisementService.saveImage(imageDTO));
    }

    @GetMapping(value = "getImageUrl")
    public ResponseEntity<String> getImageUrl() {
        return ResponseEntity.ok(url);
    }
}
