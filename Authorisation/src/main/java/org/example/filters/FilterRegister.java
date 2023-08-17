package org.example.filters;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<TestFilter> ordersFilter(){
        FilterRegistrationBean<TestFilter> registrationBean
                = new FilterRegistrationBean<>();

        registrationBean.setFilter(new TestFilter());
        registrationBean.addUrlPatterns("/api/orders/*");
        registrationBean.setOrder(1);

        return registrationBean;
    }

}
