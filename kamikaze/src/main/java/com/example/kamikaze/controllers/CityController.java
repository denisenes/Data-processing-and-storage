package com.example.kamikaze.controllers;

import com.example.kamikaze.database.entities.City;
import com.example.kamikaze.database.repositories.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/cities")
public class CityController {

    @Autowired
    CityRepository cityRepository;

    @GetMapping
    public List<City> getAllCities() {
        try {
            return cityRepository.getCities();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
