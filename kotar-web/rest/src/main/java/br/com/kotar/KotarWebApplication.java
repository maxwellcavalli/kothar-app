package br.com.kotar;

import java.util.List;
import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module.Feature;

import br.com.kotar.core.jackson.EmptyToNullStringDeserializer;
import br.com.kotar.web.configuration.JwtFilter;
import br.com.kotar.web.repository.ProdutoRepository;

@EnableWebMvc
@EnableAutoConfiguration(exclude = RepositoryRestMvcAutoConfiguration.class)
@ComponentScan
@PropertySource("application.properties")
@EnableJpaRepositories
@EnableCaching(mode = AdviceMode.PROXY)
@EnableScheduling
@EnableAsync
public class KotarWebApplication extends WebMvcConfigurerAdapter {

//	@Autowired
//	ProdutoRepository produtoRepository;

	public static void main(String[] args) {
		SpringApplication.run(KotarWebApplication.class, args);
	}

	@Bean
	public FilterRegistrationBean jwtFilter() {
		final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
		registrationBean.setFilter(new JwtFilter());
		registrationBean.addUrlPatterns("/api/secure/*");

		return registrationBean;
	}

	@Bean
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("MapLookup-");
        executor.initialize();
        return executor;
    }

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {

		Hibernate5Module module = new Hibernate5Module();
		module.disable(Feature.USE_TRANSIENT_ANNOTATION);
		module.disable(Feature.FORCE_LAZY_LOADING);

		Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
		builder.featuresToEnable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
		builder.featuresToEnable(SerializationFeature.INDENT_OUTPUT);
		builder.deserializerByType(String.class, new EmptyToNullStringDeserializer());
		builder.modulesToInstall(module);

		converters.add(new MappingJackson2HttpMessageConverter(builder.build()));
	}

//	@Override
//	public void addViewControllers(ViewControllerRegistry registry) {
//
//		registry.addRedirectViewController("/documentation/v2/api-docs", "/v2/api-docs?group=restful-api");
//		registry.addRedirectViewController("/swagger-resources/configuration/ui", "/swagger-resources/configuration/ui");
//		registry.addRedirectViewController("/swagger-resources/configuration/security", "/swagger-resources/configuration/security");
//		registry.addRedirectViewController("/swagger-resources", "/swagger-resources");
//	}

//	@Override
//	public void addResourceHandlers(ResourceHandlerRegistry registry) {
//		registry.addResourceHandler("/swagger-ui.html**").addResourceLocations("classpath:/META-INF/resources/swagger-ui.html");
//		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/**");
//	}

//	@Override
//	public void addResourceHandlers(ResourceHandlerRegistry registry) {
//		super.addResourceHandlers(registry);
//		registry
//				.addResourceHandler("swagger-ui.html")
//				.addResourceLocations("classpath:/META-INF/resources/");
//
//		registry
//				.addResourceHandler("/webjars/**")
//				.addResourceLocations("classpath:/META-INF/resources/webjars/");
//	}



	// @Bean
	// public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
	// return new Jackson2ObjectMapperBuilderCustomizer() {
	// @Override
	// public void customize(Jackson2ObjectMapperBuilder builder) {
	// builder.dateFormat(new ISO8601DateFormat());
	// }
	// };
	// }

}
