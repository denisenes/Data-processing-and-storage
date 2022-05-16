package com.example.kamikaze.controllers;

import com.example.kamikaze.database.entities.Airport;
import com.example.kamikaze.database.repositories.AirportsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/airports")
public class AirportsController {

    @Autowired
    AirportsRepository airportsRepository;

    @GetMapping
    public List<Airport> getAllAirports() {
        return airportsRepository.findAll();
    }
}
