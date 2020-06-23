package com.netcracker.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@Entity(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "last_update")
    private Date lastUpdate;

    @OneToMany
    @ToString.Exclude
    @JsonIgnore
    private List<Message> messages;

    @ToString.Exclude
    @JsonIgnore
    @ManyToMany(mappedBy = "rooms", fetch = FetchType.LAZY)
    private List<User> users;
}
