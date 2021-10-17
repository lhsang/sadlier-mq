package com.sadlier.sadliermq.annotation;

import com.sadlier.sadliermq.common.properties.SadlierMQProperties;
import com.sadlier.sadliermq.service.QueueRetryComponent;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * @author sang.le-hoang
 * @created Oct 14, 2021
 */
@Aspect
@Configuration
@Log4j2
public class EnableRabbitRetryAndDlqAspect {

    @Autowired
    QueueRetryComponent queueRetryComponent;

    @Autowired
    SadlierMQProperties sadlierMQProperties;

    @Around("@annotation(EnableRabbitRetryAndDlq)")
    public void trace(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = getMethod(joinPoint);
        EnableRabbitRetryAndDlq annotation = method.getAnnotation(EnableRabbitRetryAndDlq.class);
        try {
            joinPoint.proceed();
        } catch (Exception e) {
            handleMQException(sadlierMQProperties, annotation, e, joinPoint);
        }
    }

    private void handleMQException(SadlierMQProperties properties,
                                  EnableRabbitRetryAndDlq annotation,
                                  Exception exceptionThrown,
                                  ProceedingJoinPoint joinPoint) {
        Method method = getMethod(joinPoint);
        Message message = (Message) joinPoint.getArgs()[0];
        log.warn("Exception when handle message. Exception error: {}", exceptionThrown.getMessage());

        try{
            if (shouldDiscard(annotation, exceptionThrown)) {
                log.warn("Exception {} was parametrized to be discarded", exceptionThrown.getClass().getSimpleName());
            } else if (shouldSentToRetry(annotation, exceptionThrown)) {
                queueRetryComponent.sendToRetryOrDlq(message, properties);
            } else {
                log.info("Message will be sent to Dead Letter Queue...");
                queueRetryComponent.sendToDlq(message, properties);
            }
        } catch (Exception e){
            log.error("Can not handle message on exception.");
            e.printStackTrace();
        }
    }

    private boolean shouldDiscard(EnableRabbitRetryAndDlq annotation, Exception exceptionThrown) {
        if (annotation.discardWhen().length > 0) {
            return checkIfContainsException(annotation, annotation.discardWhen(), exceptionThrown);
        }
        return false;
    }

    private boolean shouldSentToRetry(EnableRabbitRetryAndDlq annotation, Exception exceptionThrown) {
        return checkIfContainsException(annotation, annotation.retryWhen(), exceptionThrown);
    }

    private boolean checkIfContainsException(EnableRabbitRetryAndDlq annotation, Class<?>[] acceptableExceptions, Exception exceptionThrown) {
        if (acceptableExceptions.length == 0) {
            return false;
        }

        List<Class<?>> exceptions = Arrays.asList(acceptableExceptions);
        if (annotation.checkInheritance()) {
            return exceptions.stream()
                    .anyMatch(type -> type.isAssignableFrom(exceptionThrown.getClass()));
        }
        return exceptions.contains(exceptionThrown.getClass());
    }

    private Method getMethod(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod();
    }

}
