package de.mosesonline.http.api;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@Qualifier
public @interface BackendService {

    @AliasFor(annotation = Component.class)
    String value() default "";

    @AliasFor(annotation = Qualifier.class, attribute = "value")
    String backendId();
}
