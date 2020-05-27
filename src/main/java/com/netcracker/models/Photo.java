package com.netcracker.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
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
