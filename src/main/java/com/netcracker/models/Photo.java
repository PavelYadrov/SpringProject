package com.netcracker.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "photos")
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NonNull
    String url;

    @ManyToOne
    @JoinColumn(name = "advertisement_id")
    @JsonIgnore
    private Advertisement advertisement;
}
