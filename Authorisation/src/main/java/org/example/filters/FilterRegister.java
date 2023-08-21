package org.example.filters;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterRegister {

    @Bean
    public FilterRegistrationBean<TokenValidationFilter> ordersFilter(){
        FilterRegistrationBean<TokenValidationFilter> registrationBean
                = new FilterRegistrationBean<>();

        registrationBean.setFilter(new TokenValidationFilter());
        registrationBean.addUrlPatterns("/api/orders/*","/api/invoices/*","/api/users/logout", "/api/users/register" );

        registrationBean.setOrder(1);

        return registrationBean;
    }

}
