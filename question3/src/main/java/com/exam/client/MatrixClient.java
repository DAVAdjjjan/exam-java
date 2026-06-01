package com.exam.client;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@Profile("client")
public class MatrixClient implements CommandLineRunner {

    @Override
    public void run(String... args) {
        RestTemplate restTemplate = new RestTemplate();

        List<Double> averages = restTemplate.exchange(
                "http://localhost:8080/api/matrix/averages",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Double>>() {}
        ).getBody();

        for (int i = 0; i < (averages != null ? averages.size() : 0); i++) {
            System.out.printf("Row %d average: %.2f%n", i, averages.get(i));
        }
    }
}
