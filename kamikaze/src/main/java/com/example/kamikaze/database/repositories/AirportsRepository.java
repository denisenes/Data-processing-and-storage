package com.example.kamikaze.database.repositories;

import com.example.kamikaze.database.entities.Airport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AirportsRepository extends JpaRepository<Airport, String> {

}
