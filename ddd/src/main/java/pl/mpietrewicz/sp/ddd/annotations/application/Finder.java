
package pl.mpietrewicz.sp.ddd.annotations.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Slawek
 * 
 */
@Service
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Finder {

}