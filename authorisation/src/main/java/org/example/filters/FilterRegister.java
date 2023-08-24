package org.example.filters;

import org.example.AuthorisationControllerAdvice;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterRegister {

    @Bean
    public FilterRegistrationBean<TokenValidationFilter> tokenFilter(){
        FilterRegistrationBean<TokenValidationFilter> registrationBean
                = new FilterRegistrationBean<>();

        registrationBean.setFilter(new TokenValidationFilter(new AuthorisationControllerAdvice()));
        registrationBean.addUrlPatterns("/api/orders/*","/api/invoices/*","/api/users/logout", "/api/users/register" );

        registrationBean.setOrder(2);

        return registrationBean;
    }
}
