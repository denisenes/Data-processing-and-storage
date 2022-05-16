package com.example.kamikaze.database.repositories;

import com.example.kamikaze.database.entities.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.SQLException;
import java.util.List;

public interface CitiesRepository {
    public List<City> getCities() throws SQLException;
}
