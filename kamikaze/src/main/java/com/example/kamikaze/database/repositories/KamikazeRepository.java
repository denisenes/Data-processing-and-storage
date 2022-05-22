package com.example.kamikaze.database.repositories;

import com.example.kamikaze.database.entities.City;
import com.example.kamikaze.database.entities.Flight;

import java.sql.SQLException;
import java.util.List;

public interface KamikazeRepository {
    List<City> getCities() throws SQLException;
    List<Flight> getInboundFlights(String airport) throws SQLException;
    List<Flight> getOutboundFlights(String airport) throws SQLException;

}
