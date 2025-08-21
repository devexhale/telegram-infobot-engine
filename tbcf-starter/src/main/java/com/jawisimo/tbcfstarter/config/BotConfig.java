package com.jawisimo.tbcfstarter.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableConfigurationProperties(BotProperties.class)
@EnableAsync
public class BotConfig {

}
