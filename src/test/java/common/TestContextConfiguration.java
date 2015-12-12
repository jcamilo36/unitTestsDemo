package common;

import com.mycompany.Application;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Test configuration.
 * Created by jcortes on 12/9/15.
 */
@Configuration
@ComponentScan(basePackages = { "com.mycompany", "common" })
@Import({Application.class})
@PropertySources({
        @PropertySource("classpath:config/test.properties")
})
public class TestContextConfiguration {


    /**
     * @return Bean to resolve properties
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertiesResolver() {
        PropertySourcesPlaceholderConfigurer configurer =
                new PropertySourcesPlaceholderConfigurer();
        return configurer;
    }
}
