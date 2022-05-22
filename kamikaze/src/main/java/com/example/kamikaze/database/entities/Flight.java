package com.example.kamikaze.database.entities;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Flight {
    public final int day;
    public final String scheduled_arrival;
    public final String flight_no;
    public final String departure_airport;
    public final String departure_city;
    public final String status;
}
