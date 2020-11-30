package main;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class MvcConfig implements WebMvcConfigurer{

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("/resources/", "classpath:/static/", "classpath:/templates/");

        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:///E:/github/DiplomSkillbox/Diplom/upload/");
        registry.addResourceHandler("/post/upload/**")
                .addResourceLocations("file:///E:/github/DiplomSkillbox/Diplom/upload/");

        registry.addResourceHandler("/avatars/**")
                .addResourceLocations("file:///E:/github/DiplomSkillbox/Diplom/avatars/");
        registry.addResourceHandler("/posts/avatars/**")
                .addResourceLocations("file:///E:/github/DiplomSkillbox/Diplom/avatars/");
        registry.addResourceHandler("/post/avatars/**")
                .addResourceLocations("file:///E:/github/DiplomSkillbox/Diplom/avatars/");
    }
}
