package pl.mpietrewicz.sp.ddd.annotations.application;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Component
@Retention(RetentionPolicy.RUNTIME)
@Transactional(propagation = Propagation.REQUIRED)
@Target(ElementType.TYPE)
public @interface ApplicationService {
    Transactional transactional(); // wymaga podania transactionManager'a
}