package com.example.kamikaze.database.repositories;

import com.example.kamikaze.database.entities.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface AirportsRepository extends JpaRepository<Airport, String> {

    @Query(value = "SELECT * FROM airports WHERE city = :city", nativeQuery = true)
    List<Airport> findAirports(@Param("city") String city);

    @Query(value = "SELECT * FROM airports WHERE airport_code = :code", nativeQuery = true)
    Airport findAirportByCode(@Param("code") String code);
}
