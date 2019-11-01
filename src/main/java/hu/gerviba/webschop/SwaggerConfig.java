package hu.gerviba.webschop;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Profile("docs")
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket defaultGroup() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .apiInfo(defaultData())
                .forCodeGeneration(true);
    }
    
    @Bean
    public Docket pagesGroup() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("Pages")
                .apiInfo(pagesData())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.regex("(/circle/.*)|(/items/.*)|(/profile/.*)|(/configure/.*)|(/admin/.*)|(/provider/.*)|(/)"))
                .build()
                .forCodeGeneration(true);
    }

    @Bean
    public Docket apiGroup() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("API")
                .apiInfo(apiData())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.regex("(/api/.*)|(/test/.*)|(/login)|(/logout)|(/loggedin)"))
                .build()
                .forCodeGeneration(true);
    }

    private ApiInfo defaultData() {
        return new ApiInfoBuilder()
                .title("Schpincér API Documentation")
                .description("Documentation of both RestAPI and Pages endpoints")
                .version("1.0.0")
                .build();
    }
    
    private ApiInfo apiData() {
        return new ApiInfoBuilder()
                .title("Schpincér RestAPI Documentation")
                .description("List of RestAPI endpoints")
                .version("1.0.0")
                .build();
    }
    
    private ApiInfo pagesData() {
        return new ApiInfoBuilder()
                .title("Schpincér Pages Documentation")
                .description("List of web pages")
                .version("1.0.0")
                .build();
    }

}
