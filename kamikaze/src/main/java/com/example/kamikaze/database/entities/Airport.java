package com.example.kamikaze.database.entities;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "airports_data")
public class Airport implements Serializable {
    @Id
    private final String airport_code;
    @Column(name = "airport_name")
    private final String name;
    @Column(name = "city")
    private final String city;
    @Column(name = "timezone")
    private String timezone;

    Airport() {
        super();
        airport_code = null;
        name = null;
        city = null;
    }
}