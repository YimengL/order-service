package com.polarbookshop.orderservice.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;

@Configuration // Indicates a class as a source of Spring configuration
@EnableR2dbcAuditing // Enables R2DBC auditing for persistent entities
public class DataConfig {
}
