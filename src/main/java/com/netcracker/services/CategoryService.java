package com.netcracker.services;

import com.netcracker.dto.CategoryDTO;
import com.netcracker.models.Category;
import com.netcracker.repositories.CategoryRepository;
import com.netcracker.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CategoryService {

    private UserRepository userRepository;

    private CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(UserRepository userRepository, CategoryRepository categoryRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }
    public Category findById(Long id){
        return categoryRepository.findById(id).orElse(null);
    }

    public List<CategoryDTO> getAllCategoriesById(Long id){
        if (id==null){
            id=1L;
        }
        List<CategoryDTO> categories = categoryRepository.findAllByParentCategory(id)
                                            .stream().map(CategoryDTO::fromCategory)
                                                .collect(Collectors.toList());
        categories.forEach(categoryDTO -> categoryDTO.setChilds(getAllChilds(categoryDTO.getId())));
        log.info("IN all categories by parent id {}- cats: {} successfully found",id,categories);
        return categories;
    }

    public void deleteCategory(Long id){
        categoryRepository.findAllByParentCategory(id).stream()
                                            .map(Category::getId)
                                            .forEach(ids -> categoryRepository.deleteById(ids));
        log.info("IN all categories by parent id {} successfully found",id);
    }
    public ResponseEntity<String> addCategory(Category category){
        if(isValid(category)==null){
            categoryRepository.save(category);
            return ResponseEntity.ok("Category with name = "+category.getName()+" successfully added");
        }
        else return isValid(category);

    }
    private ResponseEntity<String> isValid(Category category){
        if(categoryRepository.findByName(category.getName())!=null){
            return new ResponseEntity<>("Category with name = " + category.getName() + " already exists",
                    HttpStatus.BAD_REQUEST);
        }
        if (categoryRepository.findById(category.getParent_id()).orElse(null) == null) {
            return new ResponseEntity<>("Parent category with id = " + category.getId() + " does not exist",
                    HttpStatus.BAD_REQUEST);
        }
        return null;
    }

    public List<CategoryDTO> getAllChilds(Long id) {
        List<Long> ids = categoryRepository.findDistinctByParent_id();
        List<CategoryDTO> categoryDTOS = categoryRepository.findAllChilds(id)
                .stream()
                .map(CategoryDTO::fromCategory)
                .map(categoryDTO -> {
                    if (!ids.contains(categoryDTO.getId())) {
                        categoryDTO.setHasChilds(false);
                    }
                    return categoryDTO;
                }).collect(Collectors.toList());

        return categoryDTOS;
    }

    public List<Long> getCategoryList(String parentId) {
        try {
            LinkedList<Long> list = new LinkedList<>();

            Long currId = Long.parseLong(parentId);
            list.addFirst(currId);
            Long nextId;
            while (currId != 1) {
                nextId = categoryRepository.findById(currId).get().getParent_id();
                list.addFirst(nextId);
                currId = nextId;
            }
            return list;
        } catch (NoSuchElementException e) {
            LinkedList<Long> list = new LinkedList<>();
            list.add(1L);
            return list;
        }
    }
}
