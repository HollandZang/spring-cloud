package fml.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;

//@ComponentScan("com.holland.common.spring.configuration")
@Configuration
public class HollandConf {

    @Bean
    public SecurityFilterChain webSecurityConfigurer(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .authorizeRequests()
                .mvcMatchers("/indexAuth").authenticated()
//                .anyRequest().authenticated()
                .and().formLogin()
                .and().build();
    }
}
