package de.mosesonline.http;

import de.mosesonline.http.routing.RequestRouterService;
import de.mosesonline.http.session.DynamoDbSessionData;
import de.mosesonline.http.session.RawRequestSession;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.annotation.RegisterReflection;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.context.annotation.ReflectiveScan;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
@ImportRuntimeHints({HttpApplication.Hints.class})
@RegisterReflection(classes = DynamoDbSessionData.class, memberCategories =
        {MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_PUBLIC_METHODS, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS})
@RegisterReflectionForBinding(RequestRouterService.class)
@ReflectiveScan
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
            hints.resources().registerPattern("io/awspring/cloud/core/SpringCloudClientConfiguration.properties");
            hints.reflection().registerType(RawRequestSession.class);
        }
    }

    @Bean
    ExecutorService executorService() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

}
