package de.mosesonline.http.api;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;


@Target({ElementType.TYPE, ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@BackendService
public @interface QualifiedBackendService {

    @AliasFor(annotation = Component.class)
    String value() default "";

    @AliasFor(annotation = Qualifier.class, attribute = "value")
    String backendId();
}
