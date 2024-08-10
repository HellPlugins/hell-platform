package org.helldev.javacord.platform.annotations;

import org.javacord.api.entity.intent.Intent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SetIntents {
    Intent[] value();
}
