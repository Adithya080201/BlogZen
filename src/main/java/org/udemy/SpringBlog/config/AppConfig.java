package org.udemy.SpringBlog.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

//This is used to generate email for resetting password
@Configuration
public class AppConfig {    

    @Value("${mail.transport.protocol}")
    private String mailTransportProtocol;

    @Value("${spring.mail.host}")
    private String springMailHost;

    @Value("${spring.mail.port}")
    private String springMailPort;

    @Value("${spring.mail.username}")
    private String springMailUsername;

    @Value("${spring.mail.password}")
    private String springMailPassword;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private String mailSmtpAuth;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private String mailSmtpAuthStarttlsEnable;

    @Value("${spring.mail.smtp.ssl.trust}")
    private String mailSmtpSslTrust;

    @Bean
    public JavaMailSender getJavaMailSender(){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(springMailHost);
        mailSender.setPort(Integer.parseInt(springMailPort));

        mailSender.setUsername(springMailUsername);
        mailSender.setPassword(springMailPassword);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", mailTransportProtocol);
        props.put("mail.smtp.auth", mailSmtpAuth);
        props.put("mail.smtp.starttls.enable", mailSmtpAuthStarttlsEnable);
        props.put("mail.debug", "true");
        props.put("mail.smtp.ssl.trust", mailSmtpSslTrust);

        return mailSender;
    }
    
}
