package com.kw.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:FileUpload.properties")
public class ConfigsProperties {

    @Value( "${files.upload.dir}" )
    public String FILE_UPLOAD_DIR;
}
