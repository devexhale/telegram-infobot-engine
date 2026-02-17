package com.github.jawisimo.tbcfstarter.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@AutoConfiguration
@ComponentScan(basePackages = "com.github.jawisimo.tbcfstarter")
@PropertySource("classpath:application-starter.properties")
public class StarterAutoConfig {}
