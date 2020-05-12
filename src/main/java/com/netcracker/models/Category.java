package com.netcracker.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String name;

    @NonNull
    private String description;

    @NonNull
    @Column(name = "parent_id")
    private Long parent_id;

    @OneToMany(mappedBy = "category",cascade = CascadeType.ALL)
    @JsonIgnore

    private List<Advertisement> advertisementsCategory;

}
