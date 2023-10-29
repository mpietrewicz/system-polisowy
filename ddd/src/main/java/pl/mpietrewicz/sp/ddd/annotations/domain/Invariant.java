
package pl.mpietrewicz.sp.ddd.annotations.domain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * List of Aggregate invariants implemented in method
 * 
 * @author Slawek
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Invariant {

	String[] value();
}