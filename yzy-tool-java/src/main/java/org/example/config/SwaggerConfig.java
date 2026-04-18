package org.example.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableSwaggerBootstrapUi;
import org.example.config.token.JxToken;
import org.example.config.web3.tag.Web3NodeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 杨镇宇
 * @date 2024/6/13 15:31
 * @version 1.0
 */
@Configuration
@EnableSwagger2
@EnableSwaggerBootstrapUi
public class SwaggerConfig {
private static final Logger log = LoggerFactory.getLogger(SwaggerConfig.class);

    public SwaggerConfig() {
        log.info("===================集成Swagger2配置===================");
    }

    /**
     * 创建API
     */
    @Bean
    public Docket createRestApi() {
        List<Parameter> pars = new ArrayList<>();

        ParameterBuilder tokenPar = new ParameterBuilder();
        tokenPar.name(JxToken.ACCESS_AUTHORIZATION).description("Access 令牌").modelRef(new ModelRef("string")).parameterType("header").required(false);
        ParameterBuilder tokenPar1 = new ParameterBuilder();
        tokenPar1.name(JxToken.REFRESH_AUTHORIZATION).description("Refresh 令牌").modelRef(new ModelRef("string")).parameterType("header").required(false);

        ParameterBuilder tokenPar2 = new ParameterBuilder();
        tokenPar2.name(Web3NodeUtils.TAG_TYPE).description("智能合约交互类型").modelRef(new ModelRef("string")).parameterType("header").required(false);

        ParameterBuilder tokenPar3 = new ParameterBuilder();
        tokenPar3.name(Web3NodeUtils.TENANT_TAG).description("智能合约链路标签").modelRef(new ModelRef("string")).parameterType("header").required(false);

        pars.add(tokenPar.build());
        pars.add(tokenPar1.build());
        pars.add(tokenPar2.build());
        pars.add(tokenPar3.build());
        return new Docket(DocumentationType.SWAGGER_2)
                // 详细定制
                .apiInfo(apiInfo())
                .globalOperationParameters(pars)
                .select()
                // 指定当前包路径
                .apis(RequestHandlerSelectors.basePackage("org.example"))
                // 扫描所有 .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }
    /**
     * 添加摘要信息
     */
    private ApiInfo apiInfo() {
        // 用ApiInfoBuilder进行定制
        return new ApiInfoBuilder()
                .title("接口文档")
                .description("描述：接口")
                .version("版本号:1.0.0" )
                .contact(new Contact("yangzhenyu", "", "xxx@ccbscf.com"))
                //http://127.0.0.1:8089/doc.html
                .termsOfServiceUrl("http://{ip}:{port}/swagger-ui.html")
                .build();
    }

}
