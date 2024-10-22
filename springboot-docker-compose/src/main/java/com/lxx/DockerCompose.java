package com.lxx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.lxx.repository")
public class DockerCompose {
    public static void main(String[] args) {
        SpringApplication.run(DockerCompose.class, args);
    }
}
