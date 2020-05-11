package com.netcracker.controllers.users;

import com.netcracker.dto.CategoryDTO;
import com.netcracker.models.Category;
import com.netcracker.services.CategoryService;
import com.netcracker.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


    @GetMapping(value = "getCategories")
    public ResponseEntity<List<CategoryDTO>> getCats(@RequestBody(required = false) String id){
        if(id!=null && categoryService.findById(Long.parseLong(id))==null){
            return new ResponseEntity("Category with id="+id+" does not exist",HttpStatus.BAD_REQUEST);
        }
        if (id==null) return ResponseEntity.ok(categoryService.getAllCatsById(null));

        return ResponseEntity.ok(categoryService.getAllCatsById(Long.parseLong(id)));
    }

    @GetMapping(value = "getFirstLayerCategories")
        public ResponseEntity<List<Category>> getFirstLayerCategories(@RequestBody(required = true) String parentId){
        Long id = Long.parseLong(parentId);
        if(categoryService.findById(id)==null){
            return new ResponseEntity("Category with id="+id+" does not exist",HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(categoryService.getAllChilds(id));
    }
}
