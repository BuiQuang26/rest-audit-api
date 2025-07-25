package com.quangbs.restaudit.annotions;

import com.quangbs.restaudit.configs.RestAuditConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(java.lang.annotation.ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(RestAuditConfiguration.class)
public @interface RestAuditEnable {
}
