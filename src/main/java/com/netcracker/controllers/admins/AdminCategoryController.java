package com.netcracker.controllers.admins;

import com.netcracker.models.Category;
import com.netcracker.repositories.UserRepository;
import com.netcracker.services.CategoryService;
import com.netcracker.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/admin/")
public class AdminCategoryController {

    private final UserService userService;
    private final CategoryService categoryService;
    private final UserRepository userRepository;

    @Autowired
    public AdminCategoryController(UserService userService, UserRepository userRepository, CategoryService categoryService) {
        this.userService = userService;
        this.userRepository=userRepository;
        this.categoryService = categoryService;
    }

    @DeleteMapping(value = "deleteCategories")
    public ResponseEntity<String> getCategories(@RequestBody(required = true) String ide){
        Long id = Long.parseLong(ide);

        if(categoryService.findById(id)==null){
            return new ResponseEntity<String>("Category with id="+id+" does not exist",HttpStatus.BAD_REQUEST);
        }
        categoryService.deleteCats(id);
        return ResponseEntity.ok("All cats with parent id="+id+" successfully deleted");
    }

    @PostMapping(value = "addCategory")
    public ResponseEntity<String> addCategory(@RequestBody Category category){
        return categoryService.addCategory(category);
    }

}
