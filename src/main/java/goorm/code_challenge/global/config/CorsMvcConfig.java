package goorm.code_challenge.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsMvcConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry corsRegistry) {

		corsRegistry.addMapping("/**")
			.allowedOrigins("https://localhost:3000")
			.allowedMethods("GET", "POST", "PUT", "DELETE")
			.allowedHeaders("access","Authorization" ,"Content-Type")
			.exposedHeaders("access","Authorization")
			.allowCredentials(true);
	}
}
