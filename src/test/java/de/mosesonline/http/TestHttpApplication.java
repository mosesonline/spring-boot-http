package de.mosesonline.http;

import org.springframework.boot.SpringApplication;

public class TestHttpApplication {

    public static void main(String[] args) {
        SpringApplication.from(HttpApplication::main).with(TestcontainersConfiguration.class).withAdditionalProfiles("local").run(args);
    }

}
