package com.smartiq.pim.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Pim.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    String orderManagementAppLink;

    public String getOrderManagementAppLink() {
        return orderManagementAppLink;
    }

    public void setOrderManagementAppLink(String orderManagementAppLink) {
        this.orderManagementAppLink = orderManagementAppLink;
    }
}
