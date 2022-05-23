package com.example.kamikaze.database.entities;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class RouteNode {

    final String from;
    final String to;
    final String cityFrom;
    final String cityTo;
    final Integer flightId;
    final Integer price;

}
