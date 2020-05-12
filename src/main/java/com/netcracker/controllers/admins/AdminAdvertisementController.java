package com.netcracker.controllers.admins;

import com.netcracker.dto.AdvertisementDTO;
import com.netcracker.repositories.AdvertisementRepository;
import com.netcracker.services.AdvertisementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/admin/")
public class AdminAdvertisementController {

    private AdvertisementService advertisementService;

    @Autowired
    public AdminAdvertisementController(AdvertisementService advertisementService) {
        this.advertisementService = advertisementService;
    }

    @DeleteMapping("deleteAdvertisement")
    public ResponseEntity<String> deleteAdvertisement(@RequestBody String advertisement_id){
        try{
            advertisementService.deleteAdminAdvertisement(Long.parseLong(advertisement_id));
        }
        catch (NumberFormatException e){
            return new ResponseEntity<>("Invalid "+e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping(value = "updateAdvertisement")
    public ResponseEntity<String> updateAdvertisement(@RequestBody AdvertisementDTO advertisementDTO) {
        return ResponseEntity.ok(advertisementService.adminUpdateAdvertisement(advertisementDTO));
    }
}
