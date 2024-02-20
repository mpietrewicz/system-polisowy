package pl.mpietrewicz.sp.ddd.support.infrastructure.repo;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Aspect
@Component
public class AggregateLoader {

    @Inject
    private ApplicationContext applicationContext;

    @AfterReturning(pointcut = "execution(* pl.mpietrewicz.sp.modules..infrastructure.repo..*(..))",
            returning = "aggregate")
    public void afterAggregateLoad(JoinPoint joinPoint, BaseAggregateRoot aggregate) {
        if (aggregate == null)
            throw new RuntimeException("Aggregate " + joinPoint.getClass().getCanonicalName()
                    + " args = " + joinPoint.getArgs() + " does not exist");
        if (aggregate.isRemoved())
            throw new RuntimeException("Aggragate + " + aggregate.aggregateId + " is removed.");

        applicationContext.getAutowireCapableBeanFactory().autowireBean(aggregate);
    }



}