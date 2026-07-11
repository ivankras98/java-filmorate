package ru.yandex.practicum.filmorate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class FilmorateApplication {

	public static void main(String[] args) {
		SpringApplication.run(FilmorateApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo(JdbcTemplate jdbcTemplate) {
		return args -> {
			System.out.println("\n=== ТАБЛИЦЫ В БАЗЕ ДАННЫХ ===");
			try {
				jdbcTemplate.query("SHOW TABLES", (rs, rowNum) -> {
					String tableName = rs.getString("TABLE_NAME");
					System.out.println("✓ " + tableName);
					return null;
				});
				System.out.println("=== Проверка завершена ===\n");
			} catch (Exception e) {
				System.err.println("Ошибка при проверке таблиц: " + e.getMessage());
			}
		};
	}
}