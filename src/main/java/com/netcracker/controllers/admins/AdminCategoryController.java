package com.netcracker.controllers.admins;

import com.netcracker.dto.CategoryDTO;
import com.netcracker.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/admin/")
public class AdminCategoryController {

    private final CategoryService categoryService;


    @Autowired
    public AdminCategoryController(CategoryService categoryService) {

        this.categoryService = categoryService;
    }

    @DeleteMapping(value = "deleteCategory")
    public ResponseEntity<String> deleteCategory(@RequestBody String categoryId) {
        Long id = Long.parseLong(categoryId);

        if (categoryService.findById(id) == null) {
            return new ResponseEntity<>("Category with id=" + id + " does not exist", HttpStatus.BAD_REQUEST);
        }
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("All cats with parent id=" + id + " successfully deleted");
    }

    @PostMapping(value = "addCategory")
    public ResponseEntity<String> addCategory(@RequestBody CategoryDTO category) {
        return categoryService.addCategory(category);
    }

}
