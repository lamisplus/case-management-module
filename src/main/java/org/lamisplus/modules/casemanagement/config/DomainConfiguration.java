package org.lamisplus.modules.casemanagement.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = {"org.lamisplus.modules.casemanagement.repository"})
public class DomainConfiguration {
}
