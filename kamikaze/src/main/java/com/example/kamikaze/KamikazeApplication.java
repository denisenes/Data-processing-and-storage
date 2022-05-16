package com.example.kamikaze;

import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Connection;
import java.sql.DriverManager;

@SpringBootApplication
public class KamikazeApplication {

	static final private String database_url = "jdbc:postgresql://127.0.0.1:5432/demo";
	static final private String database_user = "postgres";
	static final private String database_pwd = "Denis212130303";

	@SneakyThrows
	public static Connection getConnection() {
		return DriverManager.getConnection(database_url, database_user, database_pwd);
	}

	public static void main(String[] args) {
		SpringApplication.run(KamikazeApplication.class, args);
	}

}
