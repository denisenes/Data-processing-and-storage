package com.example.kamikaze.database.repositories;

import com.example.kamikaze.KamikazeApplication;
import com.example.kamikaze.database.entities.City;
import com.example.kamikaze.database.entities.Flight;
import com.example.kamikaze.database.entities.Route;
import com.example.kamikaze.database.entities.RouteNode;
import lombok.Cleanup;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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
    public List<Flight> getInboundFlights(AirportsRepository rep, String airport) throws SQLException {
        @Cleanup var result = statement.executeQuery(String.format("""
                SELECT DISTINCT extract (ISODOW from scheduled_arrival) as dow,
                    scheduled_arrival, flight_no, flight_id, departure_airport,
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
                    result.getInt("flight_id"),
                    result.getString("departure_airport"),
                    result.getString("departure_city"),
                    airport, rep.findAirportByCode(airport).getCity(),
                    result.getString("status")
            ));
        }
        return flights;
    }

    @Override
    public List<Flight> getOutboundFlights(AirportsRepository rep, String airport) throws SQLException {
        @Cleanup var result = statement.executeQuery(String.format("""
                SELECT DISTINCT extract (ISODOW from scheduled_departure) as dow,
                    scheduled_departure, flight_no, flight_id, arrival_airport,
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
                    result.getInt("flight_id"),
                    airport, rep.findAirportByCode(airport).getCity(),
                    result.getString("arrival_airport"),
                    result.getString("arrival_city"),
                    result.getString("status")
            ));
        }
        return flights;
    }

    @Override
    public Integer getPrise(String flight_no, String fare) throws SQLException {
        @Cleanup var result = statement.executeQuery(String.format("""
                SELECT DISTINCT\s
                 
                MAX(amount) as am
                 
                FROM ticket_flights\s
                INNER JOIN flights ON ticket_flights.flight_id=flights.flight_id
                 
                WHERE flight_no = '%s' and fare_conditions = '%s'
                 
                GROUP BY flight_no, fare_conditions
                """, flight_no, fare));

        if (result.next())
            return result.getInt("am");
        return null;
    }

    public List<Flight> getFlightsByAirport(AirportsRepository rep, String airport, Date date1, Date date2)
            throws SQLException {
        @Cleanup var result = statement.executeQuery(String.format("""
                SELECT DISTINCT extract (ISODOW from scheduled_departure) as dow,
                    scheduled_departure, flight_no, flight_id, arrival_airport,
                    (SELECT city FROM airports_data WHERE airport_code = arrival_airport)->>'ru' as arrival_city,
                    status
                FROM flights
                WHERE departure_airport = '%s' and (scheduled_departure between '%s' and '%s')
                ORDER BY scheduled_departure
                """, airport, dateToString(date1), dateToString(date2)));

        List<Flight> flights = new ArrayList<>();

        while (result.next()) {
            flights.add(new Flight(
                    result.getInt("dow"),
                    result.getString("scheduled_departure"),
                    result.getString("flight_no"),
                    result.getInt("flight_id"),
                    airport, rep.findAirportByCode(airport).getCity(),
                    result.getString("arrival_airport"),
                    result.getString("arrival_city"),
                    result.getString("status")
            ));
        }
        return flights;
    }

    public List<Flight> getFlightsByCity(AirportsRepository rep, String city, Date date1, Date date2)
            throws SQLException {
        @Cleanup var result = statement.executeQuery(String.format("""
                SELECT DISTINCT extract (ISODOW from scheduled_departure) as dow,
                    scheduled_departure, flight_no, flight_id,
                	(SELECT city FROM airports_data WHERE airport_code = departure_airport)->>'ru' as departure_city,
                	departure_airport,
                	arrival_airport,
                    (SELECT city FROM airports_data WHERE airport_code = arrival_airport)->>'ru' as arrival_city,
                    status
                 FROM flights
                 WHERE (scheduled_departure between '%s' and '%s') and\s
                				(SELECT city FROM airports_data WHERE airport_code = departure_airport)->>'ru' = '%s'
                """, dateToString(date1), dateToString(date2), city));

        List<Flight> flights = new ArrayList<>();

        while (result.next()) {
            flights.add(new Flight(
                    result.getInt("dow"),
                    result.getString("scheduled_departure"),
                    result.getString("flight_no"),
                    result.getInt("flight_id"),
                    result.getString("departure_airport"),
                    result.getString("departure_city"),
                    result.getString("arrival_airport"),
                    result.getString("arrival_city"),
                    result.getString("status")
            ));
        }
        return flights;
    }

    public String dateToString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    @Override
    public String makeBooking(Route route, String passenger_name) throws SQLException {
        Calendar calendar = Calendar.getInstance();

        String bookRef = "_" + UUID.randomUUID().toString().substring(0, 5);
        statement.executeUpdate(String.format("""
                INSERT INTO bookings (book_ref, book_date, total_amount)
                VALUES ('%s', '%s', %s)
                """, bookRef, dateToString(calendar.getTime()), route.cost));


        String ticket_no = "_" + UUID.randomUUID().toString().substring(0, 12);
        String passenger_id = UUID.randomUUID().toString().substring(0, 20);
        statement.executeUpdate(String.format("""
                INSERT INTO tickets (ticket_no, book_ref, passenger_id, passenger_name)
                VALUES ('%s', '%s', '%s', '%s');
                """, ticket_no, bookRef, passenger_id, passenger_name));

        for (RouteNode routeNode : route.getFlights()) {
            statement.executeUpdate(String.format("""
                    INSERT INTO ticket_flights (ticket_no, flight_id, fare_conditions, amount)
                    VALUES ('%s', %s, '%s', %s);
                    """, ticket_no, routeNode.getFlightId(), route.fare, routeNode.getPrice()));
            System.out.println("====");
            System.out.println(ticket_no);
            System.out.println(routeNode.getFlightId());
        }
        return ticket_no;
    }

    @Override
    public void checkIn(String ticketNum, Integer flightId, int boarding_no, String seat_no) throws SQLException {
        statement.executeUpdate(String.format("""
                INSERT INTO boarding_passes (ticket_no, flight_id, boarding_no, seat_no)
                VALUES ('%s', %s, %s, '%s')
                """, ticketNum, flightId, boarding_no, seat_no));
    }
}
