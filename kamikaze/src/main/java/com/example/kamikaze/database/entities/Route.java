package com.example.kamikaze.database.entities;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class Route {

    public final Integer cost;
    public final String fare;
    public final List<RouteNode> flights;

}
