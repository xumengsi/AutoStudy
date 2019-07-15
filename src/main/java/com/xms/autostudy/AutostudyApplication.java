package com.xms.autostudy;

import com.xms.autostudy.configuration.RuleConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.web.client.RestTemplate;

@Order(Integer.MAX_VALUE - 1)
@SpringBootApplication
@EnableConfigurationProperties({RuleConfiguration.class})
public class AutostudyApplication implements CommandLineRunner {

    @Autowired
    private RuleConfiguration ruleConfiguration;

    public static void main(String[] args) {
        SpringApplication.run(AutostudyApplication.class, args);
    }

    @Bean
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }

    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     * @throws Exception on error
     */
    @Override
    public void run(String... args) throws Exception {
        System.setProperty("webdriver.chrome.driver", ruleConfiguration.getChromeDriverAddress());
    }
}
