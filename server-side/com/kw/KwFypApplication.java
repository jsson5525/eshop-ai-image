package com.kw;

import com.kw.configs.ConfigsProperties;
import com.kw.services.impl.StorageProperties;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@SpringBootApplication
@EnableConfigurationProperties({StorageProperties.class})
public class KwFypApplication {

	private static CategorySingleton test1;
	
	
	public static void main(String[] args) {
		 ConfigurableApplicationContext context = SpringApplication.run(KwFypApplication.class, args);
		
		 //context.getBean(CategorySingleton.class).getInstance();
		 
	}
	

}
