package vip.stqr.STShortURL;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * TODO
 *
 * @author 曙天
 * @since 2022-09-24 01:00
 */
@SpringBootApplication
public class StShortUrlApplication {

	public static void main(String[] args) {
		SpringApplication.run(StShortUrlApplication.class, args);
		System.err.println("http://localhost:8890/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config&docExpansion=none#/");
	}
}
