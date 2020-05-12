package com.netcracker.controllers.users;

import com.netcracker.dto.CategoryDTO;
import com.netcracker.models.Category;
import com.netcracker.services.CategoryService;
import com.netcracker.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/user/")
public class UserCategoriesController {

    private final UserService userService;
    private final CategoryService categoryService;

    @Autowired
    public UserCategoriesController(UserService userService, CategoryService categoryService) {
        this.userService = userService;
        this.categoryService=categoryService;
    }


    @PostMapping(value = "getCategories")
    public ResponseEntity<List<CategoryDTO>> getCategoriesById(@RequestBody(required = false) String id){
        try {
            if(id!=null && categoryService.findById(Long.parseLong(id))==null){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            if (id==null) return ResponseEntity.ok(categoryService.getAllCategoriesById(null));

            return ResponseEntity.ok(categoryService.getAllCategoriesById(Long.parseLong(id)));
        }
       catch (NumberFormatException e){
           return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
       }
    }

    @PostMapping(value = "getFirstLayerCategories")
        public ResponseEntity<List<Category>> getFirstLayerCategories(@RequestBody String parentId){
        try {
            Long id = Long.parseLong(parentId);
            if(categoryService.findById(id)==null){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return ResponseEntity.ok(categoryService.getAllChilds(id));
        }
        catch (NumberFormatException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
