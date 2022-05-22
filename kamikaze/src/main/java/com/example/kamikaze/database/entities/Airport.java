package com.example.kamikaze.database.entities;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Airport airport = (Airport) o;
        return airport_code != null && Objects.equals(airport_code, airport.airport_code);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}