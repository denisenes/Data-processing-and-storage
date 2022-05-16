package com.example.kamikaze.database.repositories;

import com.example.kamikaze.KamikazeApplication;
import com.example.kamikaze.database.entities.City;
import lombok.Cleanup;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Component
public class CityRepository implements CitiesRepository {

    @Override
    public List<City> getCities() throws SQLException {
        @Cleanup Connection connection = KamikazeApplication.getConnection();
        @Cleanup Statement statement = connection.createStatement();

        @Cleanup var res = statement.executeQuery("""
        SELECT DISTINCT city FROM airports_data;
        """);
        List<City> list = new ArrayList<>();

        while (res.next()) {
            list.add(new City(res.getString("city")));
        }

        return list;
    }

}
