package com.netcracker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.netcracker.models.Category;
import com.netcracker.services.CategoryService;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CategoryDTO {

    private Long id;
    private String name;
    private String description;
    private Long parent_id;
    private List<Category> childs;

    public static CategoryDTO fromCategory(Category category){
        CategoryService categoryService;
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(category.getId());
        categoryDTO.setName(category.getName());
        categoryDTO.setDescription(category.getDescription());
        categoryDTO.setParent_id(category.getParent_id());
        return categoryDTO;
    }
}
