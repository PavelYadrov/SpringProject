package com.netcracker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.netcracker.models.Category;
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
    private Boolean hasChilds;
    private List<CategoryDTO> childs;

    public static CategoryDTO fromCategory(Category category) {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(category.getId());
        categoryDTO.setName(category.getName());
        categoryDTO.setDescription(category.getDescription());
        categoryDTO.setParent_id(category.getParent_id());
        categoryDTO.hasChilds = true;
        return categoryDTO;
    }

    public Category toCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setParent_id(categoryDTO.getParent_id());
        category.setDescription(categoryDTO.getDescription());
        category.setName(categoryDTO.getName());
        return category;
    }
}
