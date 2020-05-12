package com.netcracker.controllers.users;

import com.netcracker.dto.AdvertisementDTO;
import com.netcracker.models.Advertisement;
import com.netcracker.services.AdvertisementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/user/")
public class UserAdvertisementController {

    private AdvertisementService advertisementService;

    @Autowired
    public UserAdvertisementController(AdvertisementService advertisementService) {
        this.advertisementService = advertisementService;
    }

    @PostMapping(value = "getAdvertisement")
    public ResponseEntity<AdvertisementDTO> getAdvertisementById(@RequestBody String id) {
        try{
            Advertisement advertisement = advertisementService.findAdvertisementById(Long.parseLong(id));
            if (advertisement == null) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            return ResponseEntity.ok(AdvertisementDTO.fromAdvertisement(advertisement));
        }
        catch (NumberFormatException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        }
    }

    //TODO add Upload Controller
    @PostMapping(value = "addAdvertisement")
    public ResponseEntity<String> addAdvertisement(@RequestBody AdvertisementDTO advertisementDTO) {
        advertisementService.addAdvertisement(advertisementDTO);
        return ResponseEntity.ok("Advertisement successfully added");
    }

    //TODO add message for success or not?
    //TODO do we need stackTrace for exception?
    @DeleteMapping(value = "deleteAdvertisement")
    public ResponseEntity<String> deleteAdvertisement(@RequestBody String advertisement_id) {
        try{
            advertisementService.userDeleteAdvertisement(Long.parseLong(advertisement_id));
        }
        catch (NumberFormatException e){
            return new ResponseEntity<>("Invalid "+e.getLocalizedMessage(),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //TODO check UI part for random access without permission
    @PatchMapping(value = "updateAdvertisement")
    public ResponseEntity<String> updateAdvertisement(@RequestBody AdvertisementDTO advertisementDTO) {
        return ResponseEntity.ok(advertisementService.userUpdateAdvertisement(advertisementDTO));
    }

    @PostMapping(value = "getAllAdvertisementsByCategory")
    public ResponseEntity<List<AdvertisementDTO>> getAllAdvertisementsByCategory(@RequestBody String id){
        try {
            return ResponseEntity.ok(advertisementService.getAllAdvertisementsByParentCategory(Long.parseLong(id)));
        }
        catch (NumberFormatException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
