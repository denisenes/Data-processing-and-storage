package com.example.kamikaze.controllers;

import com.example.kamikaze.database.entities.*;
import com.example.kamikaze.database.repositories.AirportsRepository;
import com.example.kamikaze.database.repositories.BoardingPassRepository;
import com.example.kamikaze.database.repositories.KamikazeRepositoryImpl;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class AirportsController {

    @Autowired
    AirportsRepository airportsRepository;
    @Autowired
    KamikazeRepositoryImpl kamikazeRepositoryImpl;

    @Autowired
    BoardingPassRepository boardingPassRepository;

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
        return kamikazeRepositoryImpl.getInboundFlights(airportsRepository, airport);
    }

    @RequestMapping("/airports/{airport}/outbound")
    public List<Flight> getOutboundFlights(@PathVariable String airport) throws SQLException {
        return kamikazeRepositoryImpl.getOutboundFlights(airportsRepository, airport);
    }

    @PostMapping(value = "/book", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String bookRoute(@RequestBody RouteData data) throws SQLException {
        System.out.println(data);
        Route route = data.getRoute();
        String pass = data.getPassenger_name();
        return kamikazeRepositoryImpl.makeBooking(route, pass);
    }

    @PostMapping(value = "/register/{ticketNum}/{flightId}")
    public void checkIn(@PathVariable String ticketNum, @PathVariable Integer flightId) throws SQLException {
        List<BoardingPass> passes = boardingPassRepository.findAll();

        int boadringNo = 1;
        if (passes.size() > 0)
            boadringNo = passes.get(passes.size()-1).getBoardingNo() + 1;

        kamikazeRepositoryImpl.checkIn(ticketNum, flightId, boadringNo,
                passes.get(passes.size()-1).getSeatNo());
    }

    @RequestMapping("/routes")
    public List<Route> getRoutes(@RequestParam Optional<String> from,
                                 @RequestParam Optional<String> to,
                                 @RequestParam Optional<String> departure_time,
                                 @RequestParam Optional<String> fare,
                                 @RequestParam Optional<String> limit) throws SQLException
    {
        if (from.isEmpty() || to.isEmpty() || departure_time.isEmpty() || fare.isEmpty())
            return new ArrayList<>();

        int connections = limit.isEmpty() ? 2 : Integer.parseInt(limit.get());

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date departureTime = new Date(format.parse(departure_time.get()).getTime());
            List<Route> routes = new ArrayList<>();
            Set<String> visited = new HashSet<>();

            String city = isAirportCode(from.get()) ?
                    airportsRepository.findAirportByCode(from.get()).getCity() :
                    from.get();
            if (city == null)
                return new ArrayList<>();

            visited.add(city);
            traverseRoutes(routes, new LinkedList<>(), visited,
                    connections, city, from.get(), to.get(), fare.get(), 0, departureTime);
            return routes;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private void traverseRoutes(
            List<Route> routes, LinkedList<Flight> routeContext,
            Set<String> visited, int connections,
            String fromCity, String from, String to,
            String fare, Integer finalCost, Date departure_time
    ) throws SQLException {

        if (visited.size() - 1 > connections) {
            routeContext.removeLast();
            visited.remove(fromCity);
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(departure_time);
        calendar.add(Calendar.DAY_OF_MONTH, 1);

        Date nextDay = new Date(calendar.getTimeInMillis());

        //System.out.printf("===Dates: %s %s ===\n", departure_time, nextDay);

        if (routeContext.size() > 0 && routeContext.peekLast() != null &&
                (routeContext.peekLast().getArrival_airport().equals(to) ||
                routeContext.peekLast().getArrival_city().equals(to)))
        {
            List<RouteNode> flights = routeContext.stream().
                    map(x -> {
                        try {
                            return new RouteNode(
                                    x.departure_airport,
                                    x.arrival_airport,
                                    x.departure_city,
                                    x.arrival_city,
                                    x.flight_id,
                                    kamikazeRepositoryImpl.getPrise(x.flight_no, fare));
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }).toList();
            routes.add(new Route(finalCost, fare, flights));
        }

        //System.out.println("We are in " + from + " " + fromCity);


        Set<Flight> flights;
        if (isAirportCode(from)) {
            flights = new HashSet<>(kamikazeRepositoryImpl.getFlightsByAirport(
                    airportsRepository, from, departure_time, nextDay));
        } else {
            flights = new HashSet<>(kamikazeRepositoryImpl.getFlightsByCity(
                    airportsRepository, from, departure_time, nextDay));
        }

        //System.out.println("Found flights:");
        /*for (var i : flights.toArray()) {
            System.out.println(i);
        }*/

        flights.forEach(flight -> {
            if (!visited.contains(flight.getArrival_city())) {
                try {
                    Integer price = kamikazeRepositoryImpl.getPrise(flight.flight_no, fare);
                    if (price != null) {
                        routeContext.add(flight);
                        visited.add(flight.getArrival_city());

                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        Date departureTime = new Date(format.parse(flight.scheduled_arrival).getTime());

                        traverseRoutes(routes, routeContext,
                                visited, connections,
                                flight.getArrival_city(),
                                flight.getArrival_city(),
                                to, fare, finalCost + price,
                                departureTime);
                    }
                } catch (SQLException | ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        if (routeContext.size() > 0) {
            routeContext.removeLast();
        }
        visited.remove(fromCity);
    }

    private boolean isAirportCode(String token) {
        return token.length() == 3 && token.chars().allMatch(Character::isUpperCase);
    }

}

@lombok.Data
@RequiredArgsConstructor
class RouteData {
    public final String passenger_name;
    public final Route route;
}
