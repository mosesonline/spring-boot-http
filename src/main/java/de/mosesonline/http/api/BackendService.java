package de.mosesonline.http.api;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.*;


@Target({ElementType.TYPE, ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Qualifier
public @interface BackendService {

}
