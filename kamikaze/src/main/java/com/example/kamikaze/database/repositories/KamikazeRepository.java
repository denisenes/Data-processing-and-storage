package com.example.kamikaze.database.repositories;

import com.example.kamikaze.database.entities.City;
import com.example.kamikaze.database.entities.Flight;
import com.example.kamikaze.database.entities.Route;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public interface KamikazeRepository {
    List<City> getCities() throws SQLException;
    List<Flight> getInboundFlights(AirportsRepository rep, String airport) throws SQLException;
    List<Flight> getOutboundFlights(AirportsRepository rep, String airport) throws SQLException;

    Integer getPrise(String flight_no, String fare) throws SQLException;

    List<Flight> getFlightsByAirport(AirportsRepository rep,
                                     String airport,
                                     Date date1,
                                     Date date2) throws SQLException;
    List<Flight> getFlightsByCity(AirportsRepository rep,
                                  String city,
                                  Date date1,
                                  Date date2) throws SQLException;

    String makeBooking(Route route, String passenger_name) throws SQLException;

    void checkIn(String ticketNum, Integer flightId, int boarding_no, String seat_no) throws SQLException;
}
