package com.example.kamikaze.database.repositories;

import com.example.kamikaze.KamikazeApplication;
import com.example.kamikaze.database.entities.City;
import com.example.kamikaze.database.entities.Flight;
import lombok.Cleanup;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Component
public class KamikazeRepositoryImpl implements KamikazeRepository {

    private final Statement statement;

    public KamikazeRepositoryImpl() throws SQLException {
        Connection connection = KamikazeApplication.getConnection();
        statement = connection.createStatement();
    }

    @Override
    public List<City> getCities() throws SQLException {

        @Cleanup var res = statement.executeQuery("""
        SELECT DISTINCT city FROM airports_data;
        """);
        List<City> list = new ArrayList<>();

        while (res.next()) {
            list.add(new City(res.getString("city")));
        }

        return list;
    }

    @Override
    public List<Flight> getInboundFlights(String airport) throws SQLException {
        @Cleanup var result = statement.executeQuery(String.format("""
                SELECT DISTINCT extract (ISODOW from scheduled_arrival) as dow,
                    scheduled_arrival, flight_no, departure_airport,
                    (SELECT city FROM airports_data WHERE airport_code = departure_airport) as departure_city,
                    status
                FROM flights
                WHERE arrival_airport = '%s'
                ORDER BY scheduled_arrival
                """, airport));

        List<Flight> flights = new ArrayList<>();
        while (result.next()) {
            flights.add(new Flight(
                    result.getInt("dow"),
                    result.getString("scheduled_arrival"),
                    result.getString("flight_no"),
                    result.getString("departure_airport"),
                    result.getString("departure_city"),
                    result.getString("status")
            ));
        }
        return flights;
    }

    @Override
    public List<Flight> getOutboundFlights(String airport) throws SQLException {
        @Cleanup var result = statement.executeQuery(String.format("""
                SELECT DISTINCT extract (ISODOW from scheduled_departure) as dow,
                    scheduled_departure, flight_no, arrival_airport,
                    (SELECT city FROM airports_data WHERE airport_code = arrival_airport) as arrival_city,
                    status
                FROM flights
                WHERE departure_airport = '%s'
                ORDER BY scheduled_departure
                """, airport));

        List<Flight> flights = new ArrayList<>();
        while (result.next()) {
            flights.add(new Flight(
                    result.getInt("dow"),
                    result.getString("scheduled_departure"),
                    result.getString("flight_no"),
                    result.getString("arrival_airport"),
                    result.getString("arrival_city"),
                    result.getString("status")
            ));
        }
        return flights;
    }
}
