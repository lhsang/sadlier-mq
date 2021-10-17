package com.sadlier.sadliermq.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author sang.le-hoang
 * @created Oct 14, 2021
 */
@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableRabbitRetryAndDlq {

    /**
     * Define if the exception check should use 'instanceof' operator.
     */
    boolean checkInheritance() default true;

    /**
     * Exceptions to ignore and just discard the message.
     */
    Class[] discardWhen() default {};

    /**
     * Exceptions to verify if the message should be sent to retry.
     * If the number of retries is exceeded the message will be sent to DLQ.
     */
    Class[] retryWhen() default { Exception.class };

    /**
     * Exceptions to verify if the message should be sent to DLQ.
     */
    Class[] directToDlqWhen() default {};

}
