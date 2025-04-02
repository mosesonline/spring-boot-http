package de.mosesonline.http;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportRuntimeHints;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
@ImportRuntimeHints(HttpApplication.Hints.class)
public class HttpApplication {

    public static void main(String[] args) {
        SpringApplication.run(HttpApplication.class, args);
    }


    static class Hints implements RuntimeHintsRegistrar {
        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            hints.resources().registerPattern("backend.properties");
            hints.resources().registerPattern("application.properties");
            hints.resources().registerPattern("application-aot.properties");
        }
    }

    @Bean
    ExecutorService executorService() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

}
