package org.example.filters;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This class handles servlet filter registration
 */
@Configuration
public class FilterRegister {

    @Bean
    public FilterRegistrationBean<TokenValidationFilter> tokenFilter(){
        FilterRegistrationBean<TokenValidationFilter> registrationBean
                = new FilterRegistrationBean<>();

        registrationBean.setFilter(new TokenValidationFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);

        return registrationBean;
    }
}
