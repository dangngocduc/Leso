package com.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by DANGNGOCDUC on 6/9/2017.
 */


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface AdapterRecycleView {

    Class<?>[] viewholders() default {};

}
