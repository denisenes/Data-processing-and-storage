package com.example.kamikaze.controllers;

import com.example.kamikaze.database.entities.Airport;
import com.example.kamikaze.database.entities.City;
import com.example.kamikaze.database.entities.Flight;
import com.example.kamikaze.database.repositories.AirportsRepository;
import com.example.kamikaze.database.repositories.KamikazeRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
public class AirportsController {

    @Autowired
    AirportsRepository airportsRepository;
    @Autowired
    KamikazeRepositoryImpl kamikazeRepositoryImpl;

    @RequestMapping("/airports")
    public List<Airport> getAllAirports() {
        return airportsRepository.findAll();
    }

    @RequestMapping("/cities/{city}")
    public List<Airport> getAirportsInCity(@PathVariable String city) {
        return airportsRepository.findAirports(city);
    }

    @RequestMapping("/cities")
    public List<City> getAllCities() throws SQLException {
        return kamikazeRepositoryImpl.getCities();
    }

    @RequestMapping("/airports/{airport}/inbound")
    public List<Flight> getInboundFlights(@PathVariable String airport) throws SQLException {
        return kamikazeRepositoryImpl.getInboundFlights(airport);
    }

    @RequestMapping("/airports/{airport}/outbound")
    public List<Flight> getOutboundFlights(@PathVariable String airport) throws SQLException {
        return kamikazeRepositoryImpl.getOutboundFlights(airport);
    }

}
