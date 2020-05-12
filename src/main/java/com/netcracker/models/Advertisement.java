package com.netcracker.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

//TODO add name

@Data
@NoArgsConstructor
@Entity
@Table(name = "advertisements")
public class Advertisement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String name;

    private Date date;

    private String description;

    private Double price;

    @OneToMany(mappedBy = "advertisement",fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    private List<Photo> photos;
}
