package org.udemy.SpringBlog.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.udemy.SpringBlog.util.constants.Privileges;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {
    private static final String[] WHITELIST = {
            "/",
            "/register",
            "/login",
            "/db-console/**",
            "/resources/**",
            "/posts/**"
    };

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // THE FOLLOWING SNIPPET IS DEPRECIATED AND IS ONLY ALLOWED TILL SPRING 3.0
        // WHICH HAVE BEEN SUBJECTED TO REMOVAL AFTER THE RELEASE OF SPRING 7.0
        // CHECK OUT THE FOLLOWING ARTICLE
        // https://codejava.net/frameworks/spring-boot/spring-security-fix-deprecated-methods

        http
                .headers().frameOptions().sameOrigin()
                .and()
                .authorizeRequests()
                .requestMatchers(WHITELIST).permitAll()
                .requestMatchers("/profile/**").authenticated()
                .requestMatchers("/update_photo/**").authenticated()
                .requestMatchers("/posts/add/**").authenticated()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/editor/**").hasAnyRole("ADMIN", "EDITOR")
                .requestMatchers("/test").hasAuthority(Privileges.ACCESS_ADMIN_PANEL.getPrivilege())
                .and()
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error")
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .and()
                .rememberMe().rememberMeParameter("remember-me")
                .and()
                .httpBasic();
        // http
        // .authorizeHttpRequests(auth -> auth
        // .requestMatchers(WHITELIST)
        // .permitAll()
        // .anyRequest()
        // .authenticated());
        // EDIT: used the same type as shown in the course itself

        // TODO: Remove the following snippet once we have shifted from a h2 console
        http.csrf(csrf -> csrf.disable());
        http.headers(headers -> headers.frameOptions().disable());

        // http.csrf(httpSecurityCsrfConfigurer ->
        // httpSecurityCsrfConfigurer.disable());

        // http.headers(httpSecurityHeadersConfigurer ->
        // httpSecurityHeadersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin).disable());
        // EDIT: used the same type as shown in the course itself

        /*
         * The following code is depreciated after spring security 3 and is subjected to
         * removal.
         * Use the above snippet to permit the use of db-console
         * https://www.baeldung.com/spring-security-jdbc-authentication
         * httpSecurity.csrf().disable();
         * httpSecurity.headers().frameOptions().disable();
         */

        return http.build();
    }
}
