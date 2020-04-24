package br.com.kotar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
//import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

//@EnableWebMvc
//@EnableAutoConfiguration(exclude = RepositoryRestMvcAutoConfiguration.class)
//@ComponentScan
//@PropertySource("application.properties")
//@EnableJpaRepositories
@EnableCaching(mode = AdviceMode.PROXY)
@EnableScheduling

@SpringBootApplication//(exclude = {EmbeddedServletContainerAutoConfiguration.class, WebMvcAutoConfiguration.class})
public class KotarSchedulerApplication /* extends WebMvcConfigurerAdapter */ {

	public static void main(String[] args) {
		SpringApplication.run(KotarSchedulerApplication.class, args);
	}

}
