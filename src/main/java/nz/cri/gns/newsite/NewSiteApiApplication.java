package nz.cri.gns.newsite;

/**
 *
 * @author sitikond
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.annotation.Bean;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;

@SpringBootApplication
public class NewSiteApiApplication {
    private static final Logger logger = LoggerFactory.getLogger(NewSiteApiApplication.class);

    public static void main(String[] args) {
		SpringApplication.run(NewSiteApiApplication.class, args);
                logger.info("New Site API");
	}
    
//    @Bean
//    public Docket productApi() {
//      return new Docket(DocumentationType.SWAGGER_2).select()
//         .apis(RequestHandlerSelectors.basePackage("nz.cri.gns.newsite")).build();
//    }

}
